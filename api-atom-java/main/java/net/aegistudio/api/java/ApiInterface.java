package net.aegistudio.api.java;

public abstract class ApiInterface extends ApiObject {
	public abstract byte[] response(int call, 
			byte[] data) throws Exception;
}
