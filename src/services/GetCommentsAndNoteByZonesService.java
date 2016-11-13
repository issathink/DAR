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
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import tools.DBStatic;
import tools.LatLng;
import tools.Tools;

/*
 * Service qui recupere les commentaires et la note d'une note
 */
public class GetCommentsAndNoteByZonesService {

	public static String getComments(String adresse, String distance) {
		Connection conn = null;
		ResultSet listOfAdress = null;
		ResultSet listOfComments = null;
		PreparedStatement statement = null;
		JSONObject result = new JSONObject();
		int cpt = 0;
		double moy = 0;
		String s = "";
		int num_zone = 0;

		try {
			if (Tools.isValidAddress(adresse)) {
				conn = DBStatic.getMySQLConnection();
				LatLng latLng = Tools.getLatLng(adresse);
				if (latLng != null) {
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

						String query = "select adresse from " + DBStatic.mysql_db + ".zone"+num_zone + " limit 1";
						statement = conn.prepareStatement(query);

						listOfAdress = statement.executeQuery();
						if (listOfAdress.next()) {
							result.put("nocomments", adresse);
						}
						else {

							String queryGetComments = "select c.comment, c.note, c.adresse, c.date, u.login from "
									+ DBStatic.mysql_db + ".zone" + num_zone + " c," + DBStatic.mysql_db + ".users u where c.user_id=u.id;";
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
								if (c != null) {
									jObj.put("comment", StringEscapeUtils.escapeHtml4(c));
									jObj.put("adresse", StringEscapeUtils.escapeHtml4(listOfComments.getString("adresse")));
									jObj.put("date", StringEscapeUtils.escapeHtml4(myDate));
									jObj.put("login", StringEscapeUtils.escapeHtml4(listOfComments.getString("login")));
									String tmpNote = (n != null) ? n : "";
									jObj.put("note", tmpNote);
									listJ.add(jObj);
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
					} 
				}else {
					result.put("erreur", "Invalid address '" + adresse + "'");
				}
			}else {
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


}
