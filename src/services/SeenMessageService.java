package services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONException;
import org.json.JSONObject;

import tools.DBStatic;
import tools.NameOfTables;

public class SeenMessageService {



	public static String getMessages(String userId, String friendId){

		ResultSet resRequeteToGetMessages, resRequestToGetName;
		JSONObject jsonResult = new JSONObject();
		Connection connexion = null;
		Statement statement = null;

		try {
			String tableMessage = DBStatic.mysql_db + "." + NameOfTables.messages;
			String tableUsers = DBStatic.mysql_db + "." + NameOfTables.users;

			String requestMessage = "SELECT id_sender, message, date_send FROM "+tableMessage+
					" WHERE (id_sender='"+friendId+"' AND id_receiver='"+userId+"') " +
					"OR (id_sender='"+userId+"' AND id_receiver='"+friendId+"') ORDER BY date_send ASC";

			String requestUserName = "SELECT login FROM "+tableUsers+
					" WHERE id='"+userId+"'";
			String requestFriendName = "SELECT login FROM "+tableUsers+
					" WHERE id='"+friendId+"'";


			/* Connexion BD et reglage ... */
			connexion = DBStatic.getMySQLConnection();
			statement = connexion.createStatement();

			/* Recuperation des pseudos */
			// TODO , faire une union pour faire qu'une seul requete a la bd
			(resRequestToGetName = statement.executeQuery(requestUserName)).next();
			String userName = resRequestToGetName.getString("login");
			(resRequestToGetName = statement.executeQuery(requestFriendName)).next();
			String friendName = resRequestToGetName.getString("login");
			
			/* Requete sql vers la base pour recuperer les messages */
			resRequeteToGetMessages = statement.executeQuery(requestMessage);
			int cpt = 1;
			while(resRequeteToGetMessages.next()) {
				String id_sender = resRequeteToGetMessages.getString("id_sender");
				String message = resRequeteToGetMessages.getString("message");
				String date = resRequeteToGetMessages.getString("date_send");
				String pseudoSender = id_sender.equals(userId) ? userName : friendName;
				jsonResult.put("Message_"+(cpt++), pseudoSender+" ["+date+"]"+":"+message);
			}
			if(statement != null)
				statement.close();
			if(connexion != null)
				connexion.close();
		} catch (SQLException e) {
			return e.getMessage(); 
		} catch (JSONException e) {
			return e.getMessage();
		} 
		
		return jsonResult.toString();
	}
}
