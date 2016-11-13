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
 * Service verifiant si une session est valide
 */
public class IsConnectedService {

	public static String isConnected(String id) {
		Connection conn = null;
		PreparedStatement statement = null;
		JSONObject result = new JSONObject();
		ResultSet connections = null;

		try {
			String query = "select start from " + DBStatic.mysql_db +  ".sessions where session_id=?";
			conn = DBStatic.getMySQLConnection();
			long start;
			statement = conn.prepareStatement(query);
			statement.setString(1, id);
			connections = statement.executeQuery();

			if(connections.next()) {
				start = Long.parseLong(String.valueOf(connections.getString("start")));
				if(!Tools.moreThan30Min(start)) {
					result.put("log", true);
					if(Tools.extendSession(id)) {
						result.put("ok", 0);
					}
				} else {
					result.put("erreur", "session expired");
				}
			} else {
				result.put("erreur", "invalid session id");
			}
		} catch (SQLException e) {
			if (e.getErrorCode() == 0 && e.toString().contains("CommunicationsException"))
				return isConnected(id);
			else
				return Tools.erreurSQL + e.getMessage();
		} catch (JSONException e) {
			return Tools.erreurJSON + e.getMessage();
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
