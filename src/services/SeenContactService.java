package services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tools.DBStatic;
import tools.NameOfTables;

public class SeenContactService {

	private static Connection connexion = null;

	public static String getMessages(String userLogin) {
		ResultSet resRequest;
		JSONArray jsonResult = new JSONArray();

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
			Map<String, String> myMap = new HashMap<String, String>();
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
				myMap.put(friendLogin, ""+idFriend); // Reflechir la dessus .... pour garder un bonne ordre au niveau du dernier message
			}

			cpt = 0;
			JSONObject jObj;
			for(String loginFriend : myMap.keySet()) {

				jObj = new JSONObject();
				jObj.put("loginFriend", loginFriend);
				jObj.put("connected", isConnected(myMap.get(loginFriend)));
				jObj.put("nbMessageNotRead", nbMessageNotRead(userId, myMap.get(loginFriend)));
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

	private static String nbMessageNotRead(String idUser, String idFriend) {
//		String tableMessage = DBStatic.mysql_db + "." + NameOfTables.messages;
//		SELECT * FROM 
	}
	
	

	private static String isConnected(String idUser) throws SQLException {
		String tableSessions = DBStatic.mysql_db + "." + NameOfTables.sessions;
		String requestUserId = "SELECT session_id FROM "+tableSessions+" WHERE user_id="+idUser;
		Statement statement = connexion.createStatement();
		ResultSet resRequest = statement.executeQuery(requestUserId);
		String res = resRequest.next() ? "1" : "0"; // Si jamais y a une ligne (donc connecte)
		statement.close();
		return res;
	}



}
