/**
 * 
 */
package view;

/**
 * A class that is responsible for the translation of the PHONE data request to a raw string request that the model can understand.
 * @author Janty Azmat
 */
class ViewDataRequest implements IViewRequest {

	public ViewDataRequest(String reqestData) {
	}

	@Override
	public String getRawRequest() { // Not yet implemented since the project will represent the HTML part only (but this is in the design)
		return null;
	}

	@Override
	public boolean getIsDirect() { // Not yet implemented since the project will represent the HTML part only (but this is in the design)
		return false;
	}
}
