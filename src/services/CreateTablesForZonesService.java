package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import tools.DBStatic;
import tools.Tools;

/*
 * Service pour creer toutes les zones de l'application
 */
public class CreateTablesForZonesService {
	
	static int cpt = 0;

	public static String createTablesForZones(){

		/**
		 * Les points cardinaux delimitent le carré représentant l'Ile de France
		 * delta_lat : taille de la longueur du carré selon l'axe des ordonnées
		 * delta_lng : taille de la largeur du carré selon l'axe des abscisses
		 * cumul_lat : progression de la latitude dans l'algo
		 * cumul_lng : progression de la longitude dans l'algo
		 */
		
		double cumul_lat = Tools.south;
		double cumul_lng = Tools.west;

		Connection conn = null;
		PreparedStatement statement = null;
		JSONObject result = new JSONObject();
		
		/**
		 * On va avoir une boucle pour la longueur des cases (en latitude) dans laquelle on bouclera
		 * sur la largeur des cases (en longitude). --> construction de plusieurs carrés pour partitionner
		 * la map en commencer en bas à gauche (Sud-Ouest) puis en allant vers la droite (Est) puis en allante vers le haut (Nord)
		 */

		try {
			conn = DBStatic.getMySQLConnection();
			while (cumul_lat < Tools.north) {
				while (cumul_lng < Tools.east) {
					String query = "CREATE TABLE zone" + cpt++ + " (id INT PRIMARY KEY NOT NULL AUTO_INCREMENT, "
							+ "user_id INT NOT NULL, comment VARCHAR(512), note INT, lat DOUBLE KEY NOT NULL, lng DOUBLE KEY NOT NULL, "
							+ "adress TEXT KEY NOT NULL, date DATETIME KEY NOT NULL CURRENT_TIMESTAMP)";
					statement = conn.prepareStatement(query);
					statement.executeUpdate(query);
					cumul_lng += Tools.delta_lng;
					System.out.println("cpt : "+cpt);
				}
				cumul_lng = Tools.west;
				cumul_lat += Tools.delta_lat;
			}
			result.put("ok", "tablesCreated");
		}catch (SQLException e) {
			return Tools.erreurSQL + e.getMessage();
		} catch (JSONException e) {
			return Tools.erreurJSON;
		}
		return result.toString();

	}
	
}
