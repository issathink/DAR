package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import tools.DBStatic;
import tools.Tools;

public class CommentRateService {

	public static String commentRate(String sessionId, String adresse, double lat, double lng, String commentNote, boolean comment) {
		Connection conn = null;
		ResultSet listOfComments = null;
		PreparedStatement statement = null;
		PreparedStatement stmt = null;
		JSONObject result = new JSONObject();
		String userId = "";
		String s = "";

		try {
			conn = DBStatic.getMySQLConnection();
			
			userId = Tools.getUserId(sessionId, conn);
			String query = "select id, comment, note from " + DBStatic.mysql_db 
					+  ".comments where user_id=? AND lat=? AND lng=?";
			
			if(userId != null) {
				statement = conn.prepareStatement(query);
				statement.setInt(1, Integer.parseInt(userId));
				statement.setDouble(2, lat);
				statement.setDouble(3, lng);
				if(Tools.isInParis(lat, lng)) {
					listOfComments = statement.executeQuery();

					if(listOfComments.next()) {
						String update;
						if(comment) {
							s += "1 ";
							update = "UPDATE " + DBStatic.mysql_db +  ".comments SET date=now(), comment=? where user_id=? AND lat=? AND lng=?";
							stmt = conn.prepareStatement(update);
							stmt.setString(1, commentNote);
							stmt.setInt(2, Integer.parseInt(userId));
							stmt.setDouble(3, lat);
							stmt.setDouble(4, lng);
						} else {
							s += "1else ";
							update = "UPDATE " + DBStatic.mysql_db +  ".comments SET date=now(), note=? where user_id=? AND lat=? AND lng=?";
							stmt = conn.prepareStatement(update);
							stmt.setInt(1, Integer.parseInt(commentNote));
							stmt.setInt(2, Integer.parseInt(userId));
							stmt.setDouble(3, lat);
							stmt.setDouble(4, lng);
						}
						if(stmt.executeUpdate() > 0) 
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
							insert =  "INSERT INTO " +DBStatic.mysql_db +  ".comments (user_id, comment, note, lat, lng, adresse) VALUES (?,?,"+"NULL"+",?,?,?)";
							stmt = conn.prepareStatement(insert);
							stmt.setInt(1, Integer.parseInt(userId));
							stmt.setString(2, commentNote);
							stmt.setDouble(3, lat);
							stmt.setDouble(4, lng);
							stmt.setString(5, adresse);
						} else {
							s += "2else ";
							//							insert = "INSERT INTO " + DBStatic.mysql_db +  ".comments values (NULL,'" + userId
							//								+ "', NULL, '" + commentNote + "','" + lat + "','" + lng + "','" + adresse +  "')";
							insert =  "INSERT INTO " +DBStatic.mysql_db +  ".comments (user_id, comment, note, lat, lng, adresse) VALUES ('"
									+ userId+"',"+"NULL"+","+commentNote+",'"+lat+"','"+lng+"','"+adresse+"')";
							stmt = conn.prepareStatement(insert);
							stmt.setInt(1, Integer.parseInt(userId));
							stmt.setInt(2, Integer.parseInt(commentNote));
							stmt.setDouble(3, lat);
							stmt.setDouble(4, lng);
							stmt.setString(5, adresse);
						}
						if(stmt.executeUpdate() > 0) 
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
			if (e.getErrorCode() == 0 && e.toString().contains("CommunicationsException"))
				return commentRate(sessionId, adresse, lat, lng, commentNote, comment);
			else
				return s + Tools.erreurSQL + e.getMessage();
		} catch (JSONException e) {
			return Tools.erreurJSON;
		}

		try {
			if (statement != null)
				statement.close();
			if(stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		} catch (SQLException e) {}

		return result.toString();
	}

}
