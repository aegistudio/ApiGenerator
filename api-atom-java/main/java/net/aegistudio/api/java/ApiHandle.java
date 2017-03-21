package net.aegistudio.api.java;

public class ApiHandle extends ApiObject {
	protected int objectId;
	protected ApiHost host;
	
	protected ApiHandle(int objectId) {
		this.objectId = objectId;
	}
	
	public int handle() {
		return objectId;
	}
}
