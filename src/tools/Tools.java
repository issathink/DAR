package tools;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

	public static String erreur = "{ \"erreur\" : \"Une erreur inattendue s'est produite. Veuillez verifier et réessayer.\" }";
	public static String erreurSQL = "{ \"erreur\" : \"Une erreur inattendue s'est produite(SQL). Veuillez verifier et réessayer.\" }";
	public static String erreurMongo = "{ \"erreur\" : \"Une erreur inattendue s'est produite(NoSQL). Veuillez verifier et réessayer.\" }";
	public static String erreurJSON = "{ \"erreur\" : \"Une erreur inattendue s'est produite(JSON). Veuillez verifier et réessayer.\" }";
	public static String erreurParam = "{ \"erreur\": \"Erreur paramêtres. Veuillez vérifier et réessayer.\" }";
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

	public static boolean moreThan30Min(long d) {
		d += 30 * 60 * 1000;

		if (d < getNowMillis())
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

	public static String getLogin(String key) throws JSONException {
		ResultSet KeyToUser = null;
		ResultSet is_connected = null;
		String login = "";
		String start = "";
		Connection conn = null;

		Statement statement = null;

		try {
			conn = DBStatic.getMySQLConnection();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		String query2 = "select login from " + DBStatic.mysql_db +  ".sessions where session_id='"
				+ key + "'";
		String query3 = "select start from " + DBStatic.mysql_db +  ".sessions where session_id='"
				+ key + "'";

		try {
			statement = (Statement) conn.createStatement();
		} catch (SQLException e1) {
			e1.getStackTrace();
		}

		try {
			KeyToUser = statement.executeQuery(query2);

			while (KeyToUser.next()) {
				login += KeyToUser.getString("login");
			}

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		try {
			is_connected = statement.executeQuery(query3);
			while (is_connected.next()) {
				start += is_connected.getString("start");
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		if (moreThan30Min(Long.valueOf(start))) {
			try {
				if (statement != null)
					statement.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return null;
		}
		return login;
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

	public static ArrayList<Integer> getFriendsId(String login) {
		ArrayList<Integer> amis = new ArrayList<Integer>();
		amis.add(getId(login));

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
				amis.add(getId(res.getString("followed")));
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

		String query = "select date from " + DBStatic.mysql_db +  ".logs where session_id='"
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

	/*public static String getLikeLogin(String id_comment) {

		DB db = null;
		try {
			db = DBStatic.getMongoDB();
		} catch (MongoException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		DBCollection collection = db.getCollection("comments");

		BasicDBObject dbObj = new BasicDBObject();
		dbObj.put("id", id_comment);

		DBCursor dbCursor = collection.find(dbObj).limit(1);
		if (dbCursor.hasNext())
			return dbCursor.next().get("auteur_login").toString();
		else
			return null;
	}

	public static int getScore(String id) {
		DB db = null;
		try {
			db = DBStatic.getMongoDB();
		} catch (MongoException e) {
			e.printStackTrace();
			return 0;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
		DBCollection collection = db.getCollection("like");

		BasicDBObject dbObj = new BasicDBObject();
		dbObj.put("id_comment", id);

		DBCursor dbCursor = collection.find(dbObj);

		return dbCursor.count();
	}


	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(
			Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			@Override
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		
		return result;
	}*/
	
	
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
		} catch (SQLException e) {
		}
	}

}

/*
 * Connect to ssh ssh guest@li328.lip6.fr mdp : guest$
 */

