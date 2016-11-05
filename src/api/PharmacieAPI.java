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

		
//		if(fields.has("telephone"))
//			res.put("telephone", fields.getString("telephone"));

		String nom = fields.has("rslongue") ? fields.getString("rslongue") : fields.getString("rs");
		res.put("nom", nom.replaceAll("^PHARM ", "PHARMACIE "));
//		if(fields.has("numvoie"))
//			res.put(("numvoie"), fields.getString("numvoie"));
		res.put("description", "");
		res.put("type", "pharmacie");
		
		
		return res;
	}


	public static JSONArray getPharmacieJSON(double latitude, double longitude, double distance) throws JSONException, Exception{
		PharmacieAPI p = new PharmacieAPI(latitude, longitude, distance);
		JSONArray res = new JSONArray();
		String URL = p.getUrl();
		JSONArray records = Tools.sendGet(URL).getJSONArray("records");
		//Map<String, JSONObject> map = new HashMap<>();


		for(int i=0 ; i<records.length() ; i++) {
			JSONObject tmp = p.getMyJsonObjectFromRecord(records.getJSONObject(i));
			res.put(tmp);
		}
			
		// Le code en bas permet d'eviter les doublons d'adresse (je sais pas si y en a) mais il faut 
		// commenter la boucle for juste en haut et la map aussi
//		for(int i=0 ; i<records.length() ; i++) {
//			JSONObject tmp = p.getMyJsonObjectFromRecord(records.getJSONObject(i));
//			map.put((tmp.getString("latitude")+tmp.getString("longitude")), tmp);
//		}
//
//		for(JSONObject j : map.values())
//			res.put(j);
		
		return res;
	}

}
