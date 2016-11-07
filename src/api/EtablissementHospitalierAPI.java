package api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tools.Tools;

public class EtablissementHospitalierAPI extends RequeteApiIleDeFrancePattern {

	private EtablissementHospitalierAPI(double latitude, double longitude, double distance) {
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
		return "les_etablissements_hospitaliers_franciliens";
	}


	@Override
	protected JSONObject getMyJsonObjectFromRecord(JSONObject record) throws JSONException {
		JSONObject res = new JSONObject();
		JSONObject fields = record.getJSONObject("fields");
		JSONObject geometry = record.getJSONObject("geometry");
		String longitude = geometry.getJSONArray("coordinates").getString(0);
		String latitude = geometry.getJSONArray("coordinates").getString(1);
		res.put("latitude", latitude);
		res.put("longitude", longitude);

		//res.put("categorie", fields.getString("categorie_de_l_etablissement"));

//		if(fields.has("num_voie"))
//			res.put("num_voie", fields.getString("num_voie"));

		String nom = fields.has("raison_sociale_entite_juridique") ? fields.getString("raison_sociale_entite_juridique") : fields.getString("raison_sociale");
		res.put("nom", nom);
		res.put("description", fields.getString("categorie_de_l_etablissement"));

//		if(fields.has("num_tel"))
//			res.put("telephone", fields.getString("num_tel"));


		res.put("type", "etablissement_hospitalier");
		return res;
	}


	public static JSONArray getEtablissementHospitalierJSON(double latitude, double longitude, double distance) throws JSONException, Exception{
		EtablissementHospitalierAPI e = new EtablissementHospitalierAPI(latitude, longitude, distance);
		JSONArray res = new JSONArray();
		String URL = e.getUrl();
		JSONArray records = Tools.sendGet(URL).getJSONArray("records");
		for(int i=0 ; i<records.length() ; i++) {
			JSONObject tmp = e.getMyJsonObjectFromRecord(records.getJSONObject(i));
			res.put(tmp);
		}		
		return res;
	}

}
