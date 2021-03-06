package api;

import org.json.JSONArray;
import org.json.JSONException;

import tools.Tools;

public class APIs {

	public static JSONArray getEducationJSON(double latitude, double longitude, double distance) throws JSONException, Exception {
		JSONArray j1 = EtablissementScolairePreBacAPI.getEtablissementScolaireJSON(latitude, longitude, distance);
		JSONArray j2 = EtablissementScolairePostBacAPI.getEtablissementScolaireJSON(latitude, longitude, distance);
		return Tools.concatArray(j1, j2);
	}

	public static JSONArray getSanteJSON(double latitude, double longitude, double distance) throws JSONException, Exception {
		JSONArray j1 = PharmacieAPI.getPharmacieJSON(latitude, longitude, distance);
		JSONArray j2 = CentreDeSoinAPI.getCentreDeSoinJSON(latitude, longitude, distance);
		JSONArray j3 = EtablissementHospitalierAPI.getEtablissementHospitalierJSON(latitude, longitude, distance);
		return Tools.concatArray(j1, j2, j3);
	}

	public static JSONArray getSportJSON(double latitude, double longitude, double distance) throws Exception {
		return SportAPI.getSportJSON(latitude, longitude, distance);
	}

	public static JSONArray getSecuriteJSON(double latitude, double longitude, double distance) throws JSONException, Exception {
		JSONArray j1 = ComissariatParisAPI.getComissariatParisJSON(latitude, longitude, distance);
		JSONArray j2 = ComissariatPetiteCouronneAPI.getComissariatPetiteCouronneJSON(latitude, longitude, distance);
		return Tools.concatArray(j1, j2);
	}
	
	public static JSONArray getTransportJSON(double latitude, double longitude, double distance) throws Exception {
		return TransportAPI.getTransportJSON(latitude, longitude, distance);
	}
	
	public static JSONArray getPosteJSON(double latitude, double longitude, double distance) throws Exception {
		return PosteAPI.getPosteJSON(latitude, longitude, distance);
	}


}
