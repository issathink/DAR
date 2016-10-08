package servlets;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import services.LogoutService;
import services.SeenMessageService;

public class SeenMessageServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		@SuppressWarnings("unchecked")
		Map<String, String> params = req.getParameterMap();

		if(params.containsKey("id_user") && params.containsKey("id_other")) {
			String userId = req.getParameter("id_user");
			String friendId = req.getParameter("id_other");
			String response = SeenMessageService.getMessages(userId, friendId);
			resp.getWriter().write(response);
		}
		else { // Gerer cas erreur
			resp.getWriter().write("Erreur : Argument 'id_user' ou 'id_other' n'existe pas");
		}
	}

}
