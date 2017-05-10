/**
 * 
 */
package model;

/**
 * @author Janty Azmat
 */
public interface IModelObserver {

	void requestPrintErrWarn(String errWarningMsg, boolean terminateExecution);
	void requestPrintMessage(String theMsg);
	boolean requestCheckPaused();
}
