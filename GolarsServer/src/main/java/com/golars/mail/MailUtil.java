package com.golars.mail;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.golars.bean.User;
import com.golars.util.CMSConstants;

public class MailUtil {
	public static void main(String[] args) {

		// sendEmail("avsrinivasa@gmail.com");
	}

	Message fetchEmailProperties() {
		Properties emailProperties = new Properties();
		try {
			Class<MailUtil> cl = MailUtil.class;

			emailProperties.load(cl.getResourceAsStream("/emailconfig.properties"));
		} catch (Exception e1) {
			System.out.println("Email Configuration fiele not found" + e1.getMessage());
		}
		final String username = emailProperties.getProperty("username");
		final String password = emailProperties.getProperty("password");
		String fromAddress = emailProperties.getProperty("from.address");

		Properties props = new Properties();
		props.put("mail.smtp.auth", emailProperties.get("mail.smtp.auth"));
		props.put("mail.smtp.starttls.enable", emailProperties.get("mail.smtp.starttls.enable"));
		props.put("mail.smtp.host", emailProperties.getProperty("mail.smtp.host"));
		props.put("mail.smtp.port", emailProperties.getProperty("mail.smtp.port"));
		props.put("mail.debug", emailProperties.getProperty("mail.debug"));

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
		Message message = new MimeMessage(session);
		try {

			message.setFrom(new InternetAddress(fromAddress));
		} catch (MessagingException e) {
			System.out.println("Exception occured during mail send --" + e.getMessage());
		}
		return message;
	}

	public void sendEmail(User userobj) {

		try {
			Message message = fetchEmailProperties();

			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userobj.getEmailAddress()));
			String messageText = "<p>Your account has been created in G360 content management system.</p> <p>Please use <a href='http://golars360.com/'>link</a> to login. </p>" + 
					"<p>Below are the login details: </p>" + 
					"<p>username: "+userobj.getUsername()+"</p>"+ 
					"<p>password: "+userobj.getPassword()+"</p>";
			message.setContent(messageText, "text/html");
			message.setSubject("New user registration with Golars 360");

			Transport.send(message);

			System.out.println("Mail sent succesfully to : " + userobj);

		} catch (MessagingException e) {
			System.out.println("Exception occured during mail send --" + e.getMessage());
		}
	}

	public  void sendforgotPasswordEmail(String toAddress, String username, String link) {
		try {
			Message message = fetchEmailProperties();

			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
			String messageText = "<h4> Golars 360 </h4>Reset Password <p>Please use the <a href="+link+"?username="+username+">link</a> to reset password ";
			message.setContent(messageText, "text/html; charset=utf-8");
			message.setSubject("Golars 360 Reset Password");

			Transport.send(message);

			System.out.println("Mail sent succesfully to : " + toAddress);

		} catch (MessagingException e) {
			System.out.println("Exception occured during mail send --" + e.getMessage());
		}

	}
}
