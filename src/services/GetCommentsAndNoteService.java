package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import tools.DBStatic;
import tools.LatLng;
import tools.Tools;

/*
 * Service qui recupere les commentaires et la note moyenne d'une adresse
 */
public class GetCommentsAndNoteService {

	public static String getComments(String adresse, String distance) {
		Connection conn = null;
		ResultSet listOfAdress = null;
		ResultSet listOfComments = null;
		PreparedStatement statement = null;
		JSONObject result = new JSONObject();
		int cpt = 0;
		double moy = 0;
		String add = "";
		boolean vide = true;
		String s = "";

		double dist = mToKm(distance);
		try {
			if (Tools.isValidAddress(adresse)) {
				conn = DBStatic.getMySQLConnection();
				String query = "select lat, lng from " + DBStatic.mysql_db + ".comments";
				statement = conn.prepareStatement(query);
				Set<LatLng> listAdress = new HashSet<>();

				listOfAdress = statement.executeQuery();
				LatLng latLng = Tools.getLatLng(adresse);

				if (latLng != null) {
					double latitude = latLng.getLat();
					double longitude = latLng.getLng();
					double latCur, longCur;

					while (listOfAdress.next()) {
						latCur = Double.valueOf(listOfAdress.getString("lat"));
						longCur = Double.valueOf(listOfAdress.getString("lng"));
						if (Tools.haversineInKM(latitude, longitude, latCur, longCur) <= dist) {
							listAdress.add(new LatLng(latCur, longCur));
							vide = false;
						}
					}

					if (vide) {
						result.put("nocomments", adresse);
					} else {
						for (LatLng ad : listAdress)
							add += "(lat=" + ad.getLat() + " AND lng=" + ad.getLng() + ") OR ";
						add = add.substring(0, add.length() - 4); // Nice one :)

						String queryGetComments = "select c.comment, c.note, c.adresse, c.date, u.login from "
								+ DBStatic.mysql_db + ".comments c," + DBStatic.mysql_db + ".users u where (" + add
								+ ") AND c.user_id=u.id;";
						s += queryGetComments;
						statement = conn.prepareStatement(queryGetComments);
						listOfComments = statement.executeQuery();
						result.put("query", queryGetComments);


						List<JSONObject> listJ = new ArrayList<>();
						while (listOfComments.next()) {
							String c = listOfComments.getString("comment");
							JSONObject jObj = new JSONObject();

							String n = listOfComments.getString("note");

							String myDate = listOfComments.getString("date");
							//							myDate = myDate.split(" ")[0];
							//							String [] tmpS = myDate.split("-");
							//							myDate = tmpS[2]+"/"+tmpS[1]+"/"+tmpS[0];
							if (c != null) {
								jObj.put("comment", StringEscapeUtils.escapeHtml4(c));
								jObj.put("adresse", StringEscapeUtils.escapeHtml4(listOfComments.getString("adresse")));
								jObj.put("date", StringEscapeUtils.escapeHtml4(myDate));
								jObj.put("login", StringEscapeUtils.escapeHtml4(listOfComments.getString("login")));
								String tmpNote = (n != null) ? n : "";
								jObj.put("note", tmpNote);
								listJ.add(jObj);
								//result.append("comment", jObj);
							}
							if (n != null) {
								moy += Double.valueOf(n);
								cpt++;
							}
						}

						Collections.sort(listJ, new Comparator<JSONObject>() {
							@Override
							public int compare(JSONObject o1, JSONObject o2) {
								try {
									String d1 = o1.getString("date");
									String d2 = o2.getString("date");

									SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									Date date1 = sdf.parse(d1);
									Date date2 = sdf.parse(d2);

									return date2.compareTo(date1);
								} catch (JSONException e) {
									e.printStackTrace(); 
								} catch (ParseException e) {
									e.printStackTrace();
								} 				
								return 0;
							}
						});
						for(JSONObject j : listJ) {
							String myDate = j.getString("date").split(" ")[0];
							String [] tmpS = myDate.split("-");
							myDate = tmpS[2]+"/"+tmpS[1]+"/"+tmpS[0];
							j.put("date", myDate);
							result.append("comment", j);
						}

						result.put("cpt", cpt);
						if (cpt > 0)
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
			return Tools.erreurJSON + e.getMessage();
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
		d = d / 1000;
		return d;
	}

}