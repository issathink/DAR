package api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tools.Tools;

public abstract class RequeteApiIleDeFrancePattern {

	private String nbRow = "2000";
	private String lang = "fr";
	private String start = "0";
	private String geofilter_distance;
	private String timeZone = "Europe/Paris";
	private String pretty_print = "false";
	private String debUrl = "https://data.iledefrance.fr/api/records/1.0/search/?";

	protected RequeteApiIleDeFrancePattern(double latitude, double longitude, double distance) {
		geofilter_distance = latitude+","+longitude+","+distance;
	}

	protected String getUrl() {
		return debUrl+getArgs();
	}

	private String getArgs() {
		String res = getDataSet()+getLang()+getStart()+getGeofilter()+
				getTimezone()+getFacette()+getExclude()+getRefine()+getNbRow()+getPretty();
		if(res.charAt(res.length()-1) == '&')
			res = res.substring(0, res.length()-1);
		res = protectURL(res);
		return res;
	}

	private String getFacette() {
		String res = "";
		String [] tmp = getArrayOfFacettes();
		if(tmp != null)
			for(String s : tmp) 
				res += "facet="+s+"&";
		return res;
	}

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

	private String getDataSet() {
		return "dataset="+getDataSetName()+"&";
	}

	private String getNbRow() {
		return "rows="+nbRow+"&";
	}
	
	protected JSONArray getResJSON(double latitude, double longitude, double distance) throws JSONException, Exception{
		JSONArray res = new JSONArray();
		String URL = this.getUrl();
		JSONArray records = Tools.sendGet(URL).getJSONArray("records");
		for(int i=0 ; i<records.length() ; i++) {
			JSONObject tmp = this.getMyJsonObjectFromRecord(records.getJSONObject(i));
			res.put(tmp);
		}
		return res;
	}


	// Pas sur de la laisser dans l'abstract celle la
	protected abstract JSONObject getMyJsonObjectFromRecord(JSONObject record) throws JSONException;

	protected abstract String getExclude();

	protected abstract String getRefine();

	protected abstract String [] getArrayOfFacettes();

	protected abstract String getDataSetName();

	//	public static JSONArray getEtablissementScolaireJSON(double latitude, double longitude, double distance) throws JSONException, Exception{
	//		EtablissementScolairePreBacAPI t = new EtablissementScolairePreBacAPI();
	//		t.geofilter_distance = latitude+","+longitude+","+distance;
	//		JSONArray res = new JSONArray();
	//		String URL = t.getUrl();
	//		JSONArray records = Tools.sendGet(URL).getJSONArray("records");
	//
	//		for(int i=0 ; i<records.length() ; i++) 
	//			res.put(t.getMyJsonObjectFromRecord(records.getJSONObject(i)));
	//
	//		return res;
	//	}

}
