/**
 * 
 */
package view;

/**
 * @author Janty Azmat
 */
public interface IViewObserver {

	void requestPrintErrWarn(String errWarningMsg, boolean terminateExecution);
	void requestPrintMessage(String theMsg);
	void requestPause();
	void requestQuit();
	String requestRawResponse(String theRequest);
}
