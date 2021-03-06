/**
 * 
 */
package view;

import javax.xml.bind.JAXBException;

/**
 * @author Janty Azmat
 */
class ViewRequestResposeFactory {
	// A private enum to specify the type of the transaction
	private enum TransactionType { // Other types can be added here
		DATA,
		HTML
	}

	// Fields
	private String meReq;
	private TransactionType meType;

	public ViewRequestResposeFactory(byte[] theRequest) {
		this.meReq = new String(theRequest);
		if (this.meReq.startsWith("DATA_")) { // Here, we classify the transaction type
			this.meType = TransactionType.DATA; // The type is DATA (for clients like mobile devices)
		} else {
			this.meType = TransactionType.HTML; // The type is HTTPS (for clients using a browser)
		}
	}

	public IViewRequest createViewRequest() {
		IViewRequest outReq = null;
		switch (this.meType) {
			case DATA: // Here, we choose the concrete ViewDataRequest
				outReq = new ViewPhoneRequest(this.meReq);
				break;
			case HTML: // Here, we choose the concrete ViewHtmlRequest
				outReq = new ViewHtmlRequest(this.meReq);
		}
		return outReq;
	}

	public IViewResponse createViewResponse(String rawResponse) throws JAXBException {
		IViewResponse outResp = null;
		switch (this.meType) {
			case DATA: // Here, we choose the concrete ViewDataResponse
				outResp = new ViewPhoneResponse(rawResponse);
				break;
			case HTML: // Here, we choose the concrete ViewHtmlResponse
				outResp = new ViewHtmlResponse(rawResponse);
		}
		return outResp;
	}
}
