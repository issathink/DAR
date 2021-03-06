package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tools.DBStatic;
import tools.NameOfTables;
import tools.Tools;

/*
 * Service pour mettre à jour le flag d'un message
 */
public class SeenMessageService {

	public static String getMessages(String idSession, String friendLogin){
		ResultSet resRequeteToGetMessages, resRequestToGetName;
		JSONArray jsonResult = new JSONArray();
		Connection connexion = null;
		PreparedStatement statement = null, statement2 = null;

		try {
			/* Connexion BD et reglage ... */
			connexion = DBStatic.getMySQLConnection();

			/* Recuperation des Ids */
			String tableUsers = DBStatic.mysql_db + "." + NameOfTables.users;
			String tableSession = DBStatic.mysql_db + "." + NameOfTables.sessions;

			String requestUserId = "SELECT user_id FROM "+tableSession+
					" WHERE session_id=?";
			String requestFriendId = "SELECT id FROM "+tableUsers+
					" WHERE login=?";
			statement = connexion.prepareStatement(requestUserId);
			statement.setString(1, idSession);
			statement2 = connexion.prepareStatement(requestFriendId);
			statement2.setString(1, friendLogin);

			if((resRequestToGetName = statement.executeQuery()).next() == false) {
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

			if((resRequestToGetName = statement2.executeQuery()).next() == false) {
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
					" WHERE id=?";
			statement = connexion.prepareStatement(requestUserLogin);
			statement.setInt(1, Integer.parseInt(userId));
			(resRequestToGetName = statement.executeQuery()).next();
			String userLogin = resRequestToGetName.getString("login");


			String tableMessage = DBStatic.mysql_db + "." + NameOfTables.messages;
			String requestMessage = "SELECT id, id_sender, id_receiver, message, date_send, is_read FROM "+tableMessage+
					" WHERE (id_sender="+"?"+" AND id_receiver="+"?"+") " +
					"OR (id_sender="+"?"+" AND id_receiver="+"?"+") ORDER BY date_send ASC";
			statement = connexion.prepareStatement(requestMessage);
			statement.setInt(1, Integer.parseInt(friendId));
			statement.setInt(2, Integer.parseInt(userId));
			statement.setInt(3, Integer.parseInt(userId));
			statement.setInt(4, Integer.parseInt(friendId));

			/* Requete sql vers la base pour recuperer les messages */
			resRequeteToGetMessages = statement.executeQuery();

			while(resRequeteToGetMessages.next()) {
				String idMessage = resRequeteToGetMessages.getString("id");
				String id_sender = resRequeteToGetMessages.getString("id_sender");
				String id_receiver = resRequeteToGetMessages.getString("id_receiver");
				String message = resRequeteToGetMessages.getString("message");
				message = Tools.deProtectStrToDB(message);

				String date = resRequeteToGetMessages.getString("date_send");
				String isRead = resRequeteToGetMessages.getString(("is_read"));
				String pseudoSender = id_sender.equals(userId) ? userLogin : friendLogin;

				/* On indique le message a ete lu */
				if(isRead.equals("0") && (id_receiver.equals(id_sender) || !id_sender.equals(userId))) { // On a recu le message => le mettre a vu
					String requestUpdateIsRead = "UPDATE "+tableMessage+" SET is_read=1 WHERE id=?";
					statement2 = connexion.prepareStatement(requestUpdateIsRead);
					statement2.setInt(1, Integer.parseInt(idMessage));
					statement2.executeUpdate();
				}

				JSONObject jObj = new JSONObject();
				jObj.put("userLogin", StringEscapeUtils.escapeHtml4(userLogin));
				jObj.put("login", StringEscapeUtils.escapeHtml4(pseudoSender));
				jObj.put("message", StringEscapeUtils.escapeHtml4(message));
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
			if (e.getErrorCode() == 0 && e.toString().contains("CommunicationsException"))
				return getMessages(idSession, friendLogin);
			else
				return e.getMessage(); 
		} catch (JSONException e) {
			return e.getMessage();
		} 

		return jsonResult.toString();
	}
}
