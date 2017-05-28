/**
 * 
 */
package view;

/**
 * @author Janty Azmat
 */
class ViewHtmlRequest implements IViewRequest {
	// Constants
	private static final String me_HTML_PREFIX = "HTML_";
	// Fields
	private String meRawReq; // The request in the raw format
	private boolean meIsDirect; // a field to specify if this request needs a direct response without the need of processing

	public ViewHtmlRequest(String reqestData) {
		String tmpArr[] = reqestData.split(" ", 3);
		String httpVerCheck[] = tmpArr[2].split("\n", 2);
		if(!"HTTP/1.1".equals(httpVerCheck[0].trim())){ // Check for HTTP version (only 1.1 is accepted)
			this.meIsDirect = true;
			if (httpVerCheck[0].trim().startsWith("HTTP/")) {
				this.meRawReq = "WRONG_HTTP";
			} else {
				this.meRawReq = "BAD";
			}
		} else if (tmpArr[0].startsWith(me_HTML_PREFIX)) { // If it is a CoWorker application specific HTML request
			this.meRawReq = reqestData.substring(me_HTML_PREFIX.length());
		} else { // If this is the initial connection requesting a page (or an erroneous request)
			if (!tmpArr[0].equals("GET") || tmpArr[1].length() != 37)
				this.meIsDirect = true;
			switch (tmpArr[0]) {
				case "GET": // This is the case of visiting the server's site for the initial time
					if (tmpArr[1].length() == 37) // The case of UUID (the slash char + 36 for UUID chars)
						this.meRawReq = "UPDATE_USER " + tmpArr[1] + " " + tmpArr[2];
					else // The case of init page or favicon.ico
						this.meRawReq = tmpArr[0] + " " + tmpArr[1]; // We don't need to add more info
					break;
				case "PUT":
				case "POST":
				case "DELETE":
				case "OPTIONS":
				case "HEAD":
				case "TRACE":
				case "CONNECT":
					this.meRawReq = "NOT_IMP";
					break;
				default:
					this.meRawReq = "BAD";
			}
		}
	}

	@Override
	public String getRawRequest() {
		return this.meRawReq;
	}

	@Override
	public boolean getIsDirect() {
		return meIsDirect;
	}
}
