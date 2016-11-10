package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tools.DBStatic;
import tools.NameOfTables;

public class SeenContactService {

	private static Connection connexion = null;

	public static String getMessages(String idSession) {
		ResultSet resRequest;
		JSONArray jsonResult = new JSONArray();

		PreparedStatement statement = null;
		PreparedStatement statement2 = null;

		try {
			connexion = DBStatic.getMySQLConnection();
			
			// Get UserId
			String tableSession = DBStatic.mysql_db + "." + NameOfTables.sessions;
			String tableUsers = DBStatic.mysql_db + "." + NameOfTables.users;

			// Get user id from session
			String requestUserId = "SELECT user_id FROM "+tableSession+
					" WHERE session_id=?";
			statement = connexion.prepareStatement(requestUserId);
			statement.setString(1, idSession);
			
			if((resRequest= statement.executeQuery()).next() == false) {
				if(statement != null)
					statement.close();
				if(connexion != null)
					connexion.close();
				JSONObject res = new JSONObject();
				res.put("erreur1 :", "La connexion n'existe pas");
				return res.toString();
			}

			String userId = resRequest.getString("user_id");

			// Get Contact
			// Parcourt du resultat
			//int cpt = 0; // Pour les afficher dans l'ordre peut etre
			Map<String, String> myMap = new HashMap<String, String>();
			ResultSet resRequest2;

			String tableMessage = DBStatic.mysql_db + "." + NameOfTables.messages;
			String requestMessage = "SELECT DISTINCT id_sender, id_receiver FROM " + tableMessage+
					" WHERE (id_sender=? OR id_receiver=?)";
			statement = connexion.prepareStatement(requestMessage);
			statement.setInt(1, Integer.parseInt(userId));
			statement.setInt(2, Integer.parseInt(userId));
			resRequest =  statement.executeQuery();

			while(resRequest.next()) {
				String id_sender = resRequest.getString("id_sender");
				String id_receiver = resRequest.getString("id_receiver");
				String idFriend = id_sender.equals(userId) ? id_receiver : id_sender;

				// Get FriendId
				String requestFriendLogin = "SELECT login FROM " + tableUsers + " WHERE id=?";
				statement2 = connexion.prepareStatement(requestFriendLogin);
				statement2.setInt(1, Integer.parseInt(idFriend));
				(resRequest2 = statement2.executeQuery()).next();
				String friendLogin = resRequest2.getString("login");

				// Resultat du service
				myMap.put(friendLogin, ""+idFriend); // Reflechir la dessus .... pour garder un bonne ordre au niveau du dernier message
			}

			//cpt = 0;
			JSONObject jObj;
			List<JSONObject> l = new ArrayList<JSONObject>(myMap.keySet().size());
			for(String loginFriend : myMap.keySet()) {
				jObj = new JSONObject();
				jObj.put("loginFriend", loginFriend);
				jObj.put("connected", isConnected(myMap.get(loginFriend)));
				jObj.put("nbMessageNotRead", nbMessageNotRead(userId, myMap.get(loginFriend)));
				jObj.put("dateLastMessage", dateLastMessage(userId, myMap.get(loginFriend)));
				l.add(jObj);
				//jsonResult.put(jObj);
			}


			Collections.sort(l, new Comparator<JSONObject>() {
				@Override
				public int compare(JSONObject o1, JSONObject o2) {
					try {
						String d1 = o1.getString("dateLastMessage");
						String d2 = o2.getString("dateLastMessage");

						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date date1 = sdf.parse(d1);
						Date date2 = sdf.parse(d2);

						return date2.compareTo(date1);
					} catch (JSONException e) {
						e.printStackTrace(); 
					} catch (ParseException e) {
						e.printStackTrace();
					} 				
					return 0;
				}
			});

			for(JSONObject j : l)
				jsonResult.put(j);

			if(statement != null)
				statement.close();
			if(statement2 != null)
				statement2.close();
			if(connexion != null)
				connexion.close();

		} catch (SQLException e) {
			if (e.getErrorCode() == 0 && e.toString().contains("CommunicationsException"))
				return getMessages(idSession);
			else
				return e.getMessage(); 
		} catch (JSONException e) {
			e.printStackTrace();
		} 

		return jsonResult.toString();
	}

	private static String nbMessageNotRead(String idUser, String idFriend) throws SQLException {
		String tableMessage = DBStatic.mysql_db + "." + NameOfTables.messages;
		String request = "SELECT COUNT(*) FROM " + tableMessage +
				" WHERE id_sender=? AND id_receiver=? AND is_read=0";
		PreparedStatement statement = connexion.prepareStatement(request);
		statement.setInt(1, Integer.parseInt(idFriend));
		statement.setInt(2, Integer.parseInt(idUser));
		ResultSet resRequest = statement.executeQuery();
		resRequest.next();
		String res = resRequest.getString(1);
		if(statement != null)
			statement.close();
		return res;
	}

	private static String isConnected(String idUser) throws SQLException {
		String tableSessions = DBStatic.mysql_db + "." + NameOfTables.sessions;
		String requestUserId = "SELECT session_id FROM " + tableSessions + " WHERE user_id=?";
		PreparedStatement statement = connexion.prepareStatement(requestUserId);
		statement.setInt(1, Integer.parseInt(idUser));
		ResultSet resRequest = statement.executeQuery();
		String res = resRequest.next() ? "1" : "0"; // Si jamais y a une ligne (donc connecte)
		if(statement != null)
			statement.close();
		return res;
	}

	private static String dateLastMessage(String userId, String friendId) throws SQLException {
		String tableMessage = DBStatic.mysql_db + "." + NameOfTables.messages;
		String request = "SELECT date_send FROM "+tableMessage+
				" WHERE (id_sender=? AND id_receiver=?) OR (id_sender=? AND id_receiver=?) ORDER BY date_send DESC LIMIT 1";
		// SELECT date_send FROM `messages` WHERE `id_sender`=9 OR `id_receiver`=9 ORDER BY date_send DESC LIMIT 1
		PreparedStatement statement = connexion.prepareStatement(request);
		statement.setInt(1, Integer.parseInt(userId));
		statement.setInt(2, Integer.parseInt(friendId));
		statement.setInt(3, Integer.parseInt(friendId));
		statement.setInt(4, Integer.parseInt(userId));
		ResultSet resRequest = statement.executeQuery();
		resRequest.next();
		String res = resRequest.getString("date_send");
		if(statement != null)
			statement.close();
		return res.substring(0, res.length()-2);
	}

}
