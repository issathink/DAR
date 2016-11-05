package services;

import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import api.APIs;
import tools.LatLng;
import tools.Tools;

public class SearchService {

	public static String search(String adresse) {
		double distanceAround = 500;
		JSONObject result = new JSONObject();
		LatLng position = Tools.getLatLng(adresse);

		try {
			if (Tools.isInParis(position.lat, position.lng)) {
				JSONArray ecoles = APIs.getEducationJSON(position.lat, position.lng, distanceAround);
				JSONArray soins = APIs.getSanteJSON(position.lat, position.lng, distanceAround);
				JSONArray sports = APIs.getSportJSON(position.lat, position.lng, distanceAround);
				JSONArray police = APIs.getSecuriteJSON(position.lat, position.lng, distanceAround);

				result.put("ecoles", ecoles);
				result.put("soins", soins);
				result.put("sports", sports);
				result.put("police", police);
				result.put("ok", true);
			}
		} catch (SQLException e) {
			return Tools.erreurSQL + e.getMessage();
		} catch (JSONException e) {
			return Tools.erreurJSON + e.getMessage();
		} catch (Exception e) {
			return Tools.erreur + e.getMessage();
		}

		return result.toString();
	}

}
