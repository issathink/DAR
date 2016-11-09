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
	
	public static String commentRate(String sessionId, String adresse, double lat, double lng, String commentNote, boolean comment) {
		Connection conn = null;
		ResultSet listOfComments = null;
		Statement statement = null;
		JSONObject result = new JSONObject();
		String userId = "";
		String s = "";
		
		try {
			conn = DBStatic.getMySQLConnection();
			statement = (Statement) conn.createStatement();
			userId = Tools.getUserId(sessionId, statement);
			String query = "select id, comment, note from " + DBStatic.mysql_db 
					+  ".comments where user_id='" + userId + "' AND lat='" + lat + "' AND lng='" + lng + "'";
			
			if(userId != null) {
				if(Tools.isInParis(lat, lng)) {
					listOfComments = statement.executeQuery(query);
					
					if(listOfComments.next()) {
						String update;
						if(comment) {
							s += "1 ";
							update = "UPDATE " + DBStatic.mysql_db +  ".comments SET comment='"
									+ commentNote + "' where user_id='" + userId + "' AND lat='" + lat + "' AND lng='" + lng + "'";
						} else {
							s += "1else ";
							update = "UPDATE " + DBStatic.mysql_db +  ".comments SET note='"
								+ commentNote + "' where user_id='" + userId + "' AND  lat='" + lat + "' AND lng='" + lng + "'";
						}
						if(statement.executeUpdate(update) > 0) 
							result.put("ok", "Thanks for participating.");
						else
							result.put("erreur", "Unexpected error please try again later");
					} else {
						String insert;
						if(comment) {
						//	"INSERT INTO "+tableMessage+" (id_sender, id_receiver, message) VALUES ("+
							//		senderId+","+receiverId+",'"+message+"')";
						//	s += "2 ";
							//insert = "INSERT INTO " + DBStatic.mysql_db +  ".comments values (NULL,'" + userId
								//	+ "','" + commentNote + "', NULL, '" + lat + "','" + lng + "','" + adresse + "')";
							insert =  "INSERT INTO " +DBStatic.mysql_db +  ".comments (user_id, comment, note, lat, lng, adresse) VALUES ('"
									+ userId+"','"+commentNote+"',"+"NULL"+",'"+lat+"','"+lng+"','"+adresse+"')";
						} else {
							s += "2else ";
//							insert = "INSERT INTO " + DBStatic.mysql_db +  ".comments values (NULL,'" + userId
//								+ "', NULL, '" + commentNote + "','" + lat + "','" + lng + "','" + adresse +  "')";
							insert =  "INSERT INTO " +DBStatic.mysql_db +  ".comments (user_id, comment, note, lat, lng, adresse) VALUES ('"
									+ userId+"',"+"NULL"+","+commentNote+",'"+lat+"','"+lng+"','"+adresse+"')";
						}
						if(statement.executeUpdate(insert) > 0) 
							result.put("ok", "Thanks for participating.");
						else
							result.put("erreur", "Unexpected error please try again later");
					}
				} else {
					result.put("erreur", "Invalid address.");
				}
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
