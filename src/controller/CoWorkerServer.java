/**
 * 
 */
package controller;

import model.IModelObserver;
import model.RawRequest;
import model.RawResponse;
import model.data_access.IDao;
import view.IViewObserver;
import view.ViewConsole;
import view.ViewSecureConnection;

/**
 * @author Janty Azmat
 */
public class CoWorkerServer implements IViewObserver, IModelObserver { // No modifier (inside package only)
	// Fields
	private ViewConsole meConsole;
	private ViewSecureConnection meConn;
	private IDao meDao;
	private boolean meIsPause; // Is under maintenance flag

	public CoWorkerServer(IDao theDao) {
		this.meDao = theDao;
		this.meConsole = new ViewConsole(this);
		this.meConn = new ViewSecureConnection(this);
	}

	public void start() {
		Thread tmpTh = new Thread(this.meConsole::waitPauseQuit); // Just a thread to quit the server by entering 'q' in the console (or pause it)
		System.out.println("CoWorker Server. Enter 'p' to toggle server pause (maintenance), or 'q' to quit..");
		tmpTh.start();
		this.meConn.startListen();
	}

	@Override
	public void requestPause() {
		this.meIsPause = !this.meIsPause;
		this.meConsole.printLogMessage("System is " + (meIsPause ? "paused for maintenance." : "resuming from maintenance pause."));
	}

	@Override
	public void requestQuit() {
		this.meConn.stopListen();
	}

	@Override
	public void requestPrintErrWarn(String errWarningMsg, boolean terminateExecution) {
		this.meConsole.printLogErrWarning(errWarningMsg, terminateExecution);
	}

	@Override
	public void requestPrintMessage(String theMsg) {
		this.meConsole.printLogMessage(theMsg);
	}

	@Override
	public String requestRawResponse(String theRequest) {
		RawRequest tmpReq = new RawRequest(theRequest);
		RawResponse tmpResp = new RawResponse(tmpReq, this.meDao, this);
		return tmpResp.getRawResponseData();
	}

	@Override
	public boolean requestCheckPaused() {
		return this.meIsPause;
	}
}
