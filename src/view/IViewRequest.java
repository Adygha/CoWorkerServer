/**
 * 
 */
package view;

/**
 * An interface that represents a client's request
 * @author Janty Azmat
 */
interface IViewRequest {

	/**
	 * A method to get the raw request data.
	 * @return	the raw request data
	 */
	String getRawRequest();

	/**
	 * Checks if this request needs a direct response without the need of processing (like if it is a bad request).
	 * @return	true if this request needs a direct response
	 */
	boolean getIsDirect();
}
