package services;

import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import api.APIs;
import tools.LatLng;
import tools.Tools;

/*
 * Service de recherche (faisant appel aux APIs)
 */
public class SearchService {

	public static String search(double lat, double lng, String apiname,
			double dist) {
		double distanceAround = dist;
		JSONObject result = new JSONObject();
		LatLng position = new LatLng(lat, lng);
		boolean trouve = true;

		try {
			if (Tools.isInParis(position.lat, position.lng)) {
				if (apiname.equals("education")) {
					JSONArray ecoles = APIs.getEducationJSON(position.lat, position.lng, distanceAround);
					result.put("category", "education");
					result.put("res", ecoles);
				} else if (apiname.equals("sante")) {
					JSONArray soins = APIs.getSanteJSON(position.lat, position.lng, distanceAround);
					result.put("category", "sante");
					result.put("res", soins);
				} else if (apiname.equals("sport")) {
					JSONArray sports = APIs.getSportJSON(position.lat, position.lng, distanceAround);
					result.put("category", "sport");
					result.put("res", sports);
				} else if (apiname.equals("securite")) {
					JSONArray police = APIs.getSecuriteJSON(position.lat, position.lng, distanceAround);
					result.put("category", "securite");
					result.put("res", police);
				} else if (apiname.equals("transport")) {
					JSONArray transport = APIs.getTransportJSON(position.lat, position.lng, distanceAround);
					result.put("category", "transport");
					result.put("res", transport);
				} else if (apiname.equals("all")) { // Ce qu'il y a en haut sert a rien en faite
					// Mais j'ai peur de l'effacer....
					JSONObject all = new JSONObject();

					JSONArray ecoles = APIs.getEducationJSON(position.lat, position.lng, distanceAround);
					JSONArray soins = APIs.getSanteJSON(position.lat, position.lng, distanceAround);
					JSONArray sports = APIs.getSportJSON(position.lat, position.lng, distanceAround);
					JSONArray police = APIs.getSecuriteJSON(position.lat, position.lng, distanceAround);
					JSONArray transport = APIs.getTransportJSON(position.lat, position.lng, distanceAround);
					JSONArray poste = APIs.getPosteJSON(position.lat, position.lng, distanceAround);

					all.put("education", ecoles);
					all.put("soin", soins);
					all.put("sport", sports);
					all.put("securite", police);
					all.put("transport", transport);
					all.put("poste", poste);

					result.put("category", "all");
					result.put("res", all);
					result.put("latitudeAdresse", lat);
					result.put("longitudeAdresse", lng);

				} else {
					result.put("erreur", "Invalid api '" + apiname + "'");
					trouve = false;
				}
				if (trouve)
					result.put("ok", true);
			}
		} catch (SQLException e) {
			if (e.getErrorCode() == 0 && e.toString().contains("CommunicationsException"))
				return search(lat, lng, apiname, dist);
			else
				return Tools.erreurSQL + e.getMessage();
		} catch (JSONException e) {
			return Tools.erreurJSON + e.getMessage();
		} catch (Exception e) {
			return Tools.erreur + e.getMessage();
		}

		return result.toString();
	}

}
