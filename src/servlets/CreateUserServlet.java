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

		if (params.containsKey("mail") && params.containsKey("login")
				&& params.containsKey("pw") && params.containsKey("address")) {

			email = req.getParameter("mail");
			String login = req.getParameter("login");
			String password = req.getParameter("pw");
			String address = req.getParameter("address");
			String addressWork = req.getParameter("addressWork").trim();

			resp.getWriter()
					.write(CreateUserService.createUser(email, login, password,
							address, addressWork));

		} else
			;;
			// resp.getWriter().write(Tools.erreurParam);
	}
}
