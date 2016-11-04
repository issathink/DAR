import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

<<<<<<< HEAD
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
=======
>>>>>>> 0c333148fff5447bff74ed336adb5a4a66b524fd
import org.json.JSONObject;

import tools.Tools;


public class TestAPI {

<<<<<<< HEAD
	private String dataSet = "les_etablissements_d_enseignement_des_1er_et_2d_degres_en_idf";
=======
	private String dataSet = "secteurs-scolaires";
>>>>>>> 0c333148fff5447bff74ed336adb5a4a66b524fd
	private String lang = "fr";
	private String start = "0";
	private String geofilter_distance = "48.8469029,2.3428312999999434,800";
	private String timeZone = "Europe/Paris";
	private String pretty_print = "true";

	private String debUrl = "https://data.iledefrance.fr/api/records/1.0/search/?";

	private String tmp = "https://data.iledefrance.fr/api/records/1.0/search/?dataset=secteurs-scolaires&lang=fr&start=0&geofilter.distance=48.8469029%2C2.3428312999999434%2C800&timezone=Europe%2FParis";



<<<<<<< HEAD
	private String [] facettes = 
		{ 
			"denomination_principale_uai"
		};

	private String [] excludesDenoPrincipales = 
		{ 
			"École primaire privée",
			"Collège privé",
			"Lycée général privé",
			"École secondaire générale privée"
		};

=======
>>>>>>> 0c333148fff5447bff74ed336adb5a4a66b524fd
	public String getUrl() {
		return debUrl+getArgs();
	}
	public String getArgs() {
<<<<<<< HEAD
		String res = getDataSet()+getLang()+getStart()+getGeofilter()+
				getTimezone()+getFacette()+getExclude()+getPretty();
		if(res.charAt(res.length()-1) == '&')
			res = res.substring(0, res.length()-1);
		
		//res = StringEscapeUtils.escapeHtml4(res);
=======
		String res = getDataSet()+getLang()+getStart()+getGeofilter()+getTimezone()+getPretty();
		if(res.charAt(res.length()-1) == '&')
			res = res.substring(0, res.length()-1);
>>>>>>> 0c333148fff5447bff74ed336adb5a4a66b524fd
		res = protectURL(res);
		return res;
	}

<<<<<<< HEAD

	private String getExclude() {
		String res = "";

		for(String s : excludesDenoPrincipales) {
			res += "exclude.denomination_principale_uai="+s+"&";
		}

		return res;
	}

	private String getFacette() {
		String res = "";
		for(String s : facettes) 
			res += "facet="+s+"&";
		return res;
	}

=======
>>>>>>> 0c333148fff5447bff74ed336adb5a4a66b524fd
	private String getPretty() {
		return "pretty_print="+pretty_print+"&";
	}


	private String getStart() {
		return "start="+start+"&";
	}


	private String protectURL(String res) {
		res = res.replace(",", "%2C").replace("/", "%2F");
		return res;
	}


	private String getTimezone() {
		return "timezone="+timeZone+"&";
	}


	private String getGeofilter() {
		return "geofilter.distance="+geofilter_distance+"&";
	}


	private String getLang() {
		return "lang="+lang+"&";
	}

<<<<<<< HEAD
=======





>>>>>>> 0c333148fff5447bff74ed336adb5a4a66b524fd
	private String getDataSet() {
		return "dataset="+dataSet+"&";
	}

	public static void main(String[] args) throws Exception {
		TestAPI t = new TestAPI();
<<<<<<< HEAD
		System.out.println(t.getUrl());
		//JSONObject j = Tools.sendGet(t.getUrl());
		//System.out.println(j.toString());

	}
	
	public JSONArray getJSON() throws Exception {
		JSONArray res = new JSONArray();
		String URL = getUrl();
		JSONArray records = Tools.sendGet(URL).getJSONArray("records");

		for(int i=0 ; i<records.length() ; i++) 
			res.put(getMyJsonObjectFromRecord(records.getJSONObject(i)));
		
		return res;
		
	}
	private JSONObject getMyJsonObjectFromRecord(JSONObject record) throws JSONException {
		JSONObject fields = record.getJSONObject("fields");
		JSONObject geometry = record.getJSONObject("geometry");
		
		String denominationPrincipale = fields.getString("denomination_principale_uai");
		String patronyme = fields.getString("patronyme_uai");
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



	public static JSONArray getEtablissementsScolaire() {
		return null;
	}

=======
		JSONObject j = Tools.sendGet(t.getUrl());
		System.out.println(j.toString());

	}



>>>>>>> 0c333148fff5447bff74ed336adb5a4a66b524fd

}
