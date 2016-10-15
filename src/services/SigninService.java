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
		ResultSet listOfSessions = null;
		Statement statement = null;
		JSONObject result = new JSONObject();
		String id = "";
		
		try {
			conn = DBStatic.getMySQLConnection();
			String query = "select id, login from " + DBStatic.mysql_db +  ".users where login='" + login + "'";
			statement = (Statement) conn.createStatement();
			listOfUsers = statement.executeQuery(query);
			
			if(listOfUsers.next()) {
				id = String.valueOf(listOfUsers.getString("id"));
				query = "select session_id from " + DBStatic.mysql_db + ".sessions where user_id='" + id + "'";
				listOfSessions = statement.executeQuery(query);
				
				if(listOfSessions.next()) {
					String session_id = String.valueOf(listOfSessions.getString("session_id"));
					if(Tools.extendSession(session_id)) {
						result.put("ok", "Successfully connected");
						result.put("key", session_id);
					} else {
						result.put("erreur", "Couldn't create session.");
					}
				} else {
					String key = Tools.getSessionKey();
					String insert = "INSERT INTO " + DBStatic.mysql_db +  ".sessions values (NULL,'" + key
							+ "','" + id + "','" + Tools.getNowMillis() + "')";
					if(statement.executeUpdate(insert) < 1) {
						result.put("erreur", "Couldn't create session.");
					} else {
						result.put("ok", "Successfully connected");
						result.put("key", key);
					}
				}
			} else {
				result.put("erreur", "Unknown user: '" + login + "'");
			}
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
		} catch (SQLException e) {}

		return result.toString();
	}

}
