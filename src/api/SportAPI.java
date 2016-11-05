package api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tools.Tools;

public class SportAPI extends RequeteApiIleDeFrancePattern {

	private SportAPI(double latitude, double longitude, double distance) {
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
		return "ensemble-des-equipements-sportifs-dile-de-france";
	}
	
	@Override
	protected JSONObject getMyJsonObjectFromRecord(JSONObject record) throws JSONException {
			JSONObject o = new JSONObject();
			String lon = record.getJSONObject("geometry").getJSONArray("coordinates").get(0).toString();
			String lat = record.getJSONObject("geometry").getJSONArray("coordinates").get(1).toString();
			String type_de_sport = record.getJSONObject("fields").getString("eqt_type");
			String nomLieu = record.getJSONObject("fields").getString("ins_nom");
			o.put("latitude", lat);
			o.put("longitude", lon);
			o.put("type", "sport");
			o.put("description", type_de_sport);
			o.put("nom", nomLieu);
			return o;
	}
	

	public static JSONArray getSportJSON(double latitude, double longitude, double distance) throws JSONException, Exception{
		SportAPI s = new SportAPI(latitude, longitude, distance);
		JSONArray res = new JSONArray();
		String URL = s.getUrl();
		JSONArray records = Tools.sendGet(URL).getJSONArray("records");
		for(int i=0 ; i<records.length() ; i++) {
			JSONObject tmp = s.getMyJsonObjectFromRecord(records.getJSONObject(i));
			res.put(tmp);
		}		
		return res;
	}

}
