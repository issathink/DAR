package services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONException;
import org.json.JSONObject;

import tools.DBStatic;
import tools.Tools;

public class CommentRateService {
	
	public static String commentRate(String sessionId, String adresse, String commentNote, boolean comment) {
		Connection conn = null;
		ResultSet listOfComments = null;
		Statement statement = null;
		JSONObject result = new JSONObject();
		String userId = "";
		
		try {
			conn = DBStatic.getMySQLConnection();
			statement = (Statement) conn.createStatement();
			userId = Tools.getUserId(sessionId, statement);
			String query = "select id, comment, note from " + DBStatic.mysql_db 
					+  ".comments where user_id='" + userId + "' AND adresse='" + adresse + "'";
			
			if(userId != null) {
				if(Tools.isValidAddress(adresse)) {
					listOfComments = statement.executeQuery(query);
					
					if(listOfComments.next()) {
						String update;
						if(comment) {
							update = "UPDATE " + DBStatic.mysql_db +  ".comments SET comment='"
									+ commentNote + "' where user_id='" + userId + "' AND adresse='" + adresse + "'";
						} else {
							update = "UPDATE " + DBStatic.mysql_db +  ".comments SET note='"
								+ commentNote + "' where user_id='" + userId + "' AND adresse='" + adresse + "'";
						}
						if(statement.executeUpdate(update) > 0) 
							result.put("ok", "Thanks for participating.");
						else
							result.put("erreur", "Unexpected error please try again later");
					} else {
						String insert;
						if(comment) {
							insert = "INSERT INTO " + DBStatic.mysql_db +  ".comments values (NULL,'" + userId
									+ "','" + adresse + "','" + commentNote + "', NULL)";
						} else {
							insert = "INSERT INTO " + DBStatic.mysql_db +  ".comments values (NULL,'" + userId
								+ "','" + adresse + "', NULL, '" + commentNote + "')";
						}
						if(statement.executeUpdate(insert) > 0) 
							result.put("ok", "Thanks for participating.");
						else
							result.put("erreur", "Unexpected error please try again later");
					}
				} else {
					result.put("erreur", "Invalid address '" + adresse + "'");
				}
			} else {
				result.put("erreur", "Invalid session id.");
			}
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
		} catch (SQLException e) {}

		return result.toString();
	}

}
