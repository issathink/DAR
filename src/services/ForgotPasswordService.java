package services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;

import javax.mail.*;
import javax.mail.internet.*;

import tools.DBStatic;
import tools.PwGenerator;
import tools.Tools;

public class ForgotPasswordService {

	private class SMTPAuthenticator extends javax.mail.Authenticator {
		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication("FindYourFlatCorp@gmail.com", "findyourflat");
		}
	}

	public String forgotPassword(String mail, String login){
		Connection conn = null;
		Statement statement = null;
		ResultSet LoginRes = null;
		ResultSet ResMail = null;
		JSONObject result = new JSONObject();
		String userId = "";

		try {
			conn = DBStatic.getMySQLConnection();
			statement = (Statement) conn.createStatement();
			String q = "select id from " + DBStatic.mysql_db +  ".users where login='" + login + "'";
			LoginRes = statement.executeQuery(q);
			if(LoginRes.next())
				userId = LoginRes.getString("id");
			else {
				result.put("erreur", "Login does not exist");
				return result.toString();
			}
				
			String query = "select mail from " + DBStatic.mysql_db +  ".users where id='" + userId + "'";
			if(userId != null) {
				ResMail = statement.executeQuery(query);
				if(ResMail.next()) {
					if(mail.equals(ResMail.getString("mail"))){
						
						PwGenerator pg = new PwGenerator();
						String new_pw = pg.nextSessionId();

						String d_email = "FindYourFlatCorp@gmail.com",
								//d_password = "findyourflat",
								d_host = "smtp.gmail.com",
								d_port = "465",
								m_to = mail,
								m_subject = "FYF New password",
								m_text = "Here is your new password : " + new_pw;

						Properties props = new Properties();
						props.put("mail.smtp.user", d_email);
						props.put("mail.smtp.host", d_host);
						props.put("mail.smtp.port", d_port);
						props.put("mail.smtp.starttls.enable", "true");
						props.put("mail.smtp.auth", "true");
						props.put("mail.smtp.socketFactory.port", d_port);
						props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
						props.put("mail.smtp.socketFactory.fallback", "false");

						Authenticator auth = new SMTPAuthenticator();
						Session session = Session.getInstance(props, auth);     
						MimeMessage msg = new MimeMessage(session);
						msg.setText(m_text);
						msg.setSubject(m_subject);
						msg.setFrom(new InternetAddress(d_email));
						msg.addRecipient(Message.RecipientType.TO, new InternetAddress(m_to));
						Transport.send(msg);
						
						String update = "UPDATE " + DBStatic.mysql_db +  ".users SET pw='" + new_pw + "' where id='" + userId + "'";
						if(statement.executeUpdate(update) > 0) 
							result.put("ok", "Password changed");
						else
							result.put("erreur", "Unexpected error please try again later");

					}else
						result.put("erreur", "Invalid mail.");
				}
				else
					result.put("erreur", "No mail.");
			} else {
				result.put("erreur", "Invalid session id.");
			}

		} catch (SQLException e) {
			int error = e.getErrorCode();
			if (error == 0 && e.toString().contains("CommunicationsException")){
				return forgotPassword(mail, login);
			}
			else
				return Tools.erreurSQL + e.getMessage();
		} catch (JSONException e) {
			return Tools.erreurJSON;
		} catch (MessagingException mex) {
			try {
				result.put("erreur", mex.getCause());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		try {
			if (statement != null)
				statement.close();
			if (conn != null)
				conn.close();
		} catch (SQLException e) {}

		return result.toString();

	}

}
