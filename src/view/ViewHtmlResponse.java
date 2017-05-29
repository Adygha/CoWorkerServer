/**
 * 
 */
package view;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

/**
 * A class that is responsible for the translation of the raw string response to an HTML response that the client's browser can understand.
 * @author Janty Azmat
 */
class ViewHtmlResponse implements IViewResponse {
	// Constants
		// Ready-made base responses
	private static final String me_200_OK_STARTER = "HTTP/1.1 200 OK\r\nServer: CoWorkerServer\r\n";
	private static final String me_400_BAD_REQUEST = "HTTP/1.1 400 Bad Request\r\nServer: CoWorkerServer\r\nContent-Length: 50\r\nContent-Type: text/html\r\nConnection: close\r\n\r\n<html><body><h1>400 Bad request</h1></body></html>";
	private static final String me_403_FORBIDDEN = "HTTP/1.1 403 Forbidden\r\nServer: CoWorkerServer\r\nContent-Length: 48\r\nContent-Type: text/html\r\nConnection: close\r\n\r\n<html><body><h1>403 Forbidden</h1></body></html>";
	private static final String me_501_NOT_IMPLEMENTED = "HTTP/1.1 501 Not Implemented\r\nServer: CoWorkerServer\r\nContent-Length: 54\r\nContent-Type: text/html\r\nConnection: close\r\n\r\n<html><body><h1>501 Not Implemented</h1></body></html>";
	private static final String me_505_HTTP_NOT_SUPPORTED = "HTTP/1.1 505 HTTP Version Not Supported\r\nServer: CoWorkersServer\r\nContent-Length: 65\r\nContent-Type: text/html\r\nConnection: close\r\n\r\n<html><body><h1>505 HTTP Version Not Supported</h1></body></html>";
		// Page partials
	private static final String me_CONTENT_LENGTH = "Content-Length: ";
	private static final String me_HTML_CONTENT = "Content-Type: text/html\r\nConnection: close\r\n\r\n";
	private static final String me_ICO_CONTENT = "Content-Type: image/x-icon\r\nConnection: close\r\n\r\n";
	private static final String me_XML_CONTENT = "Content-Type: text/xml\r\nConnection: close\r\n\r\n";
		// Ready made resource pages
	private static final String me_INIT_PAGE = "/00_InitPage.txt";
	private static final String me_PAGE_TOP = "/00_PageTop.txt";
	private static final String me_PAGE_BOT = "/00_PageBot.txt";
	// Fields
	private byte[] meResp;

	public ViewHtmlResponse(String rawResponse) throws JAXBException {
		try {
			switch (rawResponse) {
				case "WRONG_HTTP":
					this.meResp = this.create505WrongHTTPVer();
					break;
				case "NOT_IMP":
					this.meResp = this.create501NotImplemented();
					break;
				case "BAD":
					this.meResp = this.create400BadRequest();
					break;
				default: // Other 'GET' and 'HTML_' requests
					String[] tmpSplit = rawResponse.split(" ", 2);
					if (tmpSplit[0].equals("GET")) { // The case of init page or favicon.ico
						if (tmpSplit[1].equals("/favicon.ico")) {
							this.meResp = this.create200Ok(this.readResourceFile(tmpSplit[1]), me_ICO_CONTENT);
						} else {
							this.meResp = this.create200Ok(this.readResourceFile(me_INIT_PAGE), me_HTML_CONTENT);
						}
					} else {
						JAXBElement<Object> tmpElem = this.getRoot(rawResponse, Object.class);
						if (tmpElem.getName().getLocalPart().equals("WARNING") || tmpElem.getName().getLocalPart().equals("SHA1")) {
							this.meResp = this.create200Ok(rawResponse.getBytes(), me_XML_CONTENT);
						} else {
							ViewXmlHtmlParser tmpBld = this.getRoot(rawResponse, ViewXmlHtmlParser.class).getValue();
							this.meResp = this.createPage(tmpBld.getHtml());
						}
					}
			}
		} catch (IOException e) {
			this.meResp = this.create403Forbidden();
		}
	}

	private <T> JAXBElement<T> getRoot(String xmlString, Class<T> theClass) throws JAXBException {
		Unmarshaller tmpUnmar = JAXBContext.newInstance(theClass).createUnmarshaller();
		return tmpUnmar.unmarshal(new StreamSource(new StringReader(xmlString)), theClass);
	}

	private byte[] createPage(String pageData) throws IOException {
		ByteArrayOutputStream tmpResp = new ByteArrayOutputStream(8192); // 8kB
		tmpResp.write(this.readResourceFile(me_PAGE_TOP));
		tmpResp.write(pageData.getBytes());
		tmpResp.write(this.readResourceFile(me_PAGE_BOT));
		return this.create200Ok(tmpResp.toByteArray(), me_HTML_CONTENT);
	}

	// Creates a 200 Ok HTTP response
	private byte[] create200Ok(byte[] contentData, String contentType) throws IOException {
		ByteArrayOutputStream outResp = new ByteArrayOutputStream(8192); // 8kB
		outResp.write(me_200_OK_STARTER.getBytes());
		outResp.write((me_CONTENT_LENGTH + contentData.length + "\r\n").getBytes());
		outResp.write(contentType.getBytes());
		outResp.write(contentData);
		return outResp.toByteArray();
	}

	private byte[] create400BadRequest() {
		return me_400_BAD_REQUEST.getBytes();
	}

	private byte[] create403Forbidden() {
		return me_403_FORBIDDEN.getBytes();
	}

	private byte[] create501NotImplemented() {
		return me_501_NOT_IMPLEMENTED.getBytes();
	}

	private byte[] create505WrongHTTPVer() {
		return me_505_HTTP_NOT_SUPPORTED.getBytes();
	}

	// Reads resource file to byte array (made to avoid code duplication)
	private byte[] readResourceFile(String theFile) throws IOException {
		ByteArrayOutputStream outAll = null;
		InputStream tmpStm = this.getClass().getResourceAsStream(theFile);
		outAll = new ByteArrayOutputStream(Math.max(32, tmpStm.available()));
		byte[] tmpBuf = new byte[tmpStm.available()];
		for (int tmpRd = tmpStm.read(tmpBuf); tmpRd > -1; tmpRd = tmpStm.read(tmpBuf))
			outAll.write(tmpBuf, 0, tmpRd);
		return outAll.toByteArray();
	}

	@Override
	public byte[] getResponseData() {
		return this.meResp;
	}
}
