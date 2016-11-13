package servlets;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import services.SigninService;
import tools.Tools;

/**
 * <b>SigninServlet est la servlet qui sert a recuperer le login et le
 * mot de passe de l'utilisateur et le connecter.</b>
 * 
 * @param login
 *            - Login de l'utilisateur
 * @param pw
 *            - Mot de passe
 * 
 * @return Un JSONOBJECT contenant le couple {id, login key}.
 * 
 * @throws 1 Pas le bon nombre de parametres. 2 Si on a une SQLException. 3 Si
 *         on a une JSONException.
 * 
 * 
 */

public class SigninServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	JSONObject result;
	String res = "";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		result = new JSONObject();
		@SuppressWarnings("unchecked")
		Map<String, String> params = req.getParameterMap();

		if (params.containsKey("login") && params.containsKey("pw")) {
			String login = req.getParameter("login");
			String pw = req.getParameter("pw");

			resp.getWriter().println(
					SigninService.authenticateUser(login, pw));

		} else
			resp.getWriter().println(Tools.erreurParam);

	}

}
