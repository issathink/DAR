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
import tools.Tools;

public class SeenMessageService {


	// TODO Mettre les messages (ou on est le receiver) a vu 
	public static String getMessages(String idSession, String friendLogin){

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
			String tableSession = DBStatic.mysql_db + "." + NameOfTables.sessions;

			String requestUserId = "SELECT user_id FROM "+tableSession+
					" WHERE session_id='"+idSession+"'";
			String requestFriendId = "SELECT id FROM "+tableUsers+
					" WHERE login='"+friendLogin+"'";


			if((resRequestToGetName = statement.executeQuery(requestUserId)).next() == false) {
				if(statement != null)
					statement.close();
				if(statement2 != null)
					statement2.close();
				if(connexion != null)
					connexion.close();
				JSONObject res = new JSONObject();
				res.put("erreur1", "La connexion n'existe pas");
				return res.toString();
			}

			String userId = resRequestToGetName.getString("user_id");



			if((resRequestToGetName = statement.executeQuery(requestFriendId)).next() == false) {
				if(statement != null)
					statement.close();
				if(statement2 != null)
					statement2.close();
				if(connexion != null)
					connexion.close();
				JSONObject res = new JSONObject();
				res.put("erreur2", "Le contact n'existe pas");
				return res.toString();
			}

			String friendId = resRequestToGetName.getString("id");


			String requestUserLogin = "SELECT login FROM "+tableUsers +
					" WHERE id='"+userId+"'";
			(resRequestToGetName = statement.executeQuery(requestUserLogin)).next();
			String userLogin= resRequestToGetName.getString("login");


			///////////////////:
			String tableMessage = DBStatic.mysql_db + "." + NameOfTables.messages;
			String requestMessage = "SELECT id, id_sender, id_receiver, message, date_send, is_read FROM "+tableMessage+
					" WHERE (id_sender='"+friendId+"' AND id_receiver='"+userId+"') " +
					"OR (id_sender='"+userId+"' AND id_receiver='"+friendId+"') ORDER BY date_send ASC";


			/* Requete sql vers la base pour recuperer les messages */
			resRequeteToGetMessages = statement.executeQuery(requestMessage);

			while(resRequeteToGetMessages.next()) {
				String idMessage = resRequeteToGetMessages.getString("id");
				String id_sender = resRequeteToGetMessages.getString("id_sender");
				String id_receiver = resRequeteToGetMessages.getString("id_receiver");
				String message = resRequeteToGetMessages.getString("message");

				message = Tools.deProtectStrToDB(message);

				String date = resRequeteToGetMessages.getString("date_send");
				String isRead = resRequeteToGetMessages.getString(("is_read"));
				String pseudoSender = id_sender.equals(userId) ? userLogin : friendLogin;
				////////////////////
				/* On indique le message a ete lu */
				if(isRead.equals("0") && (id_receiver.equals(id_sender) || !id_sender.equals(userId))) { // On a recu le message => le mettre a vu
					String requestUpdateIsRead = "UPDATE "+tableMessage+" SET is_read=1 WHERE id="+idMessage;
					statement2.executeUpdate(requestUpdateIsRead);
				}
				///////////
				JSONObject jObj = new JSONObject();
				jObj.put("login", pseudoSender);
				jObj.put("message", message);
				jObj.put("date_send", date);
				if(id_sender.equals(userId)) // On indique si le destinataire a read le message ou non
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
