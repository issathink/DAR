package servlets;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import services.SearchService;
import tools.Tools;

public class SearchServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		@SuppressWarnings("unchecked")
		Map<String, String> params = req.getParameterMap();
		String session_id = null;
		String adresse = null;

		if (params.containsKey("session_id") && params.containsKey("adresse")) {
			session_id = req.getParameter("session_id");
			adresse = req.getParameter("adresse");

			resp.getWriter().write(SearchService.search(session_id, adresse));

		} else if(params.containsKey("adresse")) {
			session_id = req.getParameter("session_id");
			resp.getWriter().write(SearchService.search(session_id, adresse));
		} else {
			resp.getWriter().write(Tools.erreurParam);
		}
	}

}
