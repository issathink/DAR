package tools;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;


/**
 * <b>DBStatic est la classe permettant de recuperer une connection SQL ou Mongo.</b>
 * 
 * @return Une connection au SGBD SQL ou NoSQL.
 * 
 * 
 */


public class DBStatic {

	public static boolean mysql_pooling = false;
	// public static String mysql_host = "jdbc:mysql://132.227.201.129:33306/";
	public static String mysql_db = "projet_dar";
	public static String mysql_password = "gr3_wahb_maha$";
	public static String mysql_username = "gr3_wahb_maha";
	
	public static String mongo_db = "gr3_wahb_maha";
	public static String mongo_password = "gr3_wahb_maha$";
	public static String mongo_username = "gr3_wahb_maha";
	
	public static Connection getMySQLConnection() throws SQLException {
		DataSource ds = null;

		if (DBStatic.mysql_pooling == false) {
			try {
				ds = (DataSource) new InitialContext()
						.lookup("java:comp/env/jdbc/db");
			} catch (NamingException e) {
				e.printStackTrace();
			}

			return ds.getConnection();

		} else {
			Database database = new Database("jdbc/db");
			return database.getConnection();
		}
	}

}