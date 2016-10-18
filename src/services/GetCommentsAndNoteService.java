package services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tools.DBStatic;
import tools.Tools;

public class GetCommentsAndNoteService {

	public static String getComments(String adresse) {
		Connection conn = null;
		ResultSet listOfComments = null;
		ResultSet listOfUsers = null;
		Statement statement = null;
		JSONObject result = new JSONObject();
		ArrayList<String> usersId = new ArrayList<>();
		int cpt = 1;
		double note_moyenne = 0;

		try {
			conn = DBStatic.getMySQLConnection();
			statement = (Statement) conn.createStatement();
			String query = "select comment, note, user_id from " + DBStatic.mysql_db 
					+  ".comments where adresse='" + adresse + "'";

			if(Tools.isValidAddress(adresse)) {
				listOfComments = statement.executeQuery(query);
				while(listOfComments.next()) {
					result.append("comment", listOfComments.getString("comment"));
					usersId.add(listOfComments.getString("user_id"));
					note_moyenne += Double.valueOf(listOfComments.getString("note"));
					cpt++;
				}
				note_moyenne = note_moyenne/cpt;
				note_moyenne = (double)((int)(note_moyenne*10))/10;
				result.append("moyenne", note_moyenne);
				cpt = 1;
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
					cpt++;
				}
			} else {
				result.put("erreur", "Invalid address '" + adresse + "'");
			}

		} catch (SQLException e1) {
			return Tools.erreurSQL;
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
