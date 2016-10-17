package servlets;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import services.CommentRateService;
import tools.Tools;

public class RateServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		@SuppressWarnings("unchecked")
		Map<String, String> params = req.getParameterMap();
		String sessionId, adresse, note;
		
		if (params.containsKey("session_id") && params.containsKey("adresse") && params.containsKey("note")) {
			sessionId = req.getParameter("session_id");
			adresse = req.getParameter("adresse");
			note = req.getParameter("note");
			
			resp.getWriter().write(CommentRateService.commentRate(sessionId, adresse, note, false));
			
		} else {
			resp.getWriter().write(Tools.erreurParam);
		}
	}
	
}
