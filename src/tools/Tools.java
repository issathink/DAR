package tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.GregorianCalendar;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * <b>Tools est la classe ou l'on delegue les fonctions frequement utilisees (de
 * type boite a outils).</b>
 * 
 * 
 */

public class Tools {

	public static String erreur = "{ \"erreur\" : \"Une erreur inattendue s'est produite. Veuillez verifier et reessayer.\" }";
	public static String erreurSQL = "{ \"erreur\" : \"Une erreur inattendue s'est produite(SQL). Veuillez verifier et reessayer.\" }";
	public static String erreurMongo = "{ \"erreur\" : \"Une erreur inattendue s'est produite(NoSQL). Veuillez verifier et reessayer.\" }";
	public static String erreurJSON = "{ \"erreur\" : \"Une erreur inattendue s'est produite(JSON). Veuillez verifier et reessayer.\" }";
	public static String erreurParam = "{ \"erreur\": \"Erreur parametres. Veuillez verifier et reessayer.\" }";
	public static String ok = "{ \"ok\" : \"ok\" }";
	public final static String MAPS_KEY = "AIzaSyDZv1TYlMIMxAPIV1ZuspwPD5zZEjylW28";
	public static double LatParisOuest = 48.852159;
	public static double LonParisOuest = 2.251419;
	public static double LatParisEst = 48.85206549830757;
	public static double LonParisEst = 2.425232;
	public static double LatParisSud = 48.80776790146285;
	public static double LonParisSud = 2.346954345703125;
	public static double LatParisNord = 48.90535146044804;
	public static double LonParisNord = 2.35107421875;
	public static double LatParisCenter = 48.733809;
	public static double LonParisCenter = 2.529981;
	public static double ParisRadius = 75.0;
	public static double Circonference = 0.5;
	public static double south = 48.107892;
	public static double north = 49.235588;
	public static double west = 1.421153;
	public static double east = 3.637298;
	public static double delta_lat = 0.03;
	public static double delta_lng = 0.045;

