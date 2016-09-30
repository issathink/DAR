package servlets;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import services.CreateUserService;

/**
 * <b>CreateUserServlet est la servlet qui sert a creer un utilisateur.</b>
 * 
 * @param login
 *            - Login de l'utilisateur
 * @param pw
 *            - Mot de passe de l'utilisateur
 * 
 * @throws 1 Pas le bon nombre de parametres. 2 Si on a une SQLException. 3 Si
 *         on a une JSONException.
 * 
 * 
 */

public class CreateUserServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		@SuppressWarnings("unchecked")
		Map<String, String> params = req.getParameterMap();
		String email = null;

		if (params.containsKey("mail") && params.containsKey("name")
				&& params.containsKey("pass") && params.containsKey("address")) {

			email = req.getParameter("mail");
			String name = req.getParameter("name");
			String pass = req.getParameter("pass");
			String address = req.getParameter("address");

			resp.getWriter()
					.write(CreateUserService.createUser(email, name, pass,
							address));

		} else
			;;
			// resp.getWriter().write(Tools.erreurParam);
	}
}
