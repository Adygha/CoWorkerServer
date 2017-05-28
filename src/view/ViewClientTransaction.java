/**
 * 
 */
package view;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.net.ssl.SSLSocket;
import javax.xml.bind.JAXBException;

/**
 * @author Janty Azmat
 *
 */
class ViewClientTransaction implements Runnable {
	// Fields
	private static final int me_BUFSIZE = 512; // Buffer size
	private SSLSocket meAccSock;
	private IViewObserver meObserver;

	public ViewClientTransaction(SSLSocket acceptSocket, IViewObserver theObserver) {
		if (acceptSocket != null)
			this.meAccSock = acceptSocket;
		this.meObserver = theObserver;
	}

	@Override
	public void run() {
		if (this.meAccSock == null)
			return;
		System.out.println("Client connection accepted.."); // TODO: Should remove
		this.meAccSock.setEnabledCipherSuites(this.meAccSock.getSupportedCipherSuites());
		try {
			this.meAccSock.startHandshake();
		} catch (IOException e) {
			this.meObserver.requestPrintErrWarn("Cannot start SSL/TLS secure connection.", false);
			try {
				this.meAccSock.close();
			} catch (IOException e1) {/* Can be safely ignored */}
			System.out.println("Client connection closed..SSSSSSSSSSSSLLLLLLLLLLLLLLLLLLLLLLLLL"); // TODO: Should remove
			return;
		}
		byte[] tmpReq = this.receiveRequest();
		if (tmpReq.length < 1) {
			try {
				this.meAccSock.close();
			} catch (IOException e1) {/* Can be safely ignored */}
			System.out.println("Client connection closed..XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"); // TODO: Should remove
			return;
		}
		ViewRequestResposeFactory tmpFac = new ViewRequestResposeFactory(tmpReq);
		IViewRequest tmpViewReq = tmpFac.createViewRequest();
		IViewResponse tmpViewResp = null;
		try {
			if (tmpViewReq.getIsDirect()) {
				tmpViewResp = tmpFac.createViewResponse(tmpViewReq.getRawRequest());
			} else {
				tmpViewResp = tmpFac.createViewResponse(this.meObserver.requestRawResponse(tmpViewReq.getRawRequest()));
			}
		} catch (JAXBException e) {
			this.meObserver.requestPrintErrWarn("Error while creating the XML bilder, or extracting XML data.", true);
		}
		this.sendResponse(tmpViewResp.getResponseData());
		System.out.println("Client connection closed.."); // TODO: Should remove
	}

	// A method to fully get the request bytes
	private byte[] receiveRequest() {
		if (this.meAccSock == null)
			return null;
		byte tmpBuf[] = null;
		int tmpNum = 0; // To get the number of bytes read
		ByteArrayOutputStream tmpMsg = new ByteArrayOutputStream(me_BUFSIZE); // To push the received buffer in if message is big.
		try {
			do { // A loop to receive
				tmpBuf = new byte[me_BUFSIZE];
				tmpNum = this.meAccSock.getInputStream().read(tmpBuf); // The actual receiving
				if (tmpNum > 0)
					tmpMsg.write(tmpBuf, 0, tmpNum); // Accumulate message
			} while (!this.checkReceiveEnded(tmpMsg) && tmpNum > -1); // In case of HTTP request end-chars or received '-1' (EOF) then the other side has closed the connection (according to: http://stackoverflow.com/questions/10240694/ )
		} catch (IOException e) {
			this.meObserver.requestPrintErrWarn("Connection timed out or terminated.", false);
			if (this.meAccSock != null && !this.meAccSock.isClosed()) {
				try {
					this.meAccSock.close();
				} catch (IOException e1) {/* Can be safely ignored */}
			}
		}
		System.out.println("----------------- Start Request String -----------------");	//
		System.out.println(tmpMsg.toString());											// TODO: Should remove
		System.out.println("-----------------  End Request String  -----------------");	//
		return tmpMsg.toByteArray();
	}

	// A method to send response bytes back to client
	private void sendResponse(byte[] respBytes) {
		try {
			this.meAccSock.getOutputStream().write(respBytes);
		} catch (IOException e) {
			this.meObserver.requestPrintErrWarn("Connection timed out or terminated.", false);
			if (this.meAccSock != null && !this.meAccSock.isClosed()) {
				try {
					this.meAccSock.close();
				} catch (IOException e1) {/* Can be safely ignored */}
			}
		}
		System.out.println("----------------- Start Response String -----------------");	//
		System.out.println(new String(respBytes));											// TODO: Should remove
		System.out.println("-----------------  End Response String  -----------------");	//
	}

	// Checks if the received data is empty (it fails when uploading files big enough to be multi-part but this
	// assignment supposed to use small files)
	private boolean checkReceiveEnded(ByteArrayOutputStream byteStream) {
		String tmpStr = byteStream.toString();
		if (!tmpStr.contains("\r\n\r\n")) // If does not contain double CRLF then there is still data (but not the opposite)
			return false;
		if (tmpStr.contains("Content-Length:")) { // If there is a content-length then check if all content is there
			try {
				tmpStr = tmpStr.substring(tmpStr.indexOf("Content-Length:") + 15); // Cut the unnecessary part (15 is the length of "Content-Length:")
				int tmpLen = Integer.parseInt(tmpStr.substring(0, tmpStr.indexOf('\n')).trim()); // Get content-length
				tmpStr = tmpStr.substring(tmpStr.indexOf("\r\n\r\n") + 4); // Get only the content
				//if (tmpStr.length() < tmpLen)
				if (tmpStr.getBytes().length < tmpLen)
					return false; // In case not all content received
			} catch (IndexOutOfBoundsException | NumberFormatException e) { // These 2 exceptions will happen if not everything is received
				return false;
			}
		}
		return true;
	}
}
