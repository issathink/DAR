package servlets;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import services.LogoutService;

public class LogoutServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		@SuppressWarnings("unchecked")
		Map<String, String> params = req.getParameterMap();

		if(params.containsKey("keyID")) {
			String idSession = req.getParameter("keyID");
			String response = LogoutService.logoutUser(idSession);
			resp.getWriter().write(response);
		}
		else { // Gerer cas erreur
			resp.getWriter().write("Erreur : Argument 'keyID' n'existe pas");
		}
	}
}
