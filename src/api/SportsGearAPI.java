package api;

import org.json.JSONObject;

import tools.Tools;


public class SportsGearAPI {
	
	public String getSportsGear(String lati, String longi) throws Exception{ 
		String url = "https://data.iledefrance.fr/api/records/1.0/search/?dataset=ensemble-des-equipements-sportifs-dile-de-france";
		String geoPoint = "&geofilter.distance= &" + lati +"," + longi + ", 100";
		JSONObject j = Tools.sendGet(url+geoPoint);
		String s = "";
		for (int i = 0; i < j.getJSONArray("records").length(); i++) {
			String lon = j.getJSONArray("records").getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates").get(0).toString();
			String lat = j.getJSONArray("records").getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates").get(1).toString();
			String type = j.getJSONArray("records").getJSONObject(i).getJSONObject("fields").getString("eqt_type");
			s += lat + " ; " + lon + " ; " + type + "\n";
		}
		System.out.println(s);
		return s;
	}
}
