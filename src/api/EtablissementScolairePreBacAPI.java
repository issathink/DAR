package api;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tools.Tools;


class EtablissementScolairePreBacAPI extends RequeteApiIleDeFrancePattern {

	private String [] excludesDenoPrincipales = 
		{ 
				"École primaire privée",
				"Collège privé",
				"Lycée général privé",
				"École secondaire générale privée"
		};


	private EtablissementScolairePreBacAPI(double latitude, double longitude, double distance) {
		super(latitude, longitude, distance);
	}

	@Override
	protected String getExclude() {
		String res = "";
		for(String s : excludesDenoPrincipales) 
			res += "exclude.denomination_principale_uai="+s+"&";
		return res;
	}

	@Override
	protected String getRefine() {
		return "";
	}

	@Override
	protected JSONObject getMyJsonObjectFromRecord(JSONObject record) throws JSONException {
		JSONObject fields = record.getJSONObject("fields");
		JSONObject geometry = record.getJSONObject("geometry");

		String denominationPrincipale = fields.getString("denomination_principale_uai");
		String patronyme = fields.getString("patronyme_uai");
		// Ou l'inverse
		String latitude = geometry.getJSONArray("coordinates").getString(0);
		String longitude = geometry.getJSONArray("coordinates").getString(1);

		JSONObject res = new JSONObject();
		res.put("type", "preBac");
		res.put("denomination", denominationPrincipale);
		res.put("patronyme", patronyme);
		res.put("latitude", latitude);
		res.put("longitude", longitude);

		return res;
	}


	public static JSONArray getEtablissementScolaireJSON(double latitude, double longitude, double distance) throws JSONException, Exception{
		EtablissementScolairePreBacAPI t = new EtablissementScolairePreBacAPI(latitude, longitude, distance);
		JSONArray res = new JSONArray();
		String URL = t.getUrl();
		JSONArray records = Tools.sendGet(URL).getJSONArray("records");

		for(int i=0 ; i<records.length() ; i++) 
			res.put(t.getMyJsonObjectFromRecord(records.getJSONObject(i)));

		return res;
	}

	@Override
	protected String[] getArrayOfFacettes() {
		return new String [] { "denomination_principale_uai" };
	}

	@Override
	protected String getDataSetName() {
		return "les_etablissements_d_enseignement_des_1er_et_2d_degres_en_idf";
	}




}
