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

		if(params.containsKey("pseudo_user")) {
			String userLogin = req.getParameter("pseudo_user");
			String response = SeenContactService.getMessages(userLogin);
			resp.getWriter().write(response);
		}
		else { // Gerer cas erreur
			resp.getWriter().write("{ \"Erreur\" : \"Argument 'pseudo_user' n'existe pas\" }");
		}
	}
}
