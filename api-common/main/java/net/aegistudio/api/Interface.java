package net.aegistudio.api;

/**
 * The most common objects.
 * 
 * Interface are what we could send messages to, and
 * we commonly call such kind of objects handles.
 * 
 * We need to distinguish whether the handle would
 * be defined as client side handle or server side
 * handle. A client side handle is always called a
 * callback, and a server side object is always called
 * interface.
 * 
 * @author aegistudio
 */

public interface Interface {
	public String name();
	
	public Method[] methods();
}
