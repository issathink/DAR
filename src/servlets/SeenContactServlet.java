package servlets;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import services.SeenContactService;


public class SeenContactServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		@SuppressWarnings("unchecked")
		Map<String, String> params = req.getParameterMap();

		if(params.containsKey("id_session")) {
			String idSession = req.getParameter("id_session");
			String response = SeenContactService.getMessages(idSession);
			resp.getWriter().write(response);
		}
		else {
			resp.getWriter().write("{ \"Erreur\" : \"Argument 'id_session' n'existe pas\" }");
		}
	}
}
