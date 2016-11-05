package api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tools.Tools;


public class TransportAPI extends RequeteApiIleDeFrancePattern {

	private TransportAPI(double latitude, double longitude, double distance) {
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
		return "stations-de-transport-en-commun-existantes-grand-paris";
	}
	
	@Override
	protected JSONObject getMyJsonObjectFromRecord(JSONObject record) throws JSONException {
			JSONObject o = new JSONObject();
			String lon = record.getJSONObject("geometry").getJSONArray("coordinates").get(0).toString();
			String lat = record.getJSONObject("geometry").getJSONArray("coordinates").get(1).toString();
			String type_de_transport = record.getJSONObject("fields").getString("c_nature");
			String nomLieu = record.getJSONObject("fields").getString("l_station");
			o.put("latitude", lat);
			o.put("longitude", lon);
			o.put("type", "transport");
			o.put("type_de_transport", type_de_transport);
			o.put("nom_lieu", nomLieu);
			return o;
	}
	

	public static JSONArray getTransportJSON(double latitude, double longitude, double distance) throws JSONException, Exception{
		TransportAPI s = new TransportAPI(latitude, longitude, distance);
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
