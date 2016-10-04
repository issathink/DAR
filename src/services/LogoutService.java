package services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONException;
import org.json.JSONObject;

import tools.DBStatic;

public class LogoutService {

	public static String logoutUser(String idSession) {
		String table = DBStatic.mysql_db+".sessions";

		String requestDB = "SELECT id FROM "+table+
				" WHERE session_id='"+idSession+"'";
		String requestResp = "DELETE FROM "+table+
				" WHERE session_id='"+idSession+"'";


		ResultSet res;
		JSONObject jsonResult = new JSONObject();
		Connection connexion = null;
		Statement statement = null;

		try {
			connexion = DBStatic.getMySQLConnection();
			statement = connexion.createStatement();
			res = statement.executeQuery(requestDB);

			if(res.first() == false) {
				// N'existe pas
				jsonResult.put("Error :", "La connexion n'existe pas");
			}
			else {
				statement.executeUpdate(requestResp);
				jsonResult.put("OK", "Deconnexion effective");
			}

			if(statement != null)
				statement.close();
			if(connexion != null)
				connexion.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			return e.getMessage();
		}

		return jsonResult.toString();
	}

}