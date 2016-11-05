package tests;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.json.JSONException;

import tools.DBStatic;


/**
 * <b>TestOperation est la classe de test avant de deployer une servelet.</b>
 * 
 */

public class TestOperation {

	public static void main(String[] args) {
		// System.out.println(Calcul.calcul("5", "0", "div"));

		// testCreate();
		//testJDBC();
	}

	public static void testCreate() throws JSONException {

	}


	public static String testJDBC() {
		String result = "";
		Statement statement = null;
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();

			System.out.println("Yo");
			DataSource ds = (DataSource) new InitialContext()
					.lookup("java:comp/env/jdbc/db");
			System.out.println("Yo apres");

			conn = ds.getConnection();

			String query = "SELECT * FROM " + DBStatic.mysql_db + ".users";
			statement = (Statement) conn.createStatement();

			ResultSet resultSet = statement.executeQuery(query);

			while (resultSet.next()) {
				result += resultSet.getString("id") + "\n";
			}

		} catch (Exception e) {
			result += "nop: " + e.getMessage();
			e.printStackTrace();
		} finally {
			try {
				if (statement != null)
					statement.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		System.out.println(result);
		return result;
	}
}
