package com.sutil.rango.libs.restapi;

/**
 * 
 * Class definition for a callback to be invoked when the response for the data 
 * submission is available.
 * 
 */
public abstract class DeleteCallback {
    /**
     * Called when a DELETE success response is received. <br/>
     * This method is guaranteed to execute on the UI thread.
     */
    public abstract void onDeleteSuccess(String response);
    
    public abstract void onPreDelete();

}
