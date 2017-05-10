/**
 * 
 */
package view;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * @author Janty Azmat
 *
 */
public class ViewSecureConnection {
	// Fields
	private static final int me_PORT= 9999;
	private static final String me_CERT = "/lans.jks";
	private static final String me_CERT_PASS = "qazqazqaz";
	private SSLServerSocket meSock;
	private IViewObserver meObserver;

	// Constructor
	public ViewSecureConnection(IViewObserver theObserver) {
		this.meObserver = theObserver;
		try {
			KeyStore tmpKey = KeyStore.getInstance("JKS");
			tmpKey.load(this.getClass().getResourceAsStream(me_CERT), me_CERT_PASS.toCharArray());
			//tmpKey.load(new FileInputStream(me_CERT), me_CERT_PASS.toCharArray());
			KeyManagerFactory tmpKeyFac = KeyManagerFactory.getInstance("SunX509");
			tmpKeyFac.init(tmpKey, me_CERT_PASS.toCharArray());
			KeyManager[] tmpKeyMgr = tmpKeyFac.getKeyManagers();
			TrustManagerFactory tmpMgrFac = TrustManagerFactory.getInstance("SunX509");
			tmpMgrFac.init(tmpKey);
			TrustManager[] tmpTrMgr = tmpMgrFac.getTrustManagers();
			SSLContext tmpCont = SSLContext.getInstance("TLSv1.2");
			tmpCont.init(tmpKeyMgr, tmpTrMgr, null);
			this.meSock = (SSLServerSocket)tmpCont.getServerSocketFactory().createServerSocket(me_PORT);
		} catch (KeyStoreException e) {
			this.meObserver.requestPrintErrWarn("No Provider for the specified KeyStore type.", true);
		} catch (NoSuchAlgorithmException e) {
			this.meObserver.requestPrintErrWarn("Problem loading certificate algorithm.", true);
		} catch (CertificateException e) {
			this.meObserver.requestPrintErrWarn("Problem loading certificate.", true);
		} catch (FileNotFoundException e) {
			this.meObserver.requestPrintErrWarn("Problem opening certificate file.", true);
		} catch (IOException e) {
			this.meObserver.requestPrintErrWarn("Cannot open the socket, or certificate load problem. Socket could be reserved.", true);
		} catch (UnrecoverableKeyException e) {
			this.meObserver.requestPrintErrWarn("Problem recovering key (e.g. the key's password is wrong).", true);
		} catch (KeyManagementException e) {
			this.meObserver.requestPrintErrWarn("Problem in key management.", true);
		} catch (SecurityException e) {
			this.meObserver.requestPrintErrWarn("Security violation does not allow opening the socket or the key file.", true);
		} catch (IllegalArgumentException e) {
			this.meObserver.requestPrintErrWarn("Invalid port specified.", true);
		}
	}

	/**
	 * A method to make server start listening to client connections, and assigns a new thread for every connection.
	 */
	public void startListen() {
		while (!this.meSock.isClosed()) {
			SSLSocket tmpSock = null;
			try {
				tmpSock = (SSLSocket)this.meSock.accept();
			} catch (SocketTimeoutException e) {
				this.meObserver.requestPrintErrWarn("Connection with client timed out.", false);
			} catch (IOException e) {
				this.meObserver.requestPrintMessage("Canceling the wait for connections. Exiting...");
			} catch (SecurityException e) {
				this.meObserver.requestPrintErrWarn("Security violation does not allow accepting this new client.", false);
			}
			//new Thread(new ClientConnection(tmpSock, this.meObserver)).start();
			new Thread(new ViewClientTransaction(tmpSock, this.meObserver)).start();
		}
	}

	/**
	 * A method to stop listening to connections and close the socket.
	 */
	public void stopListen() {
		try {
			this.meSock.close();
		} catch (IOException e) {/* Safe to ignore */}
	}
}
