package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.json.JSONException;
import org.json.JSONObject;

import tools.BCrypt;
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
		PreparedStatement statement = null;
		ResultSet loginRes = null;
		ResultSet resMail = null;
		JSONObject result = new JSONObject();
		String userId = "";

		try {
			conn = DBStatic.getMySQLConnection();
			String q = "select id from " + DBStatic.mysql_db +  ".users where login=?";
			statement = conn.prepareStatement(q);
			statement.setString(1, login);
			loginRes = statement.executeQuery();
			if(loginRes.next())
				userId = loginRes.getString("id");
			else {
				result.put("erreur", "Login does not exist");
				return result.toString();
			}
				
			String query = "select mail from " + DBStatic.mysql_db +  ".users where id=?";
			if(userId != null) {
				statement = conn.prepareStatement(query);
				statement.setInt(1, Integer.parseInt(userId));
				resMail = statement.executeQuery();
				
				if(resMail.next()) {
					if(mail.equals(resMail.getString("mail"))) {
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
						
						String update = "UPDATE " + DBStatic.mysql_db +  ".users SET pw=? where id=?";
						statement = conn.prepareStatement(update);
						statement.setString(1, BCrypt.hashpw(new_pw, BCrypt.gensalt()));
						statement.setInt(2, Integer.parseInt(userId));
						
						if(statement.executeUpdate() > 0) 
							result.put("ok", "Password changed");
						else
							result.put("erreur", "Unexpected error please try again later");

					} else {
						result.put("erreur", "Invalid mail.");
					}
				} else {
					result.put("erreur", "No mail.");
				}
			} else {
				result.put("erreur", "Invalid session id.");
			}
		} catch (SQLException e) {
			if (e.getErrorCode() == 0 && e.toString().contains("CommunicationsException"))
				return forgotPassword(mail, login);
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
