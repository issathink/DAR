package api;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tools.Tools;

public class EtablissementScolairePostBacAPI extends EtablissementScolairePreBacAPI {

	private String [] excludesStatus = new String [] 
			{ 
					"Privé hors contrat",
					"Consulaire",
					"Privé reconnu",
					"Privé sous contrat"
			};

	private EtablissementScolairePostBacAPI() {
		super();

		super.dataSet = "etablissements-denseignement-superieur";
		super.facettes = new String [] { "statut"};
	}


	protected String getExclude() {
		String res = "";

		for(String s : excludesStatus) {
			res += "statut="+s+"&";
		}

		return res;
	}


	protected JSONObject getMyJsonObjectFromRecord(JSONObject record) throws JSONException {
		JSONObject fields = record.getJSONObject("fields");
		JSONObject geometry = record.getJSONObject("geometry");

		String denominationPrincipale = fields.getString("type_d_etablissement");
		String patronyme = fields.getString("universite");
		// Ou l'inverse
		String latitude = geometry.getJSONArray("coordinates").getString(0);
		String longitude = geometry.getJSONArray("coordinates").getString(1);

		JSONObject res = new JSONObject();
		res.put("denomination", denominationPrincipale);
		res.put("patronyme", patronyme);
		res.put("latitude", latitude);
		res.put("longitude", longitude);

		return res;
	}

	public static JSONArray getEtablissementScolaireJSON(double latitude, double longitude, double distance) throws JSONException, Exception{
		EtablissementScolairePostBacAPI t = new EtablissementScolairePostBacAPI();
		t.geofilter_distance = latitude+","+longitude+","+distance;
		JSONArray res = new JSONArray();
		String URL = t.getUrl();
		JSONArray records = Tools.sendGet(URL).getJSONArray("records");

		Map<String, JSONObject> map = new HashMap<String, JSONObject>();
		
		
		for(int i=0 ; i<records.length() ; i++) {
			JSONObject o = t.getMyJsonObjectFromRecord(records.getJSONObject(i));
			map.put(o.getString("patronyme"), o);
		}
		for(JSONObject o : map.values())
			res.put(o);
		
		return res;
	}

}
