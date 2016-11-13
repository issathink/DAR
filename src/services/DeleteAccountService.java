package services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONException;
import org.json.JSONObject;

import tools.DBStatic;
import tools.Tools;

/*
 * Service pour supprimer un compte utilisateur 
 */
public class DeleteAccountService {

	public static String deleteAccount(String sessionId, String pw) {
		Connection conn = null;
		Statement statement = null;
		ResultSet Pass = null;
		JSONObject result = new JSONObject();
		String userId = "";
		String s = "";

		try {
			conn = DBStatic.getMySQLConnection();
			statement = (Statement) conn.createStatement();
			userId = Tools.getUserId(sessionId, conn);
			if(userId != null) {
				String query = "select pw from " + DBStatic.mysql_db +  ".users where id='" + userId + "'";
				Pass = statement.executeQuery(query);
				if(Pass.next()){
					if(pw.equals(Pass.getString("pw"))){
						String delete = "DELETE FROM " + DBStatic.mysql_db +  ".users where id='" + userId + "'";
						if(statement.executeUpdate(delete) > 0) 
							result.put("ok", "Delete successfully.");
					} else {
						result.put("erreur", "Invalid password.");
					}
				}  else {
					result.put("erreur", "Error retrieving password.");
				}
			} else {
				result.put("erreur", "Invalid session id.");
			}
		} catch (SQLException e) {
			if (e.getErrorCode() == 0 && e.toString().contains("CommunicationsException"))
				return deleteAccount(sessionId, pw);
			else
				return s + Tools.erreurSQL + e.getMessage();
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
