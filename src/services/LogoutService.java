package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import tools.DBStatic;
import tools.Tools;

/*
 * Service de deconnexion
 */
public class LogoutService {

	public static String logoutUser(String idSession) {
		String table = DBStatic.mysql_db+".sessions";
		String requestDB = "SELECT id FROM " + table + " WHERE session_id=?";
		String requestResp = "DELETE FROM " + table + " WHERE session_id=?";

		ResultSet res;
		JSONObject jsonResult = new JSONObject();
		Connection connexion = null;
		PreparedStatement statement = null;

		try {
			connexion = DBStatic.getMySQLConnection();
			statement = connexion.prepareStatement(requestDB);
			statement.setString(1, idSession);
			res = statement.executeQuery();

			if(res.first() == false) {
				// N'existe pas
				jsonResult.put("Error :", "La connexion n'existe pas");
			} else {
				statement = connexion.prepareStatement(requestResp);
				statement.setString(1, idSession);
				statement.executeUpdate();
				jsonResult.put("OK", "Deconnexion effective");
			}
		} catch (SQLException e) {
			if (e.getErrorCode() == 0 && e.toString().contains("CommunicationsException"))
				return logoutUser(idSession);
			else
				return Tools.erreurSQL + e.getMessage();
		} catch (JSONException e) {
			return Tools.erreurJSON + e.getMessage();
		}
		
		try {
			if (statement != null)
				statement.close();
			if (connexion != null)
				connexion.close();
		} catch (SQLException e) {}

		return jsonResult.toString();
	}

}