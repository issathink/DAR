package services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import tools.DBStatic;
import tools.NameOfTables;
import tools.Tools;

public class SendMessageService {

	public static String sendMessage(String idSession, String receiver, String message) {

		ResultSet resRequestToGetId;
		JSONObject jsonResult = new JSONObject();
		Connection connexion = null;
		Statement statement = null;

		message = Tools.protectStrToDB(message);

		try {
			/* Connexion BD et reglage ... */
			connexion = DBStatic.getMySQLConnection();
			statement = connexion.createStatement();

			// get id sender/receiver
			String tableUsers = DBStatic.mysql_db + "." + NameOfTables.users;
			String tableSession = DBStatic.mysql_db + "." + NameOfTables.sessions;
			String requestSenderId = "SELECT user_id FROM "+tableSession+
					" WHERE session_id='"+idSession+"'";
			String requestReceiverId = "SELECT id FROM "+tableUsers+
					" WHERE login='"+receiver+"'";

			if((resRequestToGetId = statement.executeQuery(requestSenderId)).next() == false) {
				if(statement != null)
					statement.close();
				if(connexion != null)
					connexion.close();
				JSONObject res = new JSONObject();
				res.put("Error :", "La connexion n'existe pas");
				return res.toString();
			}
			
			/*if((resRequestToGetName = statement.executeQuery(requestFriendId)).next() == false) {
				if(statement != null)
					statement.close();
				if(statement2 != null)
					statement2.close();
				if(connexion != null)
					connexion.close();
				JSONObject res = new JSONObject();
				res.put("erreur2", "Le contact n'existe pas");
				return res.toString();
			}*/

			String senderId = resRequestToGetId.getString("user_id");
			(resRequestToGetId = statement.executeQuery(requestReceiverId)).next();
			String receiverId = resRequestToGetId.getString("id");

			// Inscription du message dans la BDD
			String tableMessage = DBStatic.mysql_db + "." + NameOfTables.messages;
			String requestSendMessage = "INSERT INTO "+tableMessage+" (id_sender, id_receiver, message) VALUES ("+
					senderId+","+receiverId+",'"+message+"')";

			int ret  = statement.executeUpdate(requestSendMessage);
			if(ret == 1)
				jsonResult.put("ValAddOnBD", "Message Add to BDD");
			else
				jsonResult.put("ValAddOnBD", "An error occured during insert message");

			if(statement != null)
				statement.close();
			if(connexion != null)
				connexion.close();
		} catch (SQLException | JSONException e) {
			return e.getMessage(); 
		} 

		return jsonResult.toString();
	}

}
