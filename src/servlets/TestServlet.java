package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import tools.DBStatic;

public class TestServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String s = req.getParameter("q");
		// resp.getWriter().write(escapeString(s));
		
		String table = DBStatic.mysql_db+".test";

		// String request = "SELECT id FROM " + table + " WHERE session_id='"+s+"'";


		ResultSet res;
		JSONObject jsonResult = new JSONObject();
		Connection connexion = null;
		// Statement statement = null;
		PreparedStatement prepStatement;

		try {
			connexion = DBStatic.getMySQLConnection();
			prepStatement = connexion.prepareStatement("SELECT * FROM " + table + " WHERE texte=?");
			prepStatement.setString(1, s);
			
			// statement = connexion.createStatement();
			// res = statement.executeQuery(request);
			res = prepStatement.executeQuery();

			
			if(res.next() == false) {
				// N'existe pas
				jsonResult.put("erreur :", "Nop");
			} else {
				
				jsonResult.put("ok", res.getString("texte"));
			}

			if(prepStatement != null)
				prepStatement.close();
			if(connexion != null)
				connexion.close();

		} catch (SQLException e) {
			resp.getWriter().write(e.getMessage());
		} catch (JSONException e) {
			resp.getWriter().write(e.getMessage());
		}

		resp.getWriter().write(jsonResult.toString());
	}
	
	public String escapeString(String s) {
		return s;
	}
	
	public String unescapeString(String s) {
		return s;
	}

}
