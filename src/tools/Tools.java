package tools;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * <b>Tools est la classe ou l'on delegue les fonctions frequement utilisees (de
 * type boite a outils).</b>
 * 
 * 
 */

public class Tools {

	public static String erreur = "{ \"erreur\" : \"Une erreur inattendue s'est produite. Veuillez verifier et reessayer.\" }";
	public static String erreurSQL = "{ \"erreur\" : \"Une erreur inattendue s'est produite(SQL). Veuillez verifier et reessayer.\" }";
	public static String erreurMongo = "{ \"erreur\" : \"Une erreur inattendue s'est produite(NoSQL). Veuillez verifier et reessayer.\" }";
	public static String erreurJSON = "{ \"erreur\" : \"Une erreur inattendue s'est produite(JSON). Veuillez verifier et reessayer.\" }";
	public static String erreurParam = "{ \"erreur\": \"Erreur parametres. Veuillez verifier et reessayer.\" }";
	public static String ok = "{ \"ok\" : \"ok\" }";

	public static String getSessionKey() {
		UUID uid = UUID.randomUUID();
		GregorianCalendar calendar = new java.util.GregorianCalendar();

		return uid.toString() + calendar.getTimeInMillis();
	}

	public static String getDate() {
		GregorianCalendar calendar = new java.util.GregorianCalendar();
		return calendar.getTime().toString();
	}

	public static long getNowMillis() {
		GregorianCalendar calendar = new java.util.GregorianCalendar();
		return calendar.getTimeInMillis();
	}

	public static boolean moreThan30Min(long start) {
		
		if (start < (getNowMillis() - 30*60*1000))
			return true;
		return false;
	}

	public static void insertError(JSONObject result, String key, String value) {
		try {
			result.put(key, value);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static int getId(String login) {
		int id = -1;
		ResultSet res = null;
		Connection conn = null;

		Statement statement = null;

		try {
			conn = DBStatic.getMySQLConnection();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		String query = "select id from " + DBStatic.mysql_db +  ".users where login='"
				+ login + "'";

		try {
			statement = (Statement) conn.createStatement();
		} catch (SQLException e1) {
			e1.getStackTrace();
			return -1;
		}

		try {
			res = statement.executeQuery(query);
			while (res.next()) {
				id = Integer.parseInt(res.getString("id"));
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			return -1;
		}

		try {
			if (statement != null) // On ferme la connection
				statement.close();
			if (conn != null)
				conn.close();
		} catch (SQLException e) {
			return -1;
		}

		return id;
	}

	public static ArrayList<String> getFriendsLogin(String login) {
		ArrayList<String> amis = new ArrayList<String>();
		amis.add(login);

		ResultSet res = null;
		Connection conn = null;

		Statement statement = null;

		try {
			conn = DBStatic.getMySQLConnection();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		String query = "select followed from " + DBStatic.mysql_db +  ".friends where follower='"
				+ login + "'";

		try {
			statement = (Statement) conn.createStatement();
		} catch (SQLException e1) {
			e1.getStackTrace();
			return null;
		}

		try {
			res = statement.executeQuery(query);
			while (res.next()) {
				amis.add(res.getString("followed"));
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			return null;
		}

		try {
			if (statement != null)
				statement.close();
			if (conn != null)
				conn.close();
		} catch (SQLException e) {
			return null;
		}

		return amis;
	}

	public static long getDate(String key) {
		ResultSet keyToDate = null;
		long date = -1;
		Connection conn = null;

		Statement statement = null;

		try {
			conn = DBStatic.getMySQLConnection();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		String query = "select date from " + DBStatic.mysql_db +  ".sessions where session_id='"
				+ key + "'";

		try {
			statement = (Statement) conn.createStatement();
		} catch (SQLException e1) {
			e1.getStackTrace();
		}

		try {
			keyToDate = statement.executeQuery(query);

			while (keyToDate.next()) {
				date = Long.parseLong(keyToDate.getString("date"));
			}

		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		try {
			if (statement != null)
				statement.close();
			if (conn != null)
				conn.close();
		} catch (SQLException e) {
			return -1;
		}

		return date;
	}
	
	public static void removeOldConnexion(String id, String key) throws SQLException{
		Connection conn = null;
		Statement statement = null;
		
		conn = DBStatic.getMySQLConnection();
		String query = "DELETE FROM " + DBStatic.mysql_db +  ".sessions WHERE user_id='" + Integer.valueOf(id) + "' and session_id!='" + key + "'";
		statement = (Statement) conn.createStatement();
		statement.executeUpdate(query);
		
		try {
			if (statement != null)
				statement.close();
			if (conn != null)
				conn.close();
		} catch (SQLException e) {}
	}

	public static boolean extendSession(String id) {
		Connection conn = null;
		Statement statement = null;
		
		try {
			conn = DBStatic.getMySQLConnection();
			statement = (Statement) conn.createStatement();
			String update = "UPDATE " + DBStatic.mysql_db +  ".sessions SET start = '"
					+ Tools.getNowMillis()
					+ "' where session_id = '" + id
					+ "'";
			statement.executeUpdate(update);
		} catch (SQLException e1) {
			// e1.printStackTrace();
			return false;
		}
		
		try {
			if (statement != null)
				statement.close();
			if (conn != null)
				conn.close();
		} catch (SQLException e) {}

		return true;
	}
	
	public static String getUserId(String sessionId, Statement statement) {
		ResultSet listOfUsers = null;
		String query = "select user_id, start from " + DBStatic.mysql_db +  ".sessions where session_id='" + sessionId + "'";
		
		try {
			listOfUsers = statement.executeQuery(query);
			
			if(listOfUsers.next())
				if(!Tools.moreThan30Min(Long.parseLong(listOfUsers.getString("start"))))
					return String.valueOf(listOfUsers.getString("user_id"));
			return null;
		} catch (SQLException e) {
			return "Exception: " + e.getMessage();
		}
	}
	
	public static boolean isValidAddress(String adresse) {
		return true;
	}

}

/*
 * Connect to ssh ssh guest@li328.lip6.fr mdp : guest$
 */
