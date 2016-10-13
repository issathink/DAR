package servlets;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tools.DBStatic;
import tools.NameOfTables;

public class SeenContactService {



	public static String getMessages(String userLogin) {
		ResultSet resRequest;
		JSONArray jsonResult = new JSONArray();
		Connection connexion = null;
		Statement statement = null;
		Statement statement2 = null;
		
		try {
			connexion = DBStatic.getMySQLConnection();
			statement = connexion.createStatement();
			statement2 = connexion.createStatement();

			// Get UserId
			String tableUsers = DBStatic.mysql_db + "." + NameOfTables.users;
			String requestUserId = "SELECT id FROM "+tableUsers+
					" WHERE login='"+userLogin+"'";
			(resRequest= statement.executeQuery(requestUserId)).next();
			String userId = resRequest.getString("id");

			// Get Contact
			String tableMessage = DBStatic.mysql_db + "." + NameOfTables.messages;
			String requestMessage = "SELECT DISTINCT id_sender, id_receiver FROM "+tableMessage+
					" WHERE (id_sender='"+userId+"' OR id_receiver='"+userId+"')";
			resRequest = statement.executeQuery(requestMessage);

			System.out.println("Ok");
			// Parcourt du resultat
			int cpt = 0; // Pour les afficher dans l'ordre peut etre
			Map<String, Integer> myMap = new HashMap<String, Integer>();
			ResultSet resRequest2;
			
			while(resRequest.next()) {
				String id_sender = resRequest.getString("id_sender");
				String id_receiver = resRequest.getString("id_receiver");
				String idFriend = id_sender.equals(userId) ? id_receiver : id_sender;

				// Get FriendId
				String requestFriendLogin = "SELECT login FROM "+tableUsers+
						" WHERE id='"+idFriend+"'";
				(resRequest2 = statement2.executeQuery(requestFriendLogin)).next();
				String friendLogin = resRequest2.getString("login");

				// Resultat du service
				myMap.put(friendLogin, cpt++); // Reflechir la dessus .... pour garder un bonne ordre au niveau du dernier message
			}

			cpt = 0;
			JSONObject jObj;
			for(String loginFriend : myMap.keySet()) {
				jObj = new JSONObject();
				jObj.put("loginFriend", loginFriend);
				jsonResult.put(jObj);
			}

			if(statement != null)
				statement.close();
			if(connexion != null)
				connexion.close();
			
		} catch (SQLException e) {
			return e.getMessage(); 
		} catch (JSONException e) {
			e.printStackTrace();
		} 

		return jsonResult.toString();
	}

}