	double haversineKM(double lat1, double long1, double lat2, double long2) {
		double d2r = Math.PI / 180.0;
		double dLong = (long2 - long1) * d2r;
		double dLat = (lat2 - lat1) * d2r;
		double a = Math.pow(Math.sin(dLat / 2.0), 2)
				+ Math.cos(lat1 * d2r) * Math.cos(lat2 * d2r) * Math.pow(Math.sin(dLong / 2.0), 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = 6367 * c;

		return d;
	}

	public static double haversineInKM(double lat1, double long1, double lat2, double long2) {
		double _eQuatorialEarthRadius = 6378.1370D;
		double _d2r = (Math.PI / 180D);

		double dlong = (long2 - long1) * _d2r;
		double dlat = (lat2 - lat1) * _d2r;
		double a = Math.pow(Math.sin(dlat / 2D), 2D)
				+ Math.cos(lat1 * _d2r) * Math.cos(lat2 * _d2r) * Math.pow(Math.sin(dlong / 2D), 2D);
		double c = 2D * Math.atan2(Math.sqrt(a), Math.sqrt(1D - a));
		double d = _eQuatorialEarthRadius * c;

		return d;
	}

	public static String getSessionKey() {
		UUID uid = UUID.randomUUID();
		GregorianCalendar calendar = new java.util.GregorianCalendar();

		return uid.toString() + calendar.getTimeInMillis();
	}

	public static String getDate() {
		GregorianCalendar calendar = new java.util.GregorianCalendar();
		return calendar.getTime().toString();
	}

	public static long getNowMillis() {
		GregorianCalendar calendar = new java.util.GregorianCalendar();
		return calendar.getTimeInMillis();
	}

	public static boolean moreThan30Min(long start) {

		if (start < (getNowMillis() - 30 * 60 * 1000))
			return true;
		return false;
	}

	public static void insertError(JSONObject result, String key, String value) {
		try {
			result.put(key, value);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static boolean extendSession(String id) {
		Connection conn = null;
		Statement statement = null;

		try {
			conn = DBStatic.getMySQLConnection();
			statement = (Statement) conn.createStatement();
			String update = "UPDATE " + DBStatic.mysql_db + ".sessions SET start = '" + Tools.getNowMillis()
			+ "' where session_id = '" + id + "'";
			statement.executeUpdate(update);
		} catch (SQLException e1) {
			return false;
		}

		try {
			if (statement != null)
				statement.close();
			if (conn != null)
				conn.close();
		} catch (SQLException e) {}

		return true;
	}

	public static String getUserId(String sessionId, Connection conn) {
		ResultSet listOfUsers = null;
		String query = "select user_id, start from " + DBStatic.mysql_db + ".sessions where session_id=?";
		PreparedStatement st = null;

		try {
			st = conn.prepareStatement(query);
			st.setString(1, sessionId);
			listOfUsers = st.executeQuery();

			if (listOfUsers.next()) {
				if (!Tools.moreThan30Min(Long.parseLong(listOfUsers.getString("start")))) {
					String s = String.valueOf(listOfUsers.getString("user_id"));
					if(st != null)
						st.close();
					return s;
				}
			}
			if(st != null)
				st.close();
			return null;
		} catch (SQLException e) {
			return null;
		}
	}

	public static boolean isValidAddress(String adresse) {
		LatLng latLng;
		if ((latLng = getLatLng(adresse)) == null)
			return false;
		return isInParis(latLng.lat, latLng.lng);
	}

	public static LatLng getLatLng(String adresse) {
		try {
			String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + adresse + "&key=" + Tools.MAPS_KEY;
			JSONObject jOb = sendGet(replace(url));
			JSONObject obj = ((JSONObject) jOb.getJSONArray("results").get(0)).getJSONObject("geometry")
					.getJSONObject("location");
			double lat = obj.getDouble("lat");
			double lng = obj.getDouble("lng");

			return new LatLng(lat, lng);
		} catch (Exception e) {
			return null;
		}
	}

	public static JSONObject sendGet(String url) throws Exception {
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");
		// add request header
		// con.setRequestProperty("User-Agent", USER_AGENT);
		
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null)
			response.append(inputLine);
		
		in.close();
		con.disconnect();

		return new JSONObject(response.toString());
	}

	public static String replace(String str) {
		String[] words = str.split(" ");
		StringBuilder sentence = new StringBuilder(words[0]);

		for (int i = 1; i < words.length; ++i) {
			sentence.append("%20");
			sentence.append(words[i]);
		}

		return sentence.toString();
	}

	public static JSONArray concatArray(JSONArray... arrs) throws JSONException {
		JSONArray result = new JSONArray();
		for (JSONArray arr : arrs)
			for (int i = 0; i < arr.length(); i++)
				result.put(arr.get(i));
		
		return result;
	}

	public static boolean isInParis(double lat, double lon) {
		return haversineInKM(LatParisCenter, LonParisCenter, lat, lon) <= ParisRadius;
	}

	public static LatLng vector(LatLng p1, LatLng p2) {
		LatLng t = new LatLng(p2.lat - p1.lat, p2.lng - p1.lng);
		return t;
	}

	public static double dot(LatLng u, LatLng v) {
		return u.lat * v.lat + u.lng * v.lng;
	}

	public static String protectStrToDB(String s) {
		s = s.replaceAll("&", "\\&");
		//s = s.replaceAll("\\", "\\\\");
		return s;
	}

	public static String deProtectStrToDB(String s) {
		//s = s.replaceAll("\\\\+","\\\\");
		s = s.replaceAll("\\&", "&");
		return s;
	}

	public static boolean isNumber(String s) {
		for(char c : s.toCharArray())
			if(c!='0' && c!='1' && c!='2' && c!='3' && c!='4' && c!='5' && c!='6' && c!='7' && c!='8' && c!='9')
				return false;
		return true;
	}
}

/*
 * Connect to ssh ssh guest@li328.lip6.fr mdp : guest$
 */
