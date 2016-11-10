package servlets;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import services.GetCommentsAndNoteService;
import tools.Tools;

public class GetCommentsAndNoteServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		@SuppressWarnings("unchecked")
		Map<String, String> params = req.getParameterMap();
		String adresse;
		String distance;
		
		if (params.containsKey("adresse") && params.containsKey("distance") && Tools.isNumber(req.getParameter("distance"))) {
			adresse = req.getParameter("adresse");
			distance = req.getParameter("distance");
			resp.getWriter().write(GetCommentsAndNoteService.getComments(adresse, distance));
			
		} else {
			resp.getWriter().write(Tools.erreurParam);
		}
	}
	

}
