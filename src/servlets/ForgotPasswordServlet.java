package servlets;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import services.ForgotPasswordService;
import tools.Tools;

public class ForgotPasswordServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		@SuppressWarnings("unchecked")
		Map<String, String> params = req.getParameterMap();
		String mail, login;
		
		if (params.containsKey("mail") && params.containsKey("login")) {
			mail = req.getParameter("mail");
			login = req.getParameter("login");
			ForgotPasswordService fps = new ForgotPasswordService();
			resp.getWriter().write(fps.forgotPassword(mail, login));
		} else {
			resp.getWriter().write(Tools.erreurParam);
		}
	}

}
