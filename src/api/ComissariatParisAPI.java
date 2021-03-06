package api;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tools.Tools;

public class ComissariatParisAPI extends RequeteApiIleDeFrancePattern {

	private ComissariatParisAPI(double latitude, double longitude, double distance) {
		super(latitude, longitude, distance);
	}

	@Override
	protected JSONObject getMyJsonObjectFromRecord(JSONObject record) throws JSONException {
		JSONObject o = new JSONObject();
		JSONObject fields =  record.getJSONObject("fields");
		String latitude = fields.getJSONArray("wgs84").getString(0);
		String longitude = fields.getJSONArray("wgs84").getString(1);

		String service = StringEscapeUtils.escapeHtml4(fields.getString("service"));
		o.put("latitude", latitude);
		o.put("longitude", longitude);
		o.put("nom", service);
		o.put("description", "");
		
//		if(fields.has("telephone")) {
//			String telephone = fields.getString("telephone");
//			o.put("telephone", telephone);
//		}

//		if(fields.has("horaires")) {
//			String horaires = fields.getString("horaires");
//			o.put("horaires", horaires);
//		}

//		if(fields.has("accessibilite")) {
//			String accHandi = fields.getString("accessibilite");
//			o.put("acces_handicape", accHandi);
//		}

		o.put("type", "comissariat");
		return o;
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
		return "carte-des-points-daccueil-police-a-paris";
	}

	public static JSONArray getComissariatParisJSON(double latitude, double longitude, double distance) throws JSONException, Exception{
		ComissariatParisAPI c = new ComissariatParisAPI(latitude, longitude, distance);
		JSONArray res = new JSONArray();
		String URL = c.getUrl();
		JSONArray records = Tools.sendGet(URL).getJSONArray("records");
		for(int i=0 ; i<records.length() ; i++) {
			JSONObject tmp = c.getMyJsonObjectFromRecord(records.getJSONObject(i));
			res.put(tmp);
		}		
		return res;
	}
}
