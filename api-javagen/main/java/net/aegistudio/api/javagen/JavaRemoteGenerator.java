package net.aegistudio.api.javagen;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;

import net.aegistudio.api.Document;
import net.aegistudio.api.Method;
import net.aegistudio.api.Namespace;
import net.aegistudio.api.Method.Parameter;
import net.aegistudio.api.gen.IndentPrintStream;
import net.aegistudio.api.gen.Interfacing;
import net.aegistudio.api.gen.SymbolTable;
import net.aegistudio.api.gen.TypeTable;

public class JavaRemoteGenerator extends JavaPerspectGenerator<Interfacing> {
	protected final Function<Document, Interfacing[]> mapping;
	public JavaRemoteGenerator(Function<Document, Interfacing[]> mapping) {
		this.mapping = mapping;
	}
	
	protected Interfacing[] perspect(Document document) {
		return mapping.apply(document);
	}
	
	@Override
	protected String name(Interfacing perspect) {
		return perspect.name();
	}
	
	@Override
	protected void construct(Interfacing perspect, SymbolTable symbolTable, 
			Namespace namespace, IndentPrintStream remotePrint) throws IOException {
		
		super.javaHeader(remotePrint, namespace);

		// Java class body begins.
		remotePrint.println("public final class <type> extends <parent> {"
				.replace("<type>", perspect.name())
				.replace("<parent>", perspect.host()? "ApiHost" : "ApiRemote"));
		remotePrint.push();
		
		// Generate host or local object.
		if(perspect.host()) {
			// Super class constructor.
			remotePrint.println("public <type>(Connection.Factory factory) {"
					.replace("<type>", perspect.name()));
			remotePrint.push();
			remotePrint.println("super(factory);");
			remotePrint.pop();
			remotePrint.println("}");
		}
		else {
			// Super class constructor.
			remotePrint.println("private <type>(ApiHost host, int objectId) {"
					.replace("<type>", perspect.name()));
			remotePrint.push();
			remotePrint.println("super(host, objectId);");
			remotePrint.pop();
			remotePrint.println("}");
			remotePrint.println();
			
			// Deserializer.
			remotePrint.println(("public static <type> read(DataInputStream dataInputStream, "
					+ "ApiHost apiHost) throws IOException, ApiException {")
					.replace("<type>", perspect.name()));
			remotePrint.push();
			remotePrint.println("return ApiRemote.read(dataInputStream, apiHost, <type>::new);"
					.replace("<type>", perspect.name()));
			remotePrint.pop();
			remotePrint.println("}");
			remotePrint.println();
			
			// Serializer.
			remotePrint.println(("public static void write(<type> value, "
					+ "DataOutputStream dataOutputStream, "
					+ "ApiHost apiHost) throws IOException, ApiException {")
					.replace("<type>", perspect.name()));
			remotePrint.push();
			remotePrint.println("ApiRemote.write(value, dataOutputStream, apiHost);");
			remotePrint.pop();
			remotePrint.println("}");
		}
		
		// Generate stub methods.
		Method[] methods = perspect.methods();
		for(int i = 0; i < methods.length; i ++) {
			remotePrint.println();
			Method method = methods[i];
			
			// Construct the signature for the stub method.
			Parameter[] parameters = method.parameters();
			String[] parameterNameList = Arrays.stream(parameters)
					.map(Parameter::name)
					.map(name -> "_" + name)
					.toArray(String[]::new);
			TypeTable.Result[] parameterTypeList = Arrays.stream(parameters)
					.map(Parameter::type)
					.map(typeTable::convertType)
					.toArray(TypeTable.Result[]::new);
			
			boolean isVoid = method.result() == null;
			TypeTable.Result returnValue = typeTable
					.convertType(method.result());
			
			StringBuilder underlyingSignature = new StringBuilder(
					"public <returnType> <methodName>("
						.replace("<returnType>", returnValue.name(namespace))
						.replace("<methodName>", method.name()));
			for(int j = 0; j < parameters.length; j ++) {
				if(j > 0) underlyingSignature.append(", ");
				underlyingSignature.append(parameterTypeList[j].name(namespace));
				underlyingSignature.append(" ");
				underlyingSignature.append(parameterNameList[j]);			
			}
			underlyingSignature.append(") throws ApiException {");
			remotePrint.println(new String(underlyingSignature));
			remotePrint.push();
			
			// Body of stub method.
			remotePrint.println((!isVoid? "return " : "")
					+ host(perspect) + ".call(" 
					+ handle(perspect) + ", " + i + ", (dataOutputStream) -> {");
			
			// Serialize parameters.
			remotePrint.push();
			for(int j = 0; j < parameters.length; j ++) {
				writeSerial.serialize(remotePrint, namespace, 
						"dataOutputStream", host(perspect), 
						parameterNameList[j], parameterTypeList[j], 
						symbolTable.lookup(parameters[j].type()));
			}
			remotePrint.pop();
			
			// Deserialize result if any.
			if(!isVoid) {
				remotePrint.println("}, (dataInputStream) -> {");
				remotePrint.push();
				readSerial.serialize(remotePrint, namespace, "dataInputStream", 
						host(perspect), "<type> result", 
						returnValue, symbolTable.lookup(method.result()));
				remotePrint.println("return result;");
				remotePrint.pop();
			}
			remotePrint.println("});");
			
			// End of stub method.
			remotePrint.pop();
			remotePrint.println("}");
		}
		
		// End of this file.
		remotePrint.pop();
		remotePrint.println("}");
	}
	
	private String host(Interfacing perspect) {
		return perspect.host()? "this" : "host";
	}
	
	private String handle(Interfacing perspect) {
		return perspect.host()? "0" : "handle";
	}
}