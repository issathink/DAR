package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import tools.DBStatic;
import tools.NameOfTables;
import tools.Tools;

/*
 * Service pour envoyer de messages
 */
public class SendMessageService {

	public static String sendMessage(String idSession, String receiver, String message) {

		ResultSet resRequestToGetId;
		JSONObject jsonResult = new JSONObject();
		Connection connexion = null;
		PreparedStatement statement = null;

		message = Tools.protectStrToDB(message);

		try {
			/* Connexion BD et reglage ... */
			connexion = DBStatic.getMySQLConnection();

			// get id sender/receiver
			String tableUsers = DBStatic.mysql_db + "." + NameOfTables.users;
			String tableSession = DBStatic.mysql_db + "." + NameOfTables.sessions;
			String requestSenderId = "SELECT user_id FROM "+tableSession+
					" WHERE session_id=?";
			String requestReceiverId = "SELECT id FROM "+tableUsers+
					" WHERE login=?";
			statement = connexion.prepareStatement(requestSenderId);
			statement.setString(1, idSession);

			if((resRequestToGetId = statement.executeQuery()).next() == false) {
				if(statement != null)
					statement.close();
				if(connexion != null)
					connexion.close();
				JSONObject res = new JSONObject();
				res.put("erreur1 :", "La connexion n'existe pas");
				return res.toString();
			}

			String senderId = resRequestToGetId.getString("user_id");
			statement = connexion.prepareStatement(requestReceiverId);
			statement.setString(1, receiver);
			if((resRequestToGetId = statement.executeQuery()).next() == false) {
				if(statement != null)
					statement.close();
				if(connexion != null)
					connexion.close();
				JSONObject res = new JSONObject();
				res.put("erreur2", "Le contact n'existe pas");
				return res.toString();
			}
			String receiverId = resRequestToGetId.getString("id");

			// Inscription du message dans la BDD
			String tableMessage = DBStatic.mysql_db + "." + NameOfTables.messages;
			String requestSendMessage = "INSERT INTO "+tableMessage+" (id_sender, id_receiver, message) VALUES (?,?,?)";
			statement = connexion.prepareStatement(requestSendMessage);
			statement.setInt(1, Integer.parseInt(senderId));
			statement.setInt(2, Integer.parseInt(receiverId));
			message = message.replaceAll("&", "\\&");
			statement.setString(3, message);
			int ret  = statement.executeUpdate();
			
			if(ret == 1)
				jsonResult.put("ValAddOnBD", "Message Add to BDD");
			else
				jsonResult.put("ValAddOnBD", "An error occured during insert message");

			if(statement != null)
				statement.close();
			if(connexion != null)
				connexion.close();
		} catch (SQLException e) {
			if (e.getErrorCode() == 0 && e.toString().contains("CommunicationsException"))
				return sendMessage(idSession, receiver, message);
			else
				return e.getMessage(); 
		} catch(JSONException e){
			e.printStackTrace();
		}

		return jsonResult.toString();
	}

}
