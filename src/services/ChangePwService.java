package services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONException;
import org.json.JSONObject;

import tools.DBStatic;
import tools.Tools;

public class ChangePwService {

	public static String changePw(String sessionId, String prec_pw, String new_pw) {
		Connection conn = null;
		Statement statement = null;
		ResultSet PrecPw = null;
		JSONObject result = new JSONObject();
		String userId = "";

		try {
			conn = DBStatic.getMySQLConnection();
			statement = (Statement) conn.createStatement();
			userId = Tools.getUserId(sessionId, statement);
			String query = "select pw from " + DBStatic.mysql_db +  ".users where id='" + userId + "'";
			if(userId != null) {
				PrecPw = statement.executeQuery(query);
				if(PrecPw.next()) {
					if(prec_pw.equals(PrecPw.getString("pw"))){
						String update = "UPDATE " + DBStatic.mysql_db +  ".users SET pw='"
								+ new_pw + "' where id='" + userId + "'";
						if(statement.executeUpdate(update) > 0) 
							result.put("ok", "Password changed successfully.");
						else
							result.put("erreur", "Unexpected error while changing pw.");
					}
					else
						result.put("erreur", "Invalid precedent pw.");
				}
				else
					result.put("erreur", "No pw.");
			} else {
				result.put("erreur", "Invalid session id.");
			}

		} catch (SQLException e) {
			return Tools.erreurSQL + e.getMessage();
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
