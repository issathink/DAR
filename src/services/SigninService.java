package services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONException;
import org.json.JSONObject;

import tools.DBStatic;
import tools.Tools;

/**
 * <b>AuthenticateUserService est la classe permettant d'authentifier un
 * utilisateur.</b>
 * 
 * @return 
 *   result - Un String contenant le triplet {id, login key} ou un message
 *         d'erreur.
 * 
 * @param 
 *   login - Le login de la personne voulant se connecter. 
 *   pw - Le mot de passe de cet utilisateur.
 * 
 */

public class SigninService {

	public static String authenticateUser(String login, String pw) {
		Connection conn = null;
		ResultSet listOfUsers = null;
		ResultSet profile = null;
		ResultSet profile2 = null;
		// ResultSet loginToId = null;
		String id = "";
		JSONObject result = new JSONObject();
		boolean error = false;
		Statement statement = null;

		try {
			conn = DBStatic.getMySQLConnection();

			String quer = "select login, pw from " + DBStatic.mysql_db +  ".users";

			statement = (Statement) conn.createStatement();
			listOfUsers = statement.executeQuery(quer);

			/********
			 * Si on s'authentifie alors qu'on est toujours connecte, on redonne
			 * du temps en plus
			 *******/
			statement = (Statement) conn.createStatement();
			String quer2 = "select id from " + DBStatic.mysql_db +  ".users where login='"
					+ login + "' and pw='" + pw + "'";
			profile = statement.executeQuery(quer2);

			while (profile.next())
				id = String.valueOf(profile.getString("id"));

			if (!id.equals("")) {
				String session_id = "";
				String quer3 = "select session_id, start from " + DBStatic.mysql_db +  ".sessions where user_id='"
						+ login + "'";
				profile2 = statement.executeQuery(quer3);
				long max = -1; // On veut la cle la plus recente si il y en a
								// plusieurs
				long date;
				while (profile2.next()) {
					date = profile2.getLong("start");
					if (max < date) {
						max = date;
						session_id = String.valueOf(profile2
								.getString("session_id"));
					}
				}
				if (!session_id.equals("")) {
					if (!Tools.moreThan30Min(Tools.getDate(session_id))) {

						Tools.removeOldConnexion(login, session_id);

						String log1 = "UPDATE " + DBStatic.mysql_db +  ".sessions SET start = '"
								+ Tools.getNowMillis()
								+ "' where session_id = '" + session_id
								+ "'";
						statement.executeUpdate(log1);
						result.put("key", session_id);
						result.put("login", login);
						result.put("id", id);

						try {
							if (statement != null)
								statement.close();
							if (conn != null)
								conn.close();
						} catch (SQLException e) {
						}

						return result.toString();
					}
				}

			}
			
			String key = valid(login, pw, listOfUsers, result, error);
			Tools.removeOldConnexion(id, key);
			
			if (key != null) {
				String log = "INSERT INTO " + DBStatic.mysql_db +  ".sessions values (NULL,'" + key
						+ "','" + id + "','" + Tools.getNowMillis() + "')";
				statement.executeUpdate(log);
			}
			
			/*String query = "select id from " + DBStatic.mysql_db +  ".users where login='"
					+ login + "'";

			loginToId = statement.executeQuery(query);*/
			result.put("login", login);
			

			/*while (loginToId.next()) {
				id = loginToId.getString("id"); // login contient le login de la
				// personne.
			}*/

			result.put("id", id);

			/*DB db = null;

			db = DBStatic.getMongoDB();

			BasicDBObject dbObject = new BasicDBObject();

			dbObject.put("login", login);
			dbObject.put("key", key);
			dbObject.put("date", Tools.getDate());

			DBCollection collection = db.getCollection("sessions");
			collection.insert(dbObject);*/

		/*} catch (MongoException e1) {
			return Tools.erreurMongo;*/
			
		} catch (SQLException e1) {
			return Tools.erreurSQL;
		} catch (JSONException e) {
			return Tools.erreurJSON;
		}

		try {
			if (statement != null)
				statement.close();
			if (conn != null)
				conn.close();
		} catch (SQLException e) {
		}

		return result.toString();
	}

	private static void error(String mess, int err, JSONObject result,
			String errorText, boolean error) {
		try {
			result.put(mess, err + "");
		} catch (JSONException e) {
			error = true;
			errorText = e.getMessage();
		}
	}

	private static String valid(String login, String pwd,
			ResultSet listOfUsers, JSONObject result, boolean error) {
		boolean exists = false;
		String key = null;

		try {
			while (listOfUsers.next())
				if (listOfUsers.getString("login").equals(login)
						&& listOfUsers.getString("pw").equals(pwd))
					exists = true;
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		if (exists) {
			error = false;
			try {
				key = Tools.getSessionKey();
				result.put("key", key);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			error = true;
			error("erreur", 2, result, "", error);
		}

		return key;
	}

}
