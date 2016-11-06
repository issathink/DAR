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
		String adresse = null;
		String apiname = null;
		double dist;

		if(params.containsKey("adresse") && params.containsKey("apiname") && params.containsKey("dist")) {
			adresse = req.getParameter("adresse");
			apiname = req.getParameter("apiname");
			dist = Double.valueOf(req.getParameter("dist"));
			resp.getWriter().write(SearchService.search(adresse, apiname, dist));
		} else {
			resp.getWriter().write(Tools.erreurParam);
			resp.getWriter().write("rien");
		}
	}

}
