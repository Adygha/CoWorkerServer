/**
 * 
 */
package view;

/**
 * A class that is responsible for the translation of the raw string response to an Phone DATA response that the client's phone can understand.
 * @author Janty Azmat
 */
class ViewDataResponse implements IViewResponse {

	public ViewDataResponse(String rawResponse) {
	}

	@Override
	public byte[] getResponseData() { // Not yet implemented since the project will represent the HTML part only (but this is in the design)
		return null;
	}
}
