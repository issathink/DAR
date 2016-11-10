package services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import tools.DBStatic;
import tools.LatLng;
import tools.Tools;

public class GetCommentsAndNoteService {

	public static String getComments(String adresse, String distance) {
		Connection conn = null;
		ResultSet listOfAdress = null;
		ResultSet listOfComments = null;
		Statement statement = null;
		JSONObject result = new JSONObject();
		int cpt = 0;
		double moy = 0;
		String add = "";
		boolean vide = true;
		String s = "";

		double dist = mToKm(distance);
		try { // TODO preparedStatement
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
						if(Tools.haversineInKM(latitude, longitude, latCur, longCur) <= dist)
							listAdress.add(new LatLng(latCur, longCur));
					}

					result.put("1", listAdress);

					if(vide) {
						result.put("No comments", adresse);
					} else {
						for(LatLng ad : listAdress)
							add += "(lat=" + ad.getLat() + " AND lng=" + ad.getLng() + ") OR ";
						add = add.substring(0, add.length()-4); // Nice one :)

						String queryGetComments = "select c.comment, c.note, c.adresse, u.login from " + DBStatic.mysql_db 
								+  ".comments c," + DBStatic.mysql_db + ".users u where (" + add + ") AND c.user_id=u.id;";
						s += queryGetComments;
						listOfComments = statement.executeQuery(queryGetComments);
						result.put("query", queryGetComments);

						while(listOfComments.next()) {
							String c = listOfComments.getString("comment");
							JSONObject jObj = new JSONObject();

							String n = listOfComments.getString("note");

							if(c != null) {
								jObj.put("comment",  StringEscapeUtils.escapeHtml4(c)+"DIST = "+dist);
								jObj.put("adresse", StringEscapeUtils.escapeHtml4(listOfComments.getString("adresse")));
								jObj.put("login", StringEscapeUtils.escapeHtml4(listOfComments.getString("login")));
								String tmpNote = (n != null) ? n : "";
								jObj.put("note", tmpNote);
								result.append("comment", jObj);
							}
							// usersId.add(listOfComments.getString("user_id"));
							if(n != null){
								moy += Double.valueOf(n);
								cpt++;
							}
						}

						result.put("cpt", cpt);
						if(cpt > 0)
							moy = moy / cpt;
						else
							moy = 0;
						result.append("moyenne", moy);
					}
				} else {
					result.put("erreur", "Invalid address '" + adresse + "'");
				}
			} else {
				result.put("erreur", "Invalid address '" + adresse + "'");
			}
		} catch (SQLException e) {
			if (e.getErrorCode() == 0 && e.toString().contains("CommunicationsException"))
				return getComments(adresse, distance);
			else
				return Tools.erreurSQL + e.getMessage() + "\n" + s;
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

	private static double mToKm(String distance) {
		Double d = Double.parseDouble(distance);
		d = d/100;
		return d;
	}

}
