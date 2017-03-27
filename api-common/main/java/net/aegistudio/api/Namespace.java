package net.aegistudio.api;

public class Namespace {
	protected final String[] namespace;
	public Namespace(String[] namespace) {
		this.namespace = namespace;
	}
	
	public Namespace(String namespaceString) {
		this(namespaceString.split("[.]"));
	}
	
	public String[] namespace() {
		return namespace;
	}
	
	public int depth() {
		return namespace.length;
	}
	
	public String concatenate(String separator) {
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < namespace.length; i ++) {
			if(i > 0) builder.append(separator);
			builder.append(namespace[i]);
		}
		return new String(builder);
	}
	
	public String concatenate(String type, String separator) {
		String former = concatenate(separator);
		return former.length() == 0? type : former + separator + type;
	}
}
