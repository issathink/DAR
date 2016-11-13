package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import tools.BCrypt;
import tools.DBStatic;
import tools.Tools;

/*
 * Service permettant de changer un mot de passe
 */
public class ChangePwService {

	public static String changePw(String sessionId, String prec_pw, String new_pw) {
		Connection conn = null;
		PreparedStatement statement = null, st = null;
		ResultSet precPw = null;
		JSONObject result = new JSONObject();
		String userId = "";

		try {
			conn = DBStatic.getMySQLConnection();
			userId = Tools.getUserId(sessionId, conn);
			String query = "select pw from " + DBStatic.mysql_db +  ".users where id=?";
			statement = conn.prepareStatement(query);
			
			if(userId != null) {
				statement.setInt(1, Integer.parseInt(userId));
				precPw = statement.executeQuery();
				if(precPw.next()) {
					if(BCrypt.checkpw(prec_pw, precPw.getString("pw"))) {
						String update = "UPDATE " + DBStatic.mysql_db +  ".users SET pw=? where id=?";
						st = conn.prepareStatement(update);
						st.setString(1, BCrypt.hashpw(new_pw, BCrypt.gensalt()));
						st.setInt(2, Integer.parseInt(userId));
						
						if(st.executeUpdate() > 0) 
							result.put("ok", "Password changed successfully.");
						else
							result.put("erreur", "Unexpected error while changing password.");
					} else {
						result.put("erreur", "Invalid precedent password.");
					}
				} else {
					result.put("erreur", "No password.");
				}
			} else {
				result.put("erreur", "Invalid session id.");
			}
		} catch (SQLException e) {
			if (e.getErrorCode() == 0 && e.toString().contains("CommunicationsException"))
				return changePw(sessionId, prec_pw, new_pw);
			else
				return Tools.erreurSQL + e.getMessage();
		} catch (JSONException e) {
			return Tools.erreurJSON;
		}

		try {
			if (statement != null)
				statement.close();
			if(st != null)
				st.close();
			if (conn != null)
				conn.close();
		} catch (SQLException e) {}

		return result.toString();
	}

}
