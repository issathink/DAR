package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import tools.DBStatic;
import tools.Tools;

public class CreateTablesForZonesService {

	public static String createTablesForZones(){

		double south = 48.107892;
		double north = 49.235588;
		double west = 1.421153;
		double east = 3.637298;
		final double delta_lat = 0.05;
		final double delta_lng = 0.5;
		double cumul_lat = south;
		double cumul_lng = west;

		Connection conn = null;
		PreparedStatement statement = null;
		JSONObject result = new JSONObject();
		int cpt = 0;

		try {
			conn = DBStatic.getMySQLConnection();
			while (cumul_lat < north) {
				while (cumul_lng < east) {
					String query = "CREATE TABLE zone" + cpt + " (id INT PRIMARY KEY NOT NULL AUTO_INCREMENT, "
							+ "user_id INT NOT NULL, comment VARCHAR(512), note INT, lat DOUBLE, lng DOUBLE, "
							+ "adress TEXT, date DATETIME)";
					statement = conn.prepareStatement(query);
					statement.executeUpdate(query);
					result.put("ok", "table"+cpt);
					cpt++;
					cumul_lng += delta_lng;
				}
				cumul_lng = west;
				cumul_lat += delta_lat;
			}
		}catch (SQLException e) {
			return Tools.erreurSQL + e.getMessage();
		} catch (JSONException e) {
			return Tools.erreurJSON;
		}
		return result.toString();

	}

}
