package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tools.DBStatic;
import tools.NameOfTables;

/*
 * Service de recherche d'utilisateur
 */
public class GetLoginBeginByService {

	public static String getLoginList(String beginBy) {
		ResultSet resRequestToGetLogins;
		JSONArray jsonResult = new JSONArray();
		Connection connexion = null;
		PreparedStatement statement = null;

		try {
			/* Connexion BD et reglage ... */
			connexion = DBStatic.getMySQLConnection();

			String tableUsers = DBStatic.mysql_db + "." + NameOfTables.users;
			String requestSQL = "SELECT * from " + tableUsers + " WHERE (login LIKE ?)";
			statement = connexion.prepareStatement(requestSQL);
			statement.setString(1, beginBy + "%");
			resRequestToGetLogins = statement.executeQuery();

			while (resRequestToGetLogins.next()) {
				String login = resRequestToGetLogins.getString("login");
				login = StringEscapeUtils.escapeHtml4(login);

				JSONObject jObj = new JSONObject();
				jObj.put("value", login);
				jObj.put("label", login);
				jsonResult.put(jObj);
			}

			if (statement != null)
				statement.close();
			if (connexion != null)
				connexion.close();
		} catch (SQLException e) {
			if (e.getErrorCode() == 0 && e.toString().contains("CommunicationsException"))
				return getLoginList(beginBy);
			else
				return e.getMessage();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonResult.toString();
	}

}
