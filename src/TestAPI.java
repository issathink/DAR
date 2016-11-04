import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import tools.Tools;


public class TestAPI {

	private String dataSet = "secteurs-scolaires";
	private String lang = "fr";
	private String start = "0";
	private String geofilter_distance = "48.8469029,2.3428312999999434,800";
	private String timeZone = "Europe/Paris";
	private String pretty_print = "true";

	private String debUrl = "https://data.iledefrance.fr/api/records/1.0/search/?";

	private String tmp = "https://data.iledefrance.fr/api/records/1.0/search/?dataset=secteurs-scolaires&lang=fr&start=0&geofilter.distance=48.8469029%2C2.3428312999999434%2C800&timezone=Europe%2FParis";



	public String getUrl() {
		return debUrl+getArgs();
	}
	public String getArgs() {
		String res = getDataSet()+getLang()+getStart()+getGeofilter()+getTimezone()+getPretty();
		if(res.charAt(res.length()-1) == '&')
			res = res.substring(0, res.length()-1);
		res = protectURL(res);
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
		return "dataset="+dataSet+"&";
	}

	public static void main(String[] args) throws Exception {
		TestAPI t = new TestAPI();
		JSONObject j = Tools.sendGet(t.getUrl());
		System.out.println(j.toString());

	}




}
