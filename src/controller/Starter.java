/**
 * 
 */
package controller;

import java.sql.SQLException;

import model.data_access.DerbyDao;
import model.data_access.IDao;

/**
 * @author Janty Azmat
 */
public class Starter {

	public static void main(String[] args) {
		try (IDao tmpDao = new DerbyDao()) {
			CoWorkerServer tmpSrv = new CoWorkerServer(tmpDao);
			tmpSrv.start();
		} catch (SQLException e) {
			if (e.getErrorCode() == 50000 && e.getSQLState().equals("XJ015")) {
				System.out.println("Connection to Derby database is closed gracefully.");
			} else {
				System.err.println("Error creating, opening, or closing database.");
			}
		}

//		try {
//			XmlPageBuilder bld = new XmlPageBuilder();
//			System.out.println(bld.buildLoginPage());
//			System.out.println(bld.buildMainPage());
//		} catch (JAXBException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}


//		DerbyDao ddd = null;
//		try {
//			ddd = new DerbyDao();
//		} catch (SQLException e) {e.printStackTrace();};
//		//ddd.backupDataBase();
//		ddd.close();
	}
}
