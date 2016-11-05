package api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tools.Tools;

public class PharmacieAPI extends RequeteApiIleDeFrancePattern {


	private PharmacieAPI(double latitude, double longitude, double distance) {
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
		return "carte-des-pharmacies-dile-de-france";
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
		
		

//		String denominationPrincipale = fields.getString("denomination_principale_uai");
//		String patronyme = fields.getString("patronyme_uai");
//		res.put("type", "preBac");
//		res.put("denomination", denominationPrincipale);
//		res.put("patronyme", patronyme);


		return res;
	}
	

	public static JSONArray getPharmacieJSON(double latitude, double longitude, double distance) throws JSONException, Exception{
		PharmacieAPI p = new PharmacieAPI(latitude, longitude, distance);
		JSONArray res = new JSONArray();
		String URL = p.getUrl();
		JSONArray records = Tools.sendGet(URL).getJSONArray("records");

		for(int i=0 ; i<records.length() ; i++) 
			res.put(p.getMyJsonObjectFromRecord(records.getJSONObject(i)));

		return res;
	}

}
