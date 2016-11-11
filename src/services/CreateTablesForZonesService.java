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

		/**
		 * Les points cardinaux delimites le carré représentant l'Ile de France
		 * delta_lat : taille de la longueur du carré selon l'axe des ordonnées
		 * delta_lng : taille de la largeur du carré selon l'axe des abscisses
		 * cumul_lat : progression de la latitude dans l'algo
		 * cumul_lng : progression de la longitude dans l'algo
		 */
		
		double south = 48.107892;
		double north = 49.235588;
		double west = 1.421153;
		double east = 3.637298;
		final double delta_lat = 0.01;
		final double delta_lng = 0.015;
		double cumul_lat = south;
		double cumul_lng = west;

		Connection conn = null;
		PreparedStatement statement = null;
		JSONObject result = new JSONObject();
		int cpt = 0;
		
		/**
		 * On va avoir une boucle pour la longueur des cases (en latitude) dans laquelle on bouclera
		 * sur la largeur des cases (en longitude). --> construction de plusieurs carrés pour partitionner
		 * la map en commencer en bas à gauche (Sud-Ouest) puis en allant vers la droite (Est) puis en allante vers le haut (Nord)
		 */

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
