/**
 * 
 */
package model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import model.data_access.DerbyDao;

/**
 * A class that represents an HTTP response.
 * @author Janty Azmat
 */
public class MyResponse {
	// Constants
	private static final int me_BUFSIZE = 512; // Buffer size
	// Ready-made base responses
	private static final String me_200_OK_STARTER = "HTTP/1.1 200 OK\r\nServer: CoWorkersServer\r\n";
	private static final String me_403_FORBIDDEN = "HTTP/1.1 403 Forbidden\r\nServer: CoWorkersServer\r\nContent-Length: 48\r\nContent-Type: text/html\r\nConnection: close\r\n\r\n<html><body><h1>403 Forbidden</h1></body></html>";
	private static final String me_404_NOT_FOUND = "HTTP/1.1 404 Not Found\r\nServer: CoWorkersServer\r\nContent-Length: 48\r\nContent-Type: text/html\r\nConnection: close\r\n\r\n<html><body><h1>404 Not found</h1></body></html>";
	private static final String me_500_INTERNAL_SERVER_ERROR = "HTTP/1.1 500 Internal Server Error\r\nServer: CoWorkersServer\r\nContent-Length: 60\r\nContent-Type: text/html\r\nConnection: close\r\n\r\n<html><body><h1>500 Internal Server Error</h1></body></html>";
	// Ready-made additional responses
	//private static final String me_201_CREATED_STARTER = "HTTP/1.1 201 Created\r\nServer: CoWorkersServer\r\nConnection: close\r\nLocation: ";
	//private static final String me_204_NO_CONTENT = "HTTP/1.1 204 No Content\r\nConnection: close\r\n\r\n";
	private static final String me_400_BAD_REQUEST = "HTTP/1.1 400 Bad Request\r\nServer: CoWorkersServer\r\nContent-Length: 50\r\nContent-Type: text/html\r\nConnection: close\r\n\r\n<html><body><h1>400 Bad request</h1></body></html>";
	//private static final String me_405_METHOD_NOT_ALLOWED = "HTTP/1.1 405 Method Not Allowed\r\nServer: CoWorkersServer\r\nContent-Length: 57\r\nContent-Type: text/html\r\nConnection: close\r\n\r\n<html><body><h1>405 Method Not Allowed</h1></body></html>";
	//private static final String me_415_UNSUPPORTED_MEDIA_TYPE = "HTTP/1.1 415 Unsupported Media Type\r\nServer: CoWorkersServer\r\nContent-Length: 61\r\nContent-Type: text/html\r\nConnection: close\r\n\r\n<html><body><h1>415 Unsupported Media Type</h1></body></html>";
	private static final String me_501_NOT_IMPLEMENTED = "HTTP/1.1 501 Not Implemented\r\nServer: CoWorkersServer\r\nContent-Length: 54\r\nContent-Type: text/html\r\nConnection: close\r\n\r\n<html><body><h1>501 Not Implemented</h1></body></html>";
	private static final String me_503_SERVICE_UNAVAILABLE = "HTTP/1.1 503 Service Unavailable\r\nServer: CoWorkersServer\r\nContent-Length: 58\r\nContent-Type: text/html\r\nConnection: close\r\n\r\n<html><body><h1>503 Service Unavailable</h1></body></html>";
	private static final String me_505_HTTP_NOT_SUPPORTED = "HTTP/1.1 505 HTTP Version Not Supported\r\nServer: CoWorkersServer\r\nContent-Length: 65\r\nContent-Type: text/html\r\nConnection: close\r\n\r\n<html><body><h1>505 HTTP Version Not Supported</h1></body></html>";
	// Partials
	private static final String me_CONTENT_LENGTH = "Content-Length: ";
	private static final String me_HTML_CONTENT = "Content-Type: text/html\r\nConnection: close\r\n\r\n";
	//private static final String me_PNG_CONTENT = "Content-Type: image/png\r\nConnection: close\r\n\r\n";
	private static final String me_ICO_CONTENT = "Content-Type: image/x-icon\r\nConnection: close\r\n\r\n";
	private static final String me_TXT_CONTENT = "Content-Type: text/plain\r\nConnection: close\r\n\r\n";
	//private static final String me_OTHER_CONTENT = "Content-Type: application/octet-stream\r\nConnection: close\r\n\r\n"; // Best fit recommended (can be downloaded)
	// Ready made resource pages
	private static final String me_INIT_PAGE = "/00_InitPage.txt";
	private static final String me_LOGIN_PAGE = "/01_LoginPage.txt";
	private static final String me_MAIN_PAGE_TOP = "/02_MainPageTop.txt";
	private static final String me_MAIN_PAGE_BOT = "/02_MainPageBot.txt";
	// Fields
	private byte[] meRespBytes; // Will hold the response bytes
	//private boolean meIsNewCopy; // To indicate if the new uploaded file should be created as a new copy (in case of POST)
	private IModelObserver meObserver;
	private MessageDigest meHash;
	private DerbyDao meDao;

