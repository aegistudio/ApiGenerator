package net.aegistudio.api.cppgen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

import net.aegistudio.api.Document;
import net.aegistudio.api.Method;
import net.aegistudio.api.Method.Parameter;
import net.aegistudio.api.Namespace;
import net.aegistudio.api.Type;
import net.aegistudio.api.gen.IndentPrintStream;
import net.aegistudio.api.gen.Interfacing;
import net.aegistudio.api.gen.SymbolTable;
import net.aegistudio.api.gen.TypeTable;

public class CppLocalGenerator extends CppPerspectGenerator<Interfacing> {
	protected final Function<Document, Interfacing[]> mapping;
	public CppLocalGenerator(boolean clientSide, 
			Function<Document, Interfacing[]> mapping) {
		super(clientSide);
		this.mapping = mapping;
	}

	@Override
	protected Interfacing[] perspect(Document document) {
		return mapping.apply(document);
	}

	@Override
	protected String name(Interfacing perspect) {
		return perspect.name();
	}

	@Override
	protected void construct(Interfacing interfacing, SymbolTable symbolTable, Namespace namespace,
			IndentPrintStream includePrinter, IndentPrintStream sourcePrinter) throws IOException {
		Method[] methods = interfacing.methods();
		String[] invokeMethods = Arrays.stream(methods)
				.map(Method::name)
				.map(name -> {
					String head = name.substring(0, 1);
					String tail = name.substring(1);
					return "invoke" + head.toUpperCase() + tail;
				}).toArray(String[]::new);
		
		// Profile Dependencies.
		List<Type> enrolledTypes = new ArrayList<>();
		Arrays.stream(interfacing.methods())
			.map(Method::result).filter(Objects::nonNull)
			.forEach(enrolledTypes::add);
		Arrays.stream(interfacing.methods())
			.map(Method::parameters)
			.map(Arrays::stream)
			.forEach(stream -> stream
					.map(Parameter::type)
					.forEach(enrolledTypes::add));
		
		Type[] dependencies = super.filterType(
				enrolledTypes.toArray(new Type[0]), symbolTable);

		super.openNamespace(namespace, includePrinter);
		
		includePrinter.println("// Dependency types.");
		Set<String> excludeSet = new TreeSet<>();
		Arrays.stream(dependencies)
			.map(Type::name).forEach(excludeSet::add);
		excludeSet.stream()
			.map(name -> "class " + name + ";")
			.forEach(includePrinter::println);;
		includePrinter.println();
		
		sourcePrinter.println("#include \"" 
				+ namespace.concatenate(interfacing.name(), "/") 
				+ ".h\"");
		excludeSet.stream()
			.map(name -> "#include \"" 
					+ namespace.concatenate(name, "/") 
					+ ".h\"")
			.forEach(sourcePrinter::println);

		sourcePrinter.println("using namespace " 
				+ namespace.concatenate("::") + ";");
		sourcePrinter.println();
		
		// Write the class.
		includePrinter.println("// Class definition.");
		if(interfacing.host()) {
			if(clientSide) throw new IllegalStateException(
					"Attempting to generate api server as local object.");
			
			includePrinter.println("class " + interfacing.name() + " : public api::ApiHost {");
			includePrinter.println("public:");
			includePrinter.push();
			
			String constructor = interfacing.name() + "(api::ConnectionFactory& factory, api::Platform& platform)";
			includePrinter.println(constructor + ";");
			includePrinter.println();
			
			sourcePrinter.println("// Construct the host from connection factory.");
			sourcePrinter.println(interfacing.name() + "::" + constructor + ":");
			sourcePrinter.println("\t\tapi::ApiHost(factory, platform) {}");
			sourcePrinter.println();
		}
		else {
			includePrinter.println("class " + interfacing.name() + " : public api::ApiLocal {");
			includePrinter.println("public:");
			includePrinter.push();
			
			// Constructor.
			includePrinter.println(interfacing.name() + "(int = 0)" + ";");
			includePrinter.println();
			
			sourcePrinter.println("// Invoke the null parameter constructor.");
			sourcePrinter.println(interfacing.name() + "::" 
					+ interfacing.name() + "(int _placeHolder):");
			sourcePrinter.println("\t\tapi::ApiLocal() {}");
			sourcePrinter.println();
			
			// Read serializer.
			String readMethod = "_EX(" + interfacing.name() + "*) <midfix>read(api::ApiHost& _host, \n" 
					+ "\tapi::InputStream& _inputStream)";
			includePrinter.println("static " + readMethod.replace("<midfix>", "") + ";");
			includePrinter.println();
			
			sourcePrinter.println("// Implement the read method.");
			sourcePrinter.println(readMethod.replace("<midfix>", interfacing.name() + "::") + " {");
			sourcePrinter.println();
			sourcePrinter.push();
			sourcePrinter.println("int32_t _address = _inputStream.readInt();");
			sourcePrinter.println("if(_address == 0) return NULL;");
			sourcePrinter.println();
			sourcePrinter.println("tryDeclare(ApiObject*, result, \n" 
					+ "\t_host.search(_address));");
			sourcePrinter.println("return reinterpret_cast<" + interfacing.name() + "*>(result);");
			sourcePrinter.pop();
			sourcePrinter.println("}");
			sourcePrinter.println();
			
			// Write serializer.
			String writeMethod = "_EX(void*) <midfix>write(" + interfacing.name() + "* _object, api::ApiHost& _host, \n"
					+ "\t api::OutputStream& _outputStream)";
			includePrinter.println("static " + writeMethod.replace("<midfix>", "") + ";");
			includePrinter.println();
			
			sourcePrinter.println("// Implement the write method.");
			sourcePrinter.println(writeMethod.replace("<midfix>", interfacing.name() + "::") + " {");
			sourcePrinter.push();
			sourcePrinter.println();
			
			sourcePrinter.println("if(_object == NULL) _outputStream.writeInt(0);");
			sourcePrinter.println("else {");
			sourcePrinter.push();
			sourcePrinter.println("int32_t _handle = _host.marshal(_object);");
			sourcePrinter.println("_outputStream.writeInt(_handle);");
			sourcePrinter.pop();
			sourcePrinter.println("}");
			
			sourcePrinter.pop();
			sourcePrinter.println("}");
			sourcePrinter.println();
		}
		
		// Invoke method skeleton.
		String invokeMethod = "_EX(void*) <midfix>invoke(int32_t callId, api::ApiHost& host, \n" +
					"\t\tapi::InputStream& inputStream, api::OutputStream& outputStream)";
		
		includePrinter.println("virtual " + invokeMethod
				.replace("<midfix>", "") + ";");
		includePrinter.println();
		
		sourcePrinter.println("// Invoke the unpacking method.");
		sourcePrinter.println(invokeMethod
				.replace("<midfix>", interfacing.name() + "::") + " {");
		sourcePrinter.println();
		
		sourcePrinter.push();
		sourcePrinter.println("switch(callId) {");
		sourcePrinter.push();
		for(int i = 0; i < invokeMethods.length; i ++) {
			sourcePrinter.println("case " + i + ":");
			sourcePrinter.println("\treturn " + invokeMethods[i] + "(host, inputStream, outputStream);");
			sourcePrinter.println();
		}
		sourcePrinter.println("default:");
		sourcePrinter.println("\treturn api::ApiLocal::invoke(callId, host, ");
		sourcePrinter.println("\t\tinputStream, outputStream);");
		sourcePrinter.pop();
		sourcePrinter.println("}");
		
		sourcePrinter.pop();
		sourcePrinter.println("}");
		sourcePrinter.println();
		
		// Invoke method stubs.
		for(int m = 0; m < methods.length; m ++) {
			Method method = methods[m];
			super.methodSignature("virtual ", "", " = 0;", symbolTable, 
					namespace, method, includePrinter);
			includePrinter.println();
		}
		
		includePrinter.pop();
		includePrinter.println("private:");
		includePrinter.push();
		
		for(int m = 0; m < methods.length; m ++) {
			Method method = methods[m];
			if(m > 0) includePrinter.println();
			String invokeSignature = "_EX(void*) <prefix>" + invokeMethods[m] + "(api::ApiHost& host, " 
					+ "\n\tapi::InputStream& inputStream, api::OutputStream& outputStream)";
			includePrinter.println(invokeSignature.replace("<prefix>", "") + ";");
			
			// The real unpacker call method.
			String[] parameterName = Arrays
					.stream(method.parameters())
					.map(Parameter::name)
					.map("_"::concat)
					.toArray(String[]::new);
			Type[] parameterType = Arrays
					.stream(method.parameters())
					.map(Parameter::type)
					.toArray(Type[]::new);
			
			sourcePrinter.println("// Implement delegator " + method.name() + ".");
			sourcePrinter.println(invokeSignature
					.replace("<prefix>", interfacing.name() + "::") + " {");
			sourcePrinter.push();
			sourcePrinter.println();
			
			sourcePrinter.println("// Collect parameters for " + method.name() + ".");
			for(int i = 0; i < parameterName.length; i ++) 
				super.namedIoMethod(symbolTable, parameterType[i], 
						parameterName[i], readSerializer, "inputStream", 
						namespace, sourcePrinter, "host");
			sourcePrinter.println();
			
			StringBuilder invocationMethod = new StringBuilder();
			invocationMethod.append(method.name());
			invocationMethod.append("(");
			for(int i = 0; i < parameterName.length; i ++) {
				if(i > 0) invocationMethod.append(", ");
				invocationMethod.append(parameterName[i]);
			}
			invocationMethod.append(")");
			
			sourcePrinter.println("// Invoke delegated method " + method.name() + ".");
			if(method.result() == null) {
				sourcePrinter.println("tryDeclare(void*, result, ");
				sourcePrinter.println("\t" + new String(invocationMethod) + ");");
				sourcePrinter.println();
			}
			else {
				TypeTable.Result resultType = typeTable
						.convertType(method.result());
				SymbolTable.Class resultClazz = symbolTable
						.lookup(method.result());
				
				sourcePrinter.println("tryDeclare(" + resultType.name(resultClazz, namespace) + ", result, ");
				sourcePrinter.println("\t" + new String(invocationMethod) + ");");
				sourcePrinter.println();
				
				sourcePrinter.println("// Collect result of " + method.name() + ".");
				super.ioMethod(symbolTable, namespace, writeSerializer, 
						"outputStream", "host", sourcePrinter, 
						new Type[] { method.result() } , new String[] { "result" });
			}
			sourcePrinter.println("return NULL;");
			sourcePrinter.pop();
			sourcePrinter.println("}");
			sourcePrinter.println();
		}
		
		includePrinter.pop();
		includePrinter.println("};");
		includePrinter.println();
		
		super.closeNamespace(namespace, includePrinter);
	}
}