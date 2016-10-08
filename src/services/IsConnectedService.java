package services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONException;
import org.json.JSONObject;

import tools.DBStatic;
import tools.Tools;

public class IsConnectedService {
	
	public static String isConnected(String id) {
		Connection conn = null;
		Statement statement = null;
		JSONObject result = new JSONObject();
		ResultSet connections = null;
		
		try {
			conn = DBStatic.getMySQLConnection();
			statement = (Statement) conn.createStatement();
			
			String query = "select start from " + DBStatic.mysql_db +  ".sessions where session_id='" + id + "'";
			long start;
			
			connections = statement.executeQuery(query);
			if(connections.next()) {
				start = Long.parseLong(String.valueOf(connections.getString("start")));
				if(Tools.moreThan30Min(start))
					if(Tools.extendSession(id)) 
						result.put("ok", 0);
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