	/**
	 * Constructor
	 * @param theRequest the request object to base the response on.
	 */
	public MyResponse(MyRequest theRequest, IModelObserver theObserver) {
		this.meObserver = theObserver;
		try {
			this.meHash = MessageDigest.getInstance("SHA-1"); // To create a SHA1 hash
			this.meDao = new DerbyDao();
		} catch (NoSuchAlgorithmException | SQLException e) {
			this.meObserver.requestPrintErrWarn("Error while initiating database connection", true);
		}
		String tmpArr[];
		String tmpNoSlash = theRequest.getPath().startsWith("/") ? theRequest.getPath().substring(1) : theRequest.getPath();
		switch (theRequest.getRequestType()) {
			case HTML_GET:
				tmpArr = theRequest.getRequstString().split("_");
				switch (tmpArr[2]) {
					case "INIT":
						this.meRespBytes = this.createHtmlInitPage();
						break;
					case "LOGIN":
						this.meRespBytes = this.createHtmlLoginPage();
						break;
					case "MAIN":
						this.meRespBytes = this.createHtmlMainPage();
						break;
					default:
						this.meRespBytes = this.create404NotFound();
				}
				break;
			case DATA_GET:
				tmpArr = theRequest.getRequstString().split("_");
				switch (tmpArr[2]) {
					case "SHA1":
						//this.meRespBytes = this.getSha1Hash(tmpNoSlash.getBytes());
						this.meRespBytes = this.create200Ok(this.getSha1Hash(tmpNoSlash.getBytes()));
						break;
					case "CREDOK":
						String tmpEmail = tmpNoSlash.substring(0, tmpNoSlash.indexOf(':'));
						String tmpSha1Pass = tmpNoSlash.substring(tmpNoSlash.indexOf(':') + 1);
					try {
						this.meRespBytes = this.create200Ok(Boolean.toString(this.meDao.checkCredentials(tmpEmail, tmpSha1Pass)).getBytes());
					} catch (SQLException e) {}
						break;
					default:
						this.meRespBytes = this.create404NotFound();
				}
				break;
			case GET: // Here we handle GET request
//				switch (theRequest.getPathType()) {
//					case FILE:
//						this.meRespBytes = this.create200Ok(theRequest);
//						break;
//					case FORBIDDEN:
//					case DIRECTORY: // We will refuse display directory content for now
//						this.meRespBytes = this.create403Forbidden();
//						break;
//					case NOT_EXIST:
//						this.meRespBytes = this.create404NotFound();
//				}
				if (theRequest.getPath().equals("/favicon.ico")) {
					this.meRespBytes = this.create200Ok();
				} else {
					if (!theRequest.getPath().equals("/"))
						theRequest.setPath("/");
					this.meRespBytes = this.createHtmlInitPage();
				}
				break;
			case PUT: // Here we handle PUT request
//				switch (theRequest.getPathType()) {
//					case FILE: // This will overwrite the existing file
//						this.meRespBytes = this.create204NoContent(theRequest);
//						break;
//					case FORBIDDEN:
//					case DIRECTORY: // Writing to a directory can be considered as forbidden
//						this.meRespBytes = this.create403Forbidden();
//						break;
//					case NOT_EXIST: // This is the normal case where a new file is created
//						this.meRespBytes = this.create201Created(theRequest);
//				}
				break;
			case POST: // Here we handle PUT request
//				switch (theRequest.getPathType()){
//					case FILE:
//					case NOT_EXIST:
//						updateFileWithPost(theRequest);
//						break;
//					case DIRECTORY:
//					case FORBIDDEN:
//						this.meRespBytes = this.create403Forbidden();
//				}
				break;
//			case NOT_ALLOWED:
//				this.meRespBytes = this.create405NotAllowed();
//				break;
			case WRONG_HTTP:
				this.meRespBytes = this.create505WrongHTTPVer();
				break;
			case NOT_IMP: // ....
				this.meRespBytes = this.create501NotImplemented();
				break;
			case ERROR:
				this.meRespBytes = this.create500InternalServerError();
				break;
			case PAUSE:
				this.meRespBytes = this.create503ServiceUnavailable();
				break;
			case BAD:
				this.meRespBytes = this.create400BadRequest();
		}
	}

//	/**
//	 * POST is only supported at a specified path
//	 * @param theRequest
//	 */
//	private void updateFileWithPost(MyRequest theRequest) {
//		if (theRequest.getRelativePath().startsWith("/post/") && theRequest.getPathType() == PathType.NOT_EXIST) {
//			this.meRespBytes = this.create201Created(theRequest);
//		} else if (theRequest.getRelativePath().startsWith("/post/") && theRequest.getPathType() == PathType.FILE) {
//			if (theRequest.getAbsolutePath().endsWith("/post/post-test.txt")) { // POST to text file in this case
//				this.meRespBytes = this.create204NoContent(theRequest);
//			} else { // Otherwise, it is an upload POST (we won't append or overwrite the file, we'll create a new copy)
//				this.meIsNewCopy = true;
//				this.meRespBytes = this.create201Created(theRequest);
//				this.meIsNewCopy = false;
//			}
//		} else {
//			this.meRespBytes = this.create403Forbidden();
//		}
//	}

