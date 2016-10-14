package services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tools.DBStatic;
import tools.NameOfTables;

public class SendMessageService {

	public static String sendMessage(String sender, String receiver, String message) {

		ResultSet resRequestToGetId;
		JSONObject jsonResult = new JSONObject();
		Connection connexion = null;
		Statement statement = null;

		try {
			/* Connexion BD et reglage ... */
			connexion = DBStatic.getMySQLConnection();
			statement = connexion.createStatement();
			
			// get id sender/receiver
			String tableUsers = DBStatic.mysql_db + "." + NameOfTables.users;
			String requestSenderId = "SELECT id FROM "+tableUsers+
					" WHERE login='"+sender+"'";
			String requestReceiverId = "SELECT id FROM "+tableUsers+
					" WHERE login='"+receiver+"'";

			(resRequestToGetId = statement.executeQuery(requestSenderId)).next();
			String senderId = resRequestToGetId.getString("id");
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