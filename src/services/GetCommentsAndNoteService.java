package services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import tools.DBStatic;
import tools.LatLng;
import tools.Tools;

public class GetCommentsAndNoteService {

	public static String getComments(String adresse) {
		Connection conn = null;
		ResultSet listOfAdress = null;
		ResultSet listOfComments = null;
		ResultSet listOfUsers = null;
		Statement statement = null;
		JSONObject result = new JSONObject();
		ArrayList<String> usersId = new ArrayList<>();
		int cpt = 0;
		double note_moyenne = 0;
		String my_adress = "";
		boolean vide = true;

		try {
			if(Tools.isValidAddress(adresse)) {
				conn = DBStatic.getMySQLConnection();
				statement = (Statement) conn.createStatement();
				String query = "select lat, lng from " + DBStatic.mysql_db 
						+  ".comments";
				Set<LatLng> listAdress = new HashSet<>();

				listOfAdress = statement.executeQuery(query);
				double latitude = Tools.getLatLng(adresse).getLat();
				double longitude = Tools.getLatLng(adresse).getLng();
				double lat_cur, long_cur;
				
				while(listOfAdress.next()){
					vide = false;
					lat_cur = Double.valueOf(listOfAdress.getString("lat"));
					long_cur = Double.valueOf(listOfAdress.getString("lng"));
					if(Tools.haversineInKM(latitude, longitude, lat_cur, long_cur) <= Tools.Circonference)
						listAdress.add(new LatLng(lat_cur, long_cur));
				}
				
				if(vide){
					result.put("No comments", adresse);
					return result.toString();
				}

				for(LatLng ad : listAdress){
					my_adress += "lat = '" + ad.getLat() + "' AND lng = '" + ad.getLng() + "' OR ";
				}
				
				my_adress = my_adress.substring(0, my_adress.length()-4);
				
				String query_get_comments = "select comment, note, user_id from " + DBStatic.mysql_db 
						+  ".comments where " + my_adress;
				listOfComments = statement.executeQuery(query_get_comments);
				
				while(listOfComments.next()) {
					result.append("comment", listOfComments.getString("comment"));
					usersId.add(listOfComments.getString("user_id"));
					note_moyenne += Double.valueOf(listOfComments.getString("note"));
					cpt++;
				}
				note_moyenne = note_moyenne/cpt;
				note_moyenne = (double)((int)(note_moyenne*10))/10;
				result.append("moyenne", note_moyenne);
				String s = " ";
				for(String uId: usersId){
					if(!uId.equals(usersId.get(usersId.size()-1)))
						s += "id=" + uId + " OR ";
					else
						s += "id=" + uId;
				}

				String query_get_login = "select id, login from " + DBStatic.mysql_db 
						+  ".users where " + s;

				listOfUsers = statement.executeQuery(query_get_login);
				while(listOfUsers.next()) {
					result.append("login", listOfUsers.getString("login"));
				}
			} else {
				result.put("erreur", "Invalid address '" + adresse + "'");
			}

		} catch (SQLException e1) {
			return Tools.erreurSQL + e1.getMessage();
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
