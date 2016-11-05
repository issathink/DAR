package api;

import org.json.JSONArray;
import org.json.JSONObject;

import tools.Tools;


public class SportsGearAPI {
	
	public static JSONArray getSportsGear(double latitude, double longitude, double distance) throws Exception {
		JSONArray res = new JSONArray();
		String url = "https://data.iledefrance.fr/api/records/1.0/search/?dataset=ensemble-des-equipements-sportifs-dile-de-france";
		String geoPoint = "&geofilter.distance=" + latitude + "," + longitude + "," + distance+"&";
		String nbRow = "rows=2000";
		
		JSONObject j = Tools.sendGet(url+geoPoint+nbRow);
		for (int i = 0; i < j.getJSONArray("records").length(); i++) {
			JSONObject o = new JSONObject();
			String lon = j.getJSONArray("records").getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates").get(0).toString();
			String lat = j.getJSONArray("records").getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates").get(1).toString();
			String type = j.getJSONArray("records").getJSONObject(i).getJSONObject("fields").getString("eqt_type");
			String nomLieu = j.getJSONArray("records").getJSONObject(i).getJSONObject("fields").getString("ins_nom");
			o.put("latitude", lat);
			o.put("longitude", lon);
			o.put("type", "sport");
			o.put("type_de_sport", type);
			o.put("nom_lieu", nomLieu);
			
			res.put(o);
		}
		return res;
	}
}
