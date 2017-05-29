/**
 * 
 */
package view;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

/**
 * @author Janty Azmat
 */
public class ViewConsole {
	// Constants
	private static final String me_LOG_FILE = "ServerLog.txt";
	private static final boolean me_PRINT_TO_CONSOLE = true; // To specify if we want to print to console too or just log file
	// Fields
	private IViewObserver meObserver;

	public ViewConsole(IViewObserver theObserver) {
		try {
			Files.deleteIfExists(Paths.get(me_LOG_FILE));
		} catch (IOException e) {/* Safe to ignore, will exit if cannot write */}
		this.meObserver = theObserver;
	}

	/**
	 * A method (used with a thread runnable) to wait for the 'q' to quite and 'p' to pause. 
	 */
	public void waitPauseQuit() {
		try {
			int tmpIn = 0;
			while (tmpIn != 'q') {
				tmpIn = System.in.read();
				if (tmpIn == 'p') {
					this.meObserver.requestPause();
				}
			}
			this.meObserver.requestQuit();
		} catch (IOException e) {/* Can be safely ignored */}
	}

	/**
	 * A method to log and print an error message to the error output stream and terminate execution if required.
	 * @param errWarningMsg			the error or warning message
	 * @param terminateExecution	true to terminate execution after displaying the message
	 */
	public void printLogErrWarning(String errWarningMsg, boolean terminateExecution) {
		// Here, I thought it's better to keep the error handling the same as in the provided
		// files (no exception throwing) to avoid confusion with accidental exceptions that
		// might happen when correcting.;
		String tmpStr = (terminateExecution ? "Error:\n" : "Warning:\n") + errWarningMsg;
		this.writeLog(tmpStr);
		if (me_PRINT_TO_CONSOLE)
			System.err.println(tmpStr);
		if (terminateExecution)
			System.exit(-1);
	}

	/**
	 * A method to log and print a message to console.
	 * @param theMsg	the message to be logged and displayed
	 */
	public void printLogMessage(String theMsg) {
		this.writeLog(theMsg);
		if (me_PRINT_TO_CONSOLE)
			System.out.println(theMsg);
	}

	// A method to write to just write to log file
	private void writeLog(String theLog) {
		try {
			// HERE WE SHOULD CHECK FOR FILE EXIST AND CHECK SIZE TO BACKUP (Not important for this course) <<<<<<<<<<<<<<
			BufferedWriter tmpLog = new BufferedWriter(new FileWriter(me_LOG_FILE, true));
			tmpLog.write("\r\n" + LocalDateTime.now() + " : " + theLog);
			tmpLog.close();
		} catch (IOException e) {
			if (me_PRINT_TO_CONSOLE)
				System.err.println("Error: Cannot handle writing to Log file. Exiting");
			System.exit(-1);
		}
	}
}
