package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
		PreparedStatement statement = null;
		int i = 0;

		try {
			conn = DBStatic.getMySQLConnection();
			String present = "SELECT id FROM " + DBStatic.mysql_db +  ".users WHERE mail=?";
			String insert = "INSERT INTO " + DBStatic.mysql_db +  ".users values (NULL, ?, ?, ?)";
			statement = conn.prepareStatement(present);
			statement.setString(1, mail);
			listOfUsers = statement.executeQuery();

			while(listOfUsers.next())
				i++;

			if (i > 0) {
				result.put("erreur", "Il existe un email associe a ce compte.");
			}else if (pw.length() < 6){
				result.put("erreur", "Mot de passe trop court");
			} else {

				statement = conn.prepareStatement(insert);
				statement.setString(1, mail);
				statement.setString(2, login);
				statement.setString(3, pw);
				statement.executeUpdate();
				JSONObject obj = new JSONObject(SigninService.authenticateUser(login, pw));
				result.put("ok", "La creation s'est bien passe");
				result.put("key", obj.get("key"));
			}
		} catch (SQLException e) {
			if (e.getErrorCode() == 0 && e.toString().contains("CommunicationsException"))
				return createUser(mail, login, pw);
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
		} catch (SQLException e) {
		}

		return result.toString();
	}

}
