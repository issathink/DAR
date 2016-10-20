package services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tools.DBStatic;
import tools.NameOfTables;

public class SeenMessageService {


	// TODO Mettre les messages (ou on est le receiver) a vu 
	public static String getMessages(String userLogin, String friendLogin){

		ResultSet resRequeteToGetMessages, resRequestToGetName;
		JSONArray jsonResult = new JSONArray();
		Connection connexion = null;
		Statement statement = null, statement2 = null;

		try {

			/* Connexion BD et reglage ... */
			connexion = DBStatic.getMySQLConnection();
			statement = connexion.createStatement();
			statement2 = connexion.createStatement();

			/* Recuperation des Ids */
			// TODO , faire une union pour faire qu'une seul requete a la bd
			String tableUsers = DBStatic.mysql_db + "." + NameOfTables.users;
			String requestUserId = "SELECT id FROM "+tableUsers+
					" WHERE login='"+userLogin+"'";
			String requestFriendId = "SELECT id FROM "+tableUsers+
					" WHERE login='"+friendLogin+"'";


			(resRequestToGetName = statement.executeQuery(requestUserId)).next();
			String userId = resRequestToGetName.getString("id");
			(resRequestToGetName = statement.executeQuery(requestFriendId)).next();
			String friendId = resRequestToGetName.getString("id");

			///////////////////:
			String tableMessage = DBStatic.mysql_db + "." + NameOfTables.messages;
			String requestMessage = "SELECT id, id_sender, message, date_send, is_read FROM "+tableMessage+
					" WHERE (id_sender='"+friendId+"' AND id_receiver='"+userId+"') " +
					"OR (id_sender='"+userId+"' AND id_receiver='"+friendId+"') ORDER BY date_send ASC";


			/* Requete sql vers la base pour recuperer les messages */
			resRequeteToGetMessages = statement.executeQuery(requestMessage);

			while(resRequeteToGetMessages.next()) {
				String idMessage = resRequeteToGetMessages.getString("id");
				String id_sender = resRequeteToGetMessages.getString("id_sender");
				String message = resRequeteToGetMessages.getString("message");
				message = StringEscapeUtils.unescapeHtml4(message);
				
				String date = resRequeteToGetMessages.getString("date_send");
				String isRead = resRequeteToGetMessages.getString(("is_read"));
				String pseudoSender = id_sender.equals(userId) ? userLogin : friendLogin;
				////////////////////
				/* On indique le message a ete lu */
				if(isRead.equals("0") && !id_sender.equals(userId)) { // On a recu le message => le mettre a vu
					String requestUpdateIsRead = "UPDATE "+tableMessage+" SET is_read=1 WHERE id="+idMessage;
					statement2.executeUpdate(requestUpdateIsRead);
				}
				///////////
				JSONObject jObj = new JSONObject();
				jObj.put("login", pseudoSender);
				jObj.put("message", message);
				jObj.put("date_send", date);
				if(id_sender.equals(userId)) // On indique si le destinataire a lu le message ou non
					jObj.put("isRead", isRead);
				jsonResult.put(jObj);
			}

			if(statement != null)
				statement.close();
			if(statement2 != null)
				statement2.close();
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