	/**
	 * Returns the response represented in bytes
	 * @return	the response's bytes
	 */
	public byte[] getResponseBytes() {
		return this.meRespBytes;
	}


	// vvvvvvvvvvvvvvvvvvvv Start Private Section vvvvvvvvvvvvvvvvvvvv //
	// Contains methods that creates resposes as bytes

	private byte[] create200Ok() { // OK Essential
		ByteArrayOutputStream outResp = new ByteArrayOutputStream(me_BUFSIZE); // Buffer-size is a good initial size
		try {
			outResp.write(me_200_OK_STARTER.getBytes());
			byte tmpFileData[] = this.readResourceFile("/favicon.ico");
			outResp.write((me_CONTENT_LENGTH + tmpFileData.length + "\r\n").getBytes());
			outResp.write(me_ICO_CONTENT.getBytes());
			outResp.write(tmpFileData);
		} catch (IOException e) {
			return this.create403Forbidden(); // If 'IOException' thrown then reading is forbidden on the file (since it is already exists)
		}
		return outResp.toByteArray();
	}

	private byte[] create200Ok(byte[] theData) { // OK Essential
		ByteArrayOutputStream outResp = new ByteArrayOutputStream(me_BUFSIZE); // Buffer-size is a good initial size
		try {
			outResp.write(me_200_OK_STARTER.getBytes());
			outResp.write((me_CONTENT_LENGTH + theData.length + "\r\n").getBytes());
			outResp.write(me_TXT_CONTENT.getBytes());
			outResp.write(theData);
		} catch (IOException e) {
			return this.create403Forbidden(); // If 'IOException' thrown then reading is forbidden on the file (since it is already exists)
		}
		return outResp.toByteArray();
	}

//	private byte[] create201Created(MyRequest theRequest) {
//		ByteArrayOutputStream outResp = new ByteArrayOutputStream(me_BUFSIZE); // Buffer-size is a good initial size
//		try {
////			if (this.meIsNewCopy) { // In case of POST upload and a new copy is needed
////				String tmpNew = theRequest.getAbsolutePath().substring(0, theRequest.getAbsolutePath().lastIndexOf('/') + 1) + "copy-" + UUID.randomUUID().toString() + "-" + theRequest.getAbsolutePath().substring(theRequest.getAbsolutePath().lastIndexOf('/') + 1);
////				Files.write(new File(tmpNew).toPath(), theRequest.getPayloadData());
////				outResp.write(me_201_CREATED_STARTER.getBytes());
////				outResp.write((theRequest.getRelativePath().substring(0, theRequest.getRelativePath().lastIndexOf('/') + 1) + tmpNew.substring(tmpNew.lastIndexOf('/') + 1)).getBytes());
////			} else { // Normal situation of POST or PUT
////				Files.write(new File(theRequest.getAbsolutePath()).toPath(), theRequest.getPayloadData());
////				outResp.write(me_201_CREATED_STARTER.getBytes());
////				outResp.write(theRequest.getRelativePath().getBytes());
////			}
//			outResp.write("\r\n\r\n".getBytes());
//		} catch (IOException e) {
//			return this.create403Forbidden(); // If 'IOException' thrown then writing is forbidden on the file (since it is already exists)
//		}
//		return outResp.toByteArray();
//	}

//	private byte[] create204NoContent(MyRequest theRequest) {
////		try {
////			if (theRequest.getRequestType() == RequestType.PUT) { // If PUT then truncate (overwrite the file)
////				Files.write(new File(theRequest.getAbsolutePath()).toPath(), theRequest.getPayloadData());
////			} else { // Then it's POST (append to file)
////				Files.write(new File(theRequest.getAbsolutePath()).toPath(), theRequest.getPayloadData(), StandardOpenOption.APPEND);
////			}
////		} catch (IOException e) {
////			return this.create403Forbidden(); // If 'IOException' thrown then writing is forbidden on the file (since it is already exists)
////		}
//		return me_204_NO_CONTENT.getBytes();
//	}

