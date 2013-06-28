package com.sutil.rango.libs.restapi;

/**
 * Class definition for a callback to be invoked when the HTTP request
 * representing the REST API Call completes.
 */
public abstract class RestTaskCallback{
	
	/**
     * Called before the HTTP request starts.
     * 
     */
	public void preTaskExecute() {};
	
    /**
     * Called when the HTTP request completes.
     * 
     * @param response The result of the HTTP request.
     */
    public abstract void onTaskComplete(String response);
}
