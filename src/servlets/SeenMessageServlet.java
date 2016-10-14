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

		if(params.containsKey("pseudo_user") && params.containsKey("pseudo_other")) {
			String userLogin = req.getParameter("pseudo_user");
			String friendLogin = req.getParameter("pseudo_other");
			String response = SeenMessageService.getMessages(userLogin, friendLogin);
			resp.getWriter().write(response);
		}
		else { // Gerer cas erreur
			resp.getWriter().write("{ \"Erreur\" : \"Argument 'pseudo_user' ou 'pseudo_other' n'existe pas\" }");
		}
	}

}
