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

public class CppRemoteGenerator extends CppPerspectGenerator<Interfacing> {
	protected final Function<Document, Interfacing[]> mapping;
	public CppRemoteGenerator(boolean clientSide, 
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
		
		// Profile read/write method.
		String readMethod = "_EX(" + interfacing.name() 
			+ ") <midfix>read(api::ApiHost& _host, \n" 
			+ "\t\tapi::InputStream& _inputStream)";
		String writeMethod = "void <midfix>write(" + interfacing.name() 
			+ "& _object, api::ApiHost& _host, \n"
			+ "\t\tapi::OutputStream& _outputStream)";
		
		// Write the class.
		includePrinter.println("// Class definition.");
		if(interfacing.host()) {
			if(!clientSide) throw new IllegalStateException(
					"Attempting to generate api server as remote object.");
			
			includePrinter.println("class " + interfacing.name() + " : public api::ApiHost {");
			includePrinter.println("public:");
			includePrinter.push();
			
			String constructor = interfacing.name() + "(api::ConnectionFactory& factory, api::Platform& platform)";
			includePrinter.println(constructor + ";");
			
			sourcePrinter.println("// Construct the host from connection factory.");
			sourcePrinter.println(interfacing.name() + "::" + constructor + ":");
			sourcePrinter.println("\t\tapi::ApiHost(factory, platform) {}");
			sourcePrinter.println();
		}
		else {
			includePrinter.println("class " + interfacing.name() + " : public api::ApiRemote {");
			includePrinter.println("public:");
			includePrinter.push();
			
			// Constructor.
			includePrinter.println(interfacing.name() + "(int = 0)" + ";");
			includePrinter.println();
			
			sourcePrinter.println("// Invoke the null parameter constructor.");
			sourcePrinter.println(interfacing.name() + "::" 
					+ interfacing.name() + "(int _placeHolder):");
			sourcePrinter.println("\t\tapi::ApiRemote() {}");
			sourcePrinter.println();
			
			// Read method.
			includePrinter.println("static " + readMethod.replace("<midfix>", "") + ";");
			includePrinter.println();
			
			sourcePrinter.println("// Implement the read method.");
			sourcePrinter.println(readMethod.replace("<midfix>", interfacing.name() + "::") + "{");
			sourcePrinter.push();
			sourcePrinter.println(interfacing.name() + " result;");
			sourcePrinter.println("result.api::ApiRemote::read(_host, _inputStream);");
			sourcePrinter.println("return result;");
			sourcePrinter.pop();
			sourcePrinter.println("}");
			sourcePrinter.println();

			// Write method.
			includePrinter.println("static " + writeMethod.replace("<midfix>", "") + ";");
			
			sourcePrinter.println("// Implement the write method.");
			sourcePrinter.println(writeMethod.replace("<midfix>", interfacing.name() + "::") + "{");
			sourcePrinter.println("\t_object.api::ApiRemote::write(_host, _outputStream);");
			sourcePrinter.println("}");
			sourcePrinter.println();
		}
		
		Method[] methods = interfacing.methods();
		for(int m = 0; m < methods.length; m ++) {
			Method method = methods[m];
			includePrinter.println();
			super.methodSignature("", "", ";", symbolTable, 
					namespace, method, includePrinter);
			
			// The real continuation call method.
			String[] parameterName = Arrays
					.stream(method.parameters())
					.map(Parameter::name)
					.map("_"::concat)
					.toArray(String[]::new);
			Type[] parameterType = Arrays
					.stream(method.parameters())
					.map(Parameter::type)
					.toArray(Type[]::new);
			
			sourcePrinter.println("// Implement continuation " + method.name() + ".");
			super.methodSignature("", interfacing.name() + "::", "{", 
					symbolTable, namespace, method, sourcePrinter);
			sourcePrinter.push();
			
			sourcePrinter.println("api::BufferOutputStream output;");
			for(int i = 0; i < parameterName.length; i ++) 
				super.ioMethod(symbolTable, namespace, writeSerializer, "output", 
						interfacing.host()? "*this" : "*host", 
						sourcePrinter, parameterType, parameterName);
			sourcePrinter.println();
			
			sourcePrinter.println("api::variant<int8_t> callData(");
			sourcePrinter.println("\toutput.size(), output.clone());");
			sourcePrinter.println();
			
			sourcePrinter.println("tryDeclare(api::variant<int8_t>, callResult, ");
			sourcePrinter.println("\tthis -> " + (interfacing.host()? 
					"call(0, " : "call(") + m + ", callData));");
			sourcePrinter.println();
			
			if(method.result() == null)
				sourcePrinter.println("return NULL;");
			else {
				sourcePrinter.println("api::BufferInputStream input(");
				sourcePrinter.println("\tcallResult.length(), *callResult);");
				sourcePrinter.println();
				
				super.namedIoMethod(symbolTable, method.result(), 
						"result", readSerializer, "input", 
						namespace, sourcePrinter, 
						interfacing.host()? "*this" : "*host");
				sourcePrinter.println("return result;");
			}
			
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