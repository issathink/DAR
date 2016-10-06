package services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONException;
import org.json.JSONObject;

import tools.DBStatic;

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

	public static String createUser(String mail, String login, String password, String address, String addressWork) {
		Connection conn = null;
		JSONObject result = new JSONObject();
		ResultSet listOfUsers = null;
		Statement statement = null;

		try {
			conn = DBStatic.getMySQLConnection();

			String present = "SELECT id FROM " + DBStatic.mysql_db +  ".users WHERE mail='"
					+ mail + "'";

			String quer = "INSERT INTO " + DBStatic.mysql_db +  ".users values (NULL, '"
					+ mail + "','" + login + "','" + password + "','" + address +"',"+addressWork
					+ "')";
			statement = (Statement) conn.createStatement();

			listOfUsers = statement.executeQuery(present);

			if (!listOfUsers.first() == false) {
				result.put("erreur",
						"Le login existe deja, veuillez en choisir un autre.");
			} else {
				statement.executeUpdate(quer);
				result.put("CreateUser", "La creation c'est bien passe");
			}
		} catch (SQLException e1) {
			// return Tools.erreurSQL;
			return e1.getMessage();
		} catch (JSONException e) {
			// return Tools.erreurJSON;
			return e.getMessage();
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
