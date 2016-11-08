package api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tools.Tools;

public class ComissariatPetiteCouronneAPI extends RequeteApiIleDeFrancePattern {

	private ComissariatPetiteCouronneAPI(double latitude, double longitude, double distance) {
		super(latitude, longitude, distance);
	}

	@Override
	protected JSONObject getMyJsonObjectFromRecord(JSONObject record) throws JSONException {
		JSONObject o = new JSONObject();
		JSONObject fields =  record.getJSONObject("fields");
		String latitude = fields.getJSONArray("geo_point_2d").getString(0);
		String longitude = fields.getJSONArray("geo_point_2d").getString(1);

		String description = fields.getString("description");
		String name = fields.getString("name");

		o.put("latitude", latitude);
		o.put("longitude", longitude);
		o.put("nom", description);
		o.put("description", name);
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
		return "cartographie-des-emplacements-des-commissariats-a-paris-et-petite-couronne";
	}

	public static JSONArray getComissariatPetiteCouronneJSON(double latitude, double longitude, double distance) throws JSONException, Exception{
		ComissariatPetiteCouronneAPI c = new ComissariatPetiteCouronneAPI(latitude, longitude, distance);
		JSONArray res = new JSONArray();
		String URL = c.getUrl();
		JSONArray records = Tools.sendGet(URL).getJSONArray("records");
		for(int i=0 ; i<records.length() ; i++) {
			if(!(records.getJSONObject(i).getJSONObject("fields").has("name") && records.getJSONObject(i).getJSONObject("fields").has("description")))
				continue;
			JSONObject tmp = c.getMyJsonObjectFromRecord(records.getJSONObject(i));
			res.put(tmp);
		}		
		return res;
	}
}
