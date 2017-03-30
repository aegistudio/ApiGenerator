package net.aegistudio.api.javagen;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;

import net.aegistudio.api.Document;
import net.aegistudio.api.Method;
import net.aegistudio.api.Method.Parameter;
import net.aegistudio.api.Namespace;
import net.aegistudio.api.gen.IndentPrintStream;
import net.aegistudio.api.gen.Interfacing;
import net.aegistudio.api.gen.SymbolTable;
import net.aegistudio.api.gen.TypeTable;

/**
 * Generate local objects.
 * 
 * @author aegistudio
 */

public class JavaLocalGenerator extends JavaPerspectGenerator<Interfacing> {
	protected final Function<Document, Interfacing[]> mapping;
	public JavaLocalGenerator(Function<Document, Interfacing[]> mapping) {
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
			Namespace namespace, IndentPrintStream localPrint) throws IOException {
		
		String[] invokeMethods = Arrays.stream(perspect.methods())
				.map(Method::name).map(methodName -> {
					String heading = methodName.substring(0, 1);
					String tailing = methodName.length() > 1? 
							methodName.substring(1) : "";
					
					return "invoke" + heading.toUpperCase() + tailing;
				})
				.toArray(String[]::new);
		
		// Java file header.
		super.javaHeader(localPrint, namespace);
		
		// Java class body begins.
		localPrint.println("public abstract class <type> extends <parent> {"
				.replace("<type>", perspect.name())
				.replace("<parent>", perspect.host()? "ApiHost" : "ApiLocal"));
		localPrint.push();
		
		// Generate host or local object.
		if(perspect.host()) {
			// Super class constructor
			localPrint.println("public <type>(Connection.Factory factory) {"
					.replace("<type>", perspect.name()));
			localPrint.push();
			localPrint.println("super(factory);");
			localPrint.pop();
			localPrint.println("}");
		}
		else {
			// Deserializer.
			localPrint.println(("public static <type> read(DataInputStream dataInputStream, "
					+ "ApiHost apiHost) throws IOException, ApiException {")
					.replace("<type>", perspect.name()));
			localPrint.push();
			localPrint.println("return ApiLocal.read(dataInputStream, apiHost, <type>.class);"
					.replace("<type>", perspect.name()));
			localPrint.pop();
			localPrint.println("}");
			localPrint.println();
			
			// Serializer.
			localPrint.println(("public static void write(<type> value, "
					+ "DataOutputStream dataOutputStream, "
					+ "ApiHost apiHost) throws IOException, ApiException {")
					.replace("<type>", perspect.name()));
			localPrint.push();
			localPrint.println("ApiLocal.write(value, dataOutputStream, apiHost);");
			localPrint.pop();
			localPrint.println("}");
		}
		localPrint.println();
		
		// Invoke methods construct.
		Method[] methods = perspect.methods();
		for(int i = 0; i < invokeMethods.length; i ++) {
			Method method = methods[i];
			String invokeMethod = invokeMethods[i];
			
			// Construct the signature for the underlying invoke method.
			Parameter[] parameters = method.parameters();
			String[] parameterNameList = Arrays.stream(parameters)
					.map(Parameter::name)
					.map(name -> "_" + name)
					.toArray(String[]::new);
			TypeTable.Result[] parameterTypeList = Arrays.stream(parameters)
					.map(Parameter::type)
					.map(typeTable::convertType)
					.toArray(TypeTable.Result[]::new);
			
			TypeTable.Result returnValue = typeTable
					.convertType(method.result());
			
			StringBuilder underlyingSignature = new StringBuilder(
					"public abstract <returnType> <methodName>("
						.replace("<returnType>", returnValue.name(namespace))
						.replace("<methodName>", method.name()));
			for(int j = 0; j < parameters.length; j ++) {
				if(j > 0) underlyingSignature.append(", ");
				underlyingSignature.append(parameterTypeList[j].name(namespace));
				underlyingSignature.append(" ");
				underlyingSignature.append(parameterNameList[j]);			
			}
			underlyingSignature.append(") throws ApiException;");
			localPrint.println(new String(underlyingSignature));
			localPrint.println();
			
			// Construct the skeleton body of invoke method.
			localPrint.println(("private void <invokeMethod>(ApiHost apiHost, "
					+ "DataInputStream dataInputStream, DataOutputStream dataOutputStream) "
					+ "throws IOException, ApiException {")
					.replace("<invokeMethod>", invokeMethod));
			localPrint.push();
			

			// Construct the parameter parser.
			for(int j = 0; j < parameters.length; j ++) 
				readSerial.serialize(localPrint, namespace, "dataInputStream", 
						perspect.host()? "this" : "apiHost", 
						parameterTypeList[j].name(namespace) + " " + parameterNameList[j], 
						parameterTypeList[j], symbolTable.lookup(parameters[j].type()));
			
			StringBuilder paramListBuilder = new StringBuilder();
			for(int j = 0; j < parameterNameList.length; j ++) {
				if(j > 0) paramListBuilder.append(", ");
				paramListBuilder.append(parameterNameList[j]);
			}
			
			String invocation = "<methodName>(<paramList>);"
					.replace("<methodName>", method.name())
					.replace("<paramList>", new String(paramListBuilder));
			
			if(method.result() != null) {
				localPrint.println(returnValue.name(namespace) + " result = " + invocation);
				writeSerial.serialize(localPrint, namespace, "dataOutputStream", 
						perspect.host()? "this" : "apiHost", "result", returnValue, 
						symbolTable.lookup(method.result()));
			}
			else localPrint.println(invocation);
			
			localPrint.pop();
			localPrint.println("}");
			localPrint.println();
		}
		
		// Call table construct.
		localPrint.println("private final Facade[] callTable = new Facade[] {");
		localPrint.push();
		for(int i = 0; i < invokeMethods.length; i ++) {
			String invokeLine = "this::<invokeMethod>"
					.replace("<invokeMethod>", invokeMethods[i]);
			if(i != invokeMethods.length - 1) invokeLine += ", ";
			localPrint.println(invokeLine);
		}
		localPrint.pop();
		localPrint.println("};");
		localPrint.println();
		
		localPrint.println("public Facade[] callTable() { return callTable; }");
		
		// End of this file.
		localPrint.pop();
		localPrint.println("}");
	}
}
