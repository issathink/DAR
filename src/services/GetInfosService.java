package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import tools.DBStatic;
import tools.Tools;

public class GetInfosService {

	public static String getInfos(String sessionId) {
		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet infos = null;
		JSONObject result = new JSONObject();
		String userId = "";

		try {
			conn = DBStatic.getMySQLConnection();
			userId = Tools.getUserId(sessionId, conn);
			String query = "select login, mail from " + DBStatic.mysql_db +  ".users where id=?";
			statement = conn.prepareStatement(query);
			
			if(userId != null) {
				statement.setInt(1, Integer.parseInt(userId));
				infos = statement.executeQuery();
				if(infos.next()) {
					result.put("login", infos.getString("login"));
					result.put("mail", infos.getString("mail"));
					result.put("ok", "Infos loaded successfully.");
				} else {
					result.put("erreur", "Problem recovering user's infos");
				}
			} else {
				result.put("erreur", "Invalid session id.");
			}
		} catch (SQLException e) {
			if (e.getErrorCode() == 0 && e.toString().contains("CommunicationsException"))
				return getInfos(sessionId);
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