	private byte[] create403Forbidden() {
		return me_403_FORBIDDEN.getBytes();
	}

	private byte[] create404NotFound() {
		return me_404_NOT_FOUND.getBytes();
	}

	private byte[] create500InternalServerError() {
//		HtmlCreator tmpCr = new HtmlCreator(this.meObserver);
//		return tmpCr.getInitPage();
		return me_500_INTERNAL_SERVER_ERROR.getBytes();
	}

	private byte[] create400BadRequest() {
		return me_400_BAD_REQUEST.getBytes();
	}

	private byte[] create501NotImplemented() {
		return me_501_NOT_IMPLEMENTED.getBytes();
	}

	private byte[] create503ServiceUnavailable() {
		return me_503_SERVICE_UNAVAILABLE.getBytes();
	}

	//private byte[] create405NotAllowed() { return me_405_METHOD_NOT_ALLOWED.getBytes(); }

	private byte[] create505WrongHTTPVer() { return me_505_HTTP_NOT_SUPPORTED.getBytes(); }

	//private byte[] create415UnsupportedMediaType() { return me_415_UNSUPPORTED_MEDIA_TYPE.getBytes(); }

	private byte[] createHtmlInitPage() {
		return this.get200OkPage(this.readResourceFile(me_INIT_PAGE));
	}

	private byte[] createHtmlLoginPage() {
		return this.get200OkPage(this.readResourceFile(me_LOGIN_PAGE));
	}

	private byte[] createHtmlMainPage() {
		// TODO: ADD PAGE DATA <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
		ByteArrayOutputStream tmpCont = new ByteArrayOutputStream(2048);
		try {
			tmpCont.write(this.readResourceFile(me_MAIN_PAGE_TOP));
			tmpCont.write(this.readResourceFile(me_MAIN_PAGE_BOT));
		} catch (IOException e) {
			this.meObserver.requestPrintErrWarn("Problem handling stream data in memory (probably memory error)", true);
		}
		return this.get200OkPage(tmpCont.toByteArray());
		
	}

	// Creates a 200 OK response with page data
	private byte[] get200OkPage(byte[] theContent) {
		ByteArrayOutputStream outPage = new ByteArrayOutputStream(2048);
		try {
			outPage.write(me_200_OK_STARTER.getBytes());
			outPage.write((me_CONTENT_LENGTH + theContent.length + "\r\n").getBytes());
			outPage.write(me_HTML_CONTENT.getBytes());
			outPage.write(theContent);
		} catch (IOException e) {
			this.meObserver.requestPrintErrWarn("Problem handling stream data in memory (probably memory error)", true);
		}
		return outPage.toByteArray();
	}

	// Reads resource file to byte array (made to avoid code duplication)
	private byte[] readResourceFile(String theFile) {
		ByteArrayOutputStream outAll = null;
		try {
			InputStream tmpStm = this.getClass().getResourceAsStream(theFile);
			outAll = new ByteArrayOutputStream(Math.max(32, tmpStm.available()));
			byte[] tmpBuf = new byte[tmpStm.available()];
			for (int tmpRd = tmpStm.read(tmpBuf); tmpRd > -1; tmpRd = tmpStm.read(tmpBuf))
				outAll.write(tmpBuf, 0, tmpRd);
		} catch (IOException e) {
			this.meObserver.requestPrintErrWarn("Problem reading resourse file: " + theFile, true);
		}
		return outAll.toByteArray();
	}

	private byte[] getSha1Hash(byte[] origData) {
		return new BigInteger(1, this.meHash.digest(origData)).toString(16).toUpperCase().getBytes();
	}

	// ^^^^^^^^^^^^^^^^^^^^  End Private Section  ^^^^^^^^^^^^^^^^^^^^ //
}
