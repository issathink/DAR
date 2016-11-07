package services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import tools.DBStatic;
import tools.Tools;

public class GetCommentsAndNoteService {

	public static String getComments(String adresse) {
		Connection conn = null;
		ResultSet listOfAdress = null;
		ResultSet listOfComments = null;
		ResultSet listOfUsers = null;
		Statement statement = null;
		JSONObject result = new JSONObject();
		ArrayList<String> usersId = new ArrayList<>();
		int cpt = 0;
		double note_moyenne = 0;
		String my_adress = "";

		try {
			if(Tools.isValidAddress(adresse)) {
				conn = DBStatic.getMySQLConnection();
				statement = (Statement) conn.createStatement();
				String query = "select adresse from " + DBStatic.mysql_db 
						+  ".comments";
				Set<String> listAdress = new HashSet<>();

				listOfAdress = statement.executeQuery(query);
				//double latitu
				while(listOfAdress.next()){
					//if(Tools.haversineInKM(lat1, long1, lat2, long2))
					listAdress.add(listOfAdress.getString("adresse"));
				}

				for(String ad : listAdress){
					my_adress += "adresse = '" + ad + "' OR ";
				}
				
				my_adress = my_adress.substring(0, my_adress.length()-4);
				
				String query_get_comments = "select comment, note, user_id from " + DBStatic.mysql_db 
						+  ".comments where " + my_adress;
				listOfComments = statement.executeQuery(query_get_comments);
				
				while(listOfComments.next()) {
					result.append("comment", listOfComments.getString("comment"));
					usersId.add(listOfComments.getString("user_id"));
					note_moyenne += Double.valueOf(listOfComments.getString("note"));
					cpt++;
				}
				note_moyenne = note_moyenne/cpt;
				note_moyenne = (double)((int)(note_moyenne*10))/10;
				result.append("moyenne", note_moyenne);
				String s = " ";
				for(String uId: usersId){
					if(!uId.equals(usersId.get(usersId.size()-1)))
						s += "id=" + uId + " OR ";
					else
						s += "id=" + uId;
				}

				String query_get_login = "select id, login from " + DBStatic.mysql_db 
						+  ".users where " + s;

				listOfUsers = statement.executeQuery(query_get_login);
				while(listOfUsers.next()) {
					result.append("login", listOfUsers.getString("login"));
				}
			} else {
				result.put("erreur", "Invalid address '" + adresse + "'");
			}

		} catch (SQLException e1) {
			return Tools.erreurSQL + e1.getMessage();
		} catch (JSONException e) {
			return Tools.erreurJSON;
		}

		try {
			if (statement != null)
				statement.close();
			if (conn != null)
				conn.close();
		} catch (SQLException e) {}

		return result.toString();
	}

}
