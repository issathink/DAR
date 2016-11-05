package api;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tools.Tools;

public class EtablissementScolairePostBacAPI extends RequeteApiIleDeFrancePattern {

	private String [] refineStatut = new String [] {"Public"};

	private String [] refineTypeEtabl = new String [] { /*"Unité de formation et de recherche"*/ };

	private EtablissementScolairePostBacAPI() {
		super();
		super.dataSet = "etablissements-denseignement-superieur";
		super.facettes = new String [] { "statut", "type_d_etablissement"};
	}

	@Override
	protected String getExclude() {
		return "";
	}

	@Override
	protected String getRefine() {
		String res = "";
		for(String s : refineStatut) 
			res += "refine"+".statut="+s+"&";
		for(String s : refineTypeEtabl)
			res += "refine"+".type_d_etablissement="+s+"&";
		return res;
	}

	@Override
	protected JSONObject getMyJsonObjectFromRecord(JSONObject record) throws JSONException {
		JSONObject fields = record.getJSONObject("fields");
		JSONObject geometry = record.getJSONObject("geometry");

		String denominationPrincipale = fields.getString("type_d_etablissement");
		String patronyme = fields.has("universite") ? fields.getString("universite") : fields.getString("nom");
		String adresse = fields.getString("adresse");
		// Ou l'inverse
		String latitude = geometry.getJSONArray("coordinates").getString(0);
		String longitude = geometry.getJSONArray("coordinates").getString(1);

		JSONObject res = new JSONObject();
		res.put("type", "postBac");
		res.put("denomination", denominationPrincipale);
		res.put("patronyme", patronyme);
		res.put("latitude", latitude);
		res.put("longitude", longitude);
		res.put("adresse", adresse);

		return res;
	}

	public static JSONArray getEtablissementScolaireJSON(double latitude, double longitude, double distance) throws JSONException, Exception{
		EtablissementScolairePostBacAPI t = new EtablissementScolairePostBacAPI();
		t.geofilter_distance = latitude+","+longitude+","+distance;
		JSONArray res = new JSONArray();
		String URL = t.getUrl();
		System.out.println(URL);
		JSONArray records = Tools.sendGet(URL).getJSONArray("records");

		Map<String, Map<String, JSONObject>> map = new HashMap<String, Map<String, JSONObject>>();

		for(int i=0 ; i<records.length() ; i++) {
			JSONObject o = t.getMyJsonObjectFromRecord(records.getJSONObject(i));
			try {
				if(!map.containsKey(o.get("adresse")))
					map.put(o.getString("adresse"), new HashMap<String, JSONObject>());
			} catch(JSONException e) {
				System.out.println("[Error] ==> "+o.toString());
				e.printStackTrace();
			}
			map.get(o.getString("adresse")).put(o.getString("patronyme"), o);
		}
		for(Map<String, JSONObject> m : map.values()) {
			for(JSONObject o : m.values())
				res.put(o);
		}
		return res;
	}

}
