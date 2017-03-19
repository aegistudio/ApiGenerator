package net.aegistudio.api.java;

public abstract class ApiInterface extends ApiObject {
	public abstract void response(int call, 
			byte[] data) throws Exception;
}
