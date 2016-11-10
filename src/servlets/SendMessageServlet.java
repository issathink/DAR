package servlets;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;

import services.SendMessageService;

public class SendMessageServlet extends HttpServlet {

	// pseudo_sender, pseudo_receiver, message
	// TODO securiser message !!

	private static final long serialVersionUID = 1L;

	private String res;
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		@SuppressWarnings("unchecked")
		Map<String, String> params = req.getParameterMap();
		if(params.containsKey("id_session") && params.containsKey("pseudo_receiver") && params.containsKey("message")) {
			//this.doGet(req, resp);
			String sender = req.getParameter("id_session");
			String receiver = req.getParameter("pseudo_receiver");
			String message = req.getParameter("message");
			String response = SendMessageService.sendMessage(sender, receiver, message);
			res = response;
		}
		else {
			res = "{ \"Erreur\" : \"Argument 'id_session' ou 'pseudo_receiver' ou 'message' n'existe pas\" }";
		}
		resp.getWriter().write(res);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//		@SuppressWarnings("unchecked")
//		Map<String, String> params = req.getParameterMap();
//
//		if(params.containsKey("pseudo_sender") && params.containsKey("pseudo_receiver") && params.containsKey("message")) {
//			String sender = req.getParameter("pseudo_sender");
//			String receiver = req.getParameter("pseudo_receiver");
//			String message = req.getParameter("message");
//			String response = SendMessageService.sendMessage(sender, receiver, message);
//			resp.getWriter().write(response);
//		}
//		else { // Gerer cas erreur
//			resp.getWriter().write("{ \"Erreur\" : \"Argument 'pseudo_sender' ou 'pseudo_receiver' ou 'message' n'existe pas\" }");
//		}

		//resp.getWriter().write(res+" RES DU DoGET");
	}

}
