package tools;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONObject;

public class CreateTablesForZones {

	public static String createTablesForZones(){

		double north = 48.107892;
		double south = 49.235588;
		double west = 1.421153;
		double east = 3.637298;
		final double delta_lat = 0.05;
		final double delta_lng = 0.5;
		double cumul_lat = south;
		double cumul_lng = west;

		Connection conn = null;
		PreparedStatement statement = null, st = null;
		ResultSet PrecPw = null;
		JSONObject result = new JSONObject();
		int cpt = 0;

		try {
			conn = DBStatic.getMySQLConnection();
			while (cumul_lat < north) {
				while (cumul_lng < east) {
					String query = "CREATE TABLE zone" + cpt + " (id INT PRIMARY KEY NOT NULL, "
							+ "adresse VARCHAR(100)";
					cpt++;
					statement = conn.prepareStatement(query);
					cumul_lng += delta_lng;
				}
				cumul_lng = west;
				cumul_lat += delta_lat;
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return null;

	}

}
