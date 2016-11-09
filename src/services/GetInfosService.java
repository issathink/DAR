package services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONException;
import org.json.JSONObject;

import tools.DBStatic;
import tools.Tools;

public class GetInfosService {

	public static String getInfos(String sessionId) {
		Connection conn = null;
		Statement statement = null;
		ResultSet Infos = null;
		JSONObject result = new JSONObject();
		String userId = "";

		try {
			conn = DBStatic.getMySQLConnection();
			statement = (Statement) conn.createStatement();
			userId = Tools.getUserId(sessionId, statement);
			String query = "select login, mail from " + DBStatic.mysql_db +  ".users where id='" + userId + "'";
			if(userId != null) {
				Infos = statement.executeQuery(query);
				if(Infos.next()) {
					result.put("login", Infos.getString("login"));
					result.put("mail", Infos.getString("mail"));
					result.put("ok", "Infos loaded successfully.");
				}
				else {
					result.put("erreur", "Problem recovering user's infos");
				}
			} else {
				result.put("erreur", "Invalid session id.");
			}

		} catch (SQLException e) {
			int error = e.getErrorCode();
			if (error == 0 && e.toString().contains("CommunicationsException")){
				return getInfos(sessionId);
			}
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
