package tools;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.json.JSONObject;

public class CreateTablesForZones {

	public static String createTablesForZones(){
		
		LatLng NorthWest = new LatLng(49.235588, 1.421153);
		LatLng NorthEast = new LatLng(49.235588, 3.637298);
		LatLng SouthEast = new LatLng(48.107892, 3.637298);
		LatLng SouthWest = new LatLng(48.107892, 1.421153);
		
		Connection conn = null;
		Statement statement = null;
		ResultSet PrecPw = null;
		JSONObject result = new JSONObject();
		
		return null;
		
	}
	
}
