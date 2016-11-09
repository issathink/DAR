package api;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PosteAPI extends RequeteApiIleDeFrancePattern {

	protected PosteAPI(double latitude, double longitude, double distance) {
		super(latitude, longitude, distance);
	}

	@Override
	protected String getExclude() {
		return "";
	}

	@Override
	protected String getRefine() {
		return "";
	}

	@Override
	protected String[] getArrayOfFacettes() {
		return null;
	}

	@Override
	protected String getDataSetName() {
		return "liste-des-points-de-contact-du-reseau-postal-dile-de-france";
	}


	@Override
	protected JSONObject getMyJsonObjectFromRecord(JSONObject record) throws JSONException {
		JSONObject res = new JSONObject();
		JSONObject fields = record.getJSONObject("fields");
		String latitude = fields.getJSONArray("wgs84").getString(0);
		String longitude = fields.getJSONArray("wgs84").getString(1);
		res.put("latitude", latitude);
		res.put("longitude", longitude);
		res.put("nom", StringEscapeUtils.escapeHtml4(fields.getString("caracteristique_du_site")));
		res.put("description", StringEscapeUtils.escapeHtml4(fields.getString("libelle_du_site")));
		res.put("type", "poste");

		return res;
	}

	public static JSONArray getPosteJSON(double latitude, double longitude, double distance) throws JSONException, Exception{
		PosteAPI p = new PosteAPI(latitude, longitude, distance);
		return p.getResJSON(latitude, longitude, distance);
	}
}
