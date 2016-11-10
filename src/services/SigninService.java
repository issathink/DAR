package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import tools.BCrypt;
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
		PreparedStatement statement = null;
		JSONObject result = new JSONObject();
		String id = "";

		try {
			conn = DBStatic.getMySQLConnection();
			String query = "select id, login, pw from " + DBStatic.mysql_db +  ".users where login=?";
			statement = conn.prepareStatement(query);
			statement.setString(1, login);
			listOfUsers = statement.executeQuery();

			if(listOfUsers.next() && BCrypt.checkpw(pw, String.valueOf(listOfUsers.getString("pw")))) {
				id = String.valueOf(listOfUsers.getString("id"));
				query = "select session_id from " + DBStatic.mysql_db + ".sessions where user_id=?";
				statement = conn.prepareStatement(query);
				statement.setInt(1, Integer.parseInt(id));
				listOfSessions = statement.executeQuery();

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
					String insert = "INSERT INTO " + DBStatic.mysql_db +  ".sessions values (NULL, ?, ?, ?)";
					statement = conn.prepareStatement(insert);
					statement.setString(1, key);
					statement.setInt(2, Integer.parseInt(id));
					statement.setString(3, Tools.getNowMillis() + "");
					if(statement.executeUpdate() < 1) {
						result.put("erreur", "Couldn't create session.");
					} else {
						result.put("ok", "Successfully connected");
						result.put("key", key);
					}
				}
			} else {
				result.put("erreur", "Unknown user: '" + login + "' or bad password.");
			}
		} catch (SQLException e) {
			if (e.getErrorCode() == 0 && e.toString().contains("CommunicationsException"))
				return authenticateUser(login, pw);
			else
				return Tools.erreurSQL + e.getMessage();
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
