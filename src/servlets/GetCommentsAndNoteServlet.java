package servlets;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import services.CommentRateService;
import services.GetCommentsAndNoteService;
import tools.Tools;

public class GetCommentsAndNoteServlet extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		@SuppressWarnings("unchecked")
		Map<String, String> params = req.getParameterMap();
		String adresse;
		
		if (params.containsKey("adresse")) {
			adresse = req.getParameter("adresse");
			
			resp.getWriter().write(GetCommentsAndNoteService.getComments(adresse));
			
		} else {
			resp.getWriter().write(Tools.erreurParam);
		}
	}
	

}
