package servlets;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import services.SeenMessageService;

public class SeenMessageServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		@SuppressWarnings("unchecked")
		Map<String, String> params = req.getParameterMap();

		if(params.containsKey("id_session") && params.containsKey("pseudo_other")) {
			String idSession = req.getParameter("id_session");
			String friendLogin = req.getParameter("pseudo_other");
			String response = SeenMessageService.getMessages(idSession, friendLogin);
			resp.getWriter().write(response);
		}
		else { // Gerer cas erreur
			resp.getWriter().write("{ \"Erreur\" : \"Argument 'id_session' ou 'pseudo_other' n'existe pas\" }");
		}
	}

}
