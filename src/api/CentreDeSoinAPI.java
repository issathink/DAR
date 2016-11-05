package api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tools.Tools;

public class CentreDeSoinAPI extends RequeteApiIleDeFrancePattern {

	private CentreDeSoinAPI(double latitude, double longitude, double distance) {
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
		return "centres_de_soins_en_ile-de-france";
	}
	
	
	@Override
	protected JSONObject getMyJsonObjectFromRecord(JSONObject record) throws JSONException {
		JSONObject res = new JSONObject();
		JSONObject fields = record.getJSONObject("fields");
		JSONObject geometry = record.getJSONObject("geometry");
		String latitude = geometry.getJSONArray("coordinates").getString(0);
		String longitude = geometry.getJSONArray("coordinates").getString(1);
		res.put("latitude", latitude);
		res.put("longitude", longitude);

		//res.put("adresse", fields.getString("adresse"));
		//res.put("categorie", fields.getString("categorie_de_l_etablissement"));
		res.put("nom", fields.getString("raison_sociale").replaceAll("^CTRE ", "CENTRE "));
		res.put("description",fields.getString("categorie_de_l_etablissement"));
//		if(fields.has("num_tel"))
//			res.put("telephone", fields.getString("num_tel"));
		
		res.put("type", "centre_de_soin");
		return res;
	}
	
	public static JSONArray getCentreDeSoinJSON(double latitude, double longitude, double distance) throws JSONException, Exception{
		CentreDeSoinAPI p = new CentreDeSoinAPI(latitude, longitude, distance);
		JSONArray res = new JSONArray();
		String URL = p.getUrl();
		JSONArray records = Tools.sendGet(URL).getJSONArray("records");
		for(int i=0 ; i<records.length() ; i++) {
			JSONObject tmp = p.getMyJsonObjectFromRecord(records.getJSONObject(i));
			res.put(tmp);
		}		
		return res;
	}

}
