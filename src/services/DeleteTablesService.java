package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import tools.DBStatic;
import tools.Tools;

public class DeleteTablesService {

	public static String deleteTables(){
		Connection conn = null;
		PreparedStatement statement = null;
		JSONObject result = new JSONObject();
		int cpt = 0;

		try {
			while(cpt < 114){
				conn = DBStatic.getMySQLConnection();
				String query = "DROP TABLE zone" + cpt;
				statement = conn.prepareStatement(query);
				statement.executeUpdate(query);
				result.put("ok", "drop"+cpt);
				cpt++;
			}
		}catch (SQLException e) {
			return Tools.erreurSQL + e.getMessage();
		} catch (JSONException e) {
			return Tools.erreurJSON;
		}
		return result.toString();

	}

}
