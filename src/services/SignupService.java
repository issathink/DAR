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
 * <b>CreateUserService est le service qui cree un nouvel utilisateur.</b>
 * 
 * @return 
 *   result - Un JSON contenant "ok" si tout s'est bien passe ou le message 
 *   			d'erreur correspondant.
 * 
 * 
 * @param 
 *   login - Le login de l'utilisateur. 
 *   password - Le mot de passe
 *   nom - Le nom de l'utilisateur. 
 *   prenom - Son prenom.
 *   email - Son email.
 *   addressWork - Adresse du lieu de travail
 * 
 */

public class SignupService {

	public static String createUser(String mail, String login, String pw) {
		Connection conn = null;
		JSONObject result = new JSONObject();
		ResultSet listOfUsers = null;
		Statement statement = null;
		int i = 0;

		try {
			conn = DBStatic.getMySQLConnection();
			String present = "SELECT id FROM " + DBStatic.mysql_db +  ".users WHERE mail='"
					+ mail + "'";
			String insert = "INSERT INTO " + DBStatic.mysql_db +  ".users values (NULL, '"
					+ mail + "','" + login + "','" + pw + "')";
			statement = (Statement) conn.createStatement();
			listOfUsers = statement.executeQuery(present);

			while(listOfUsers.next())
				i++;
			
			if (i > 0) {
				result.put("erreur", "Il existe un email associe a ce compte.");
			} else {
				statement.executeUpdate(insert);
				JSONObject obj = new JSONObject(SigninService.authenticateUser(login, pw));
				result.put("ok", "La creation s'est bien passe");
				result.put("key", obj.get("key"));
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
		} catch (SQLException e) {
		}

		return result.toString();
	}

}
