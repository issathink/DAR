package services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
		double moy = 0;
		String add = "";
		boolean vide = true;
		boolean first = true;

		try {
			if(Tools.isValidAddress(adresse)) {
				conn = DBStatic.getMySQLConnection();
				statement = (Statement) conn.createStatement();
				String query = "select lat, lng from " + DBStatic.mysql_db + ".comments";
				Set<LatLng> listAdress = new HashSet<>();

				listOfAdress = statement.executeQuery(query);
				LatLng latLng = Tools.getLatLng(adresse);
				
				if(latLng != null) {
					double latitude = latLng.getLat();
					double longitude = latLng.getLng();
					double latCur, longCur;

					while(listOfAdress.next()){
						vide = false;
						latCur = Double.valueOf(listOfAdress.getString("lat"));
						longCur = Double.valueOf(listOfAdress.getString("lng"));
						if(Tools.haversineInKM(latitude, longitude, latCur, longCur) <= Tools.Circonference)
							listAdress.add(new LatLng(latCur, longCur));
					}

					if(vide) {
						result.put("No comments", adresse);
					} else {
						for(LatLng ad : listAdress)
							add += "lat = '" + ad.getLat() + "' AND lng = '" + ad.getLng() + "' OR ";	
						add = add.substring(0, add.length()-4); // Nice one :)
	
						String queryGetComments = "select c.comment, c.note, u.login from " + DBStatic.mysql_db 
								+  ".comments c," + DBStatic.mysql_db + ".users u where " + add + " AND c.user_id=u.id";
						listOfComments = statement.executeQuery(queryGetComments);
	
						while(listOfComments.next()) {
							String c = listOfComments.getString("comment");
							// String 
							if(c != null) {
								result.append("comment", c);
							}
							// usersId.add(listOfComments.getString("user_id"));
							String n = listOfComments.getString("note");
							if(n != null){
								moy += Double.valueOf(n);
								cpt++;
							}
						}
						
						if(cpt > 0)
							moy = moy / cpt;
						else
							moy = 0;
						
						result.append("moyenne", moy);
						String s = "";
						for(int i = 0; i < usersId.size(); i++){
							if(i != usersId.size()-1)
								s += "id=" + usersId.get(i) + " OR ";
							else
								s += "id=" + usersId.get(i);
						}
	
						String query_get_login = "select id, login from " + DBStatic.mysql_db 
								+  ".users where " + s;
	
						listOfUsers = statement.executeQuery(query_get_login);
						while(listOfUsers.next()) {
							result.append("login", listOfUsers.getString("login"));
						}
					}
					
				} else {
					result.put("erreur", "Invalid address '" + adresse + "'");
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
