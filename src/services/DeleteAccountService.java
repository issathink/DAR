package services;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONException;
import org.json.JSONObject;

import tools.DBStatic;
import tools.Tools;

public class DeleteAccountService {

	public static String deleteAccount(String sessionId, String pw) {
		Connection conn = null;
		Statement statement = null;
		JSONObject result = new JSONObject();
		String userId = "";
		String s = "";

		try {
			conn = DBStatic.getMySQLConnection();
			statement = (Statement) conn.createStatement();
			userId = Tools.getUserId(sessionId, statement);
			if(userId != null) {
				String delete = "DELETE FROM " + DBStatic.mysql_db +  ".users where id='" + userId + "'";
				if(statement.executeUpdate(delete) > 0) 
					result.put("ok", "Delete successfully.");
			} else {
				result.put("erreur", "Invalid session id.");
			}
		} catch (SQLException e) {
			return s + Tools.erreurSQL + e.getMessage();
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
