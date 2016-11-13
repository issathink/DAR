package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import tools.DBStatic;
import tools.LatLng;
import tools.Tools;

/*
 * Service inserant le commentaire/note dans la bonne zone
 */
public class CommentRateByZonesService {

	public static String commentRate(String sessionId, String adresse, double lat, double lng, String commentNote, boolean comment) {
		Connection conn = null;
		ResultSet listOfComments = null;
		PreparedStatement statement = null;
		PreparedStatement stmt = null;
		JSONObject result = new JSONObject();
		String userId = "";
		int num_zone = 0;

		try {
			conn = DBStatic.getMySQLConnection();

			userId = Tools.getUserId(sessionId, conn);

			LatLng latLng = Tools.getLatLng(adresse);
			if(latLng.getLat() > Tools.north || latLng.getLat() < Tools.south || 
					latLng.getLng() > Tools.east || latLng.getLng() > Tools.west) {
				result.put("erreur", "Invalid address '" + adresse + "'");
			}
			else{
				double lat_adresse = Tools.south;
				double lng_adresse = Tools.west;

				while (lat_adresse < latLng.getLat()) {
					lat_adresse += Tools.delta_lat;
					if(latLng.getLat() > lat_adresse)
						num_zone += (Tools.north-Tools.south)/0.045;
				}	

				while (lng_adresse < latLng.getLng()) {
					lng_adresse += Tools.delta_lng;
					if(latLng.getLng() > lng_adresse)
						num_zone++;
				}

				String query = "select id, comment, note from " + DBStatic.mysql_db 
						+  ".zone" + num_zone;

				if(userId != null) {
					statement = conn.prepareStatement(query);
					if(Tools.isInParis(lat, lng)) {
						listOfComments = statement.executeQuery();

						if(listOfComments.next()) {
							String update;
							if(comment) {
								update = "UPDATE " + DBStatic.mysql_db +  ".zone"+num_zone+" SET date=now(), comment=? where user_id=? AND lat=? AND lng=?";
								stmt = conn.prepareStatement(update);
								stmt.setString(1, commentNote);
								stmt.setInt(2, Integer.parseInt(userId));
								stmt.setDouble(3, lat);
								stmt.setDouble(4, lng);
							} else {
								update = "UPDATE " + DBStatic.mysql_db +  ".zone"+num_zone+" SET date=now(), note=? where user_id=? AND lat=? AND lng=?";
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
								insert =  "INSERT INTO " +DBStatic.mysql_db +  ".zone"+num_zone+" (user_id, comment, note, lat, lng, adresse) VALUES (?,?,"+"NULL"+",?,?,?)";
								stmt = conn.prepareStatement(insert);
								stmt.setInt(1, Integer.parseInt(userId));
								stmt.setString(2, commentNote);
								stmt.setDouble(3, lat);
								stmt.setDouble(4, lng);
								stmt.setString(5, adresse);
							} else {
								insert =  "INSERT INTO " +DBStatic.mysql_db +  ".zone"+num_zone+" (user_id, comment, note, lat, lng, adresse) VALUES ('"
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
			}
		} catch (SQLException e) {
			if (e.getErrorCode() == 0 && e.toString().contains("CommunicationsException"))
				return commentRate(sessionId, adresse, lat, lng, commentNote, comment);
			else
				return Tools.erreurSQL + e.getMessage();
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
