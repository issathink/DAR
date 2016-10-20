package services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tools.DBStatic;
import tools.NameOfTables;

public class GetLoginBeginByService {

	
	public static String getLoginList(String beginBy) {
		ResultSet resRequestToGetLogins;
		JSONArray jsonResult = new JSONArray();
		Connection connexion = null;
		Statement statement = null;

		try {

			/* Connexion BD et reglage ... */
			connexion = DBStatic.getMySQLConnection();
			statement = connexion.createStatement();
			
			String tableUsers = DBStatic.mysql_db + "." + NameOfTables.users;
			String requestSQL = "SELECT * from "+tableUsers+" WHERE (login LIKE '"+beginBy+"%')";
			
			resRequestToGetLogins = statement.executeQuery(requestSQL);

			while(resRequestToGetLogins.next()) {
				String login = resRequestToGetLogins.getString("login");
				login = StringEscapeUtils.unescapeCsv(login);
				JSONObject jObj = new JSONObject();
				jObj.put("login", login);
				jsonResult.put(jObj);
			}
			
			if(statement != null)
				statement.close();
			if(connexion != null)
				connexion.close();
		} catch (SQLException e) {
			return e.getMessage(); 
		} catch (JSONException e) {
			e.printStackTrace();
		} 

		return jsonResult.toString();
	}

	
}
