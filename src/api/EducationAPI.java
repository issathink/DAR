package api;

import org.json.JSONArray;
import org.json.JSONException;

import tools.Tools;

public class EducationAPI {

	public static JSONArray getJSON(double latitude, double longitude, double distance) throws JSONException, Exception {
		JSONArray j1 = EtablissementScolairePreBacAPI.getEtablissementScolaireJSON(latitude, longitude, distance);
		JSONArray j2 = EtablissementScolairePostBacAPI.getEtablissementScolaireJSON(latitude, longitude, distance);
		return Tools.concatArray(j1, j2);
	}

}
