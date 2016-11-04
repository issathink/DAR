package services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONException;
import org.json.JSONObject;

import tools.DBStatic;
import tools.Tools;

public class SearchService {

	public static String search(String sessionId, String adresse) {
		Connection conn = null;
		ResultSet listOfComments = null;
		Statement statement = null;
		JSONObject result = new JSONObject();
		String userId = null;
		try {
			conn = DBStatic.getMySQLConnection();
			statement = (Statement) conn.createStatement();
			result.put("ok", true);
			
			result.put("ret", Tools.getLatLng(adresse));

			/*if ((userId = Tools.getUserId(sessionId, statement)) != null) {

			} else {
				result.put("erreur", "Unknown or expired session.");
				return result.toString();
			}*/
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
