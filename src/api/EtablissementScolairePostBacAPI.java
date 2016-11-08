package api;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tools.Tools;

public class EtablissementScolairePostBacAPI extends RequeteApiIleDeFrancePattern {

	private String [] refineStatut = new String [] {"Public"};

	private String [] refineTypeEtabl = new String [] { /*"Unité de formation et de recherche"*/ };

	private EtablissementScolairePostBacAPI(double latitude, double longitude, double distance) {
		super(latitude, longitude, distance);
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
		//JSONObject geometry = record.getJSONObject("geometry");

		String denominationPrincipale = fields.getString("type_d_etablissement");
		String patronyme = fields.has("universite") ? fields.getString("universite") : fields.getString("nom");
		denominationPrincipale = StringEscapeUtils.escapeHtml4(denominationPrincipale);
		patronyme = StringEscapeUtils.escapeHtml4(patronyme);
		
		String adresse = fields.getString("adresse");
		// Ou l'inverse
		String latitude = fields.getString("longitude_x");
		String longitude = fields.getString("latitude_y");
		JSONObject res = new JSONObject();
		res.put("type", "post_bac");
		res.put("nom", patronyme);
		res.put("latitude", latitude);
		res.put("longitude", longitude);
		res.put("description",denominationPrincipale);
		
		
		// Just let this args here please!
		res.put("adresse", adresse); // Juste pour quand y en a plusieurs au meme endroits
		res.put("patronyme", patronyme); // Idem
		
		return res;
	}

	public static JSONArray getEtablissementScolaireJSON(double latitude, double longitude, double distance) throws JSONException, Exception{
		EtablissementScolairePostBacAPI t = new EtablissementScolairePostBacAPI(latitude, longitude, distance);
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
	
	@Override
	protected String[] getArrayOfFacettes() {
		return  new String [] { "statut", "type_d_etablissement"};
	}

	@Override
	protected String getDataSetName() {
		return "etablissements-denseignement-superieur";
	}

}
