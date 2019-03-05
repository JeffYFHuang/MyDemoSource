package com.liteon.icgwearable.util;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import com.liteon.icgwearable.exception.MailNotSendException;

public class ProcessMailUtility {

	private static Logger log = Logger.getLogger(ProcessMailUtility.class);
	private Properties properties;
	private String fromMailId;
	private String password;
	@Value("${application.url}")
	private String baseUrl;

	public ProcessMailUtility() {

	}

	public ProcessMailUtility(Properties properties, String fromMailId, String password) {
		this.properties = properties;
		this.fromMailId = fromMailId;
		this.password = password;
	}

	public void sentMail(String name, String activationCode, String recipients, String userRole) {
		if (null != fromMailId && null != password && !fromMailId.equals("") && !password.equals("")) {
			Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {

					return new PasswordAuthentication(fromMailId, password);

				}
			});

			try {
				String resetLinkPage = "/AdminPasswordResetLink";
				if(userRole.equals(Constant.ParentAdmin) || userRole.equals(Constant.ParentMember)) {
					resetLinkPage = "/PasswordResetLink";
				}
				Message message = new MimeMessage(session);
				log.info("fromMailId" + "\t" + fromMailId);
				message.setFrom(new InternetAddress(fromMailId));
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
				String msg2 = "<html>\n" + "<head>\n" + "<title></title>\n" + "</head>\n" + "<body>\n" + "<p>\n"
						+ "Dear " + name + ",</p> \n" 
						+ "&nbsp;</p>\n" + "<p>\n"
						+ "You recently requested to reset your password for your iCampus Guardian account. Use this link below to reset your password. This password reset is only valid for the next 1 hour.</p>\n"
						+ "<p>\n" + " <a href=" + baseUrl + resetLinkPage + "?key="
						+ activationCode + ">" + baseUrl + resetLinkPage + "?key=" + activationCode + "</a></p>\n"
						+ "If you did not request a password reset, please ignore this email or contact support if you have questions."
						+ "&nbsp;</p>\n" + "<p>\n" 
						+"<p>\n" + "Thanks,</p>\n" 
						+ "<p>" + "iCampus Guardian Support</p>" 
						+ "<p>\n" + "&nbsp;</p>"
						+ "Copyright &copy; 2017 iCampus Guardian. All Rights Reserved.</p>\n "
						+ "</body>\n"
						+ "</html>\n";
				message.setSubject("iCampus Guardian Password Reset");
				message.setContent(msg2, "text/html; charset=UTF-8;");
				Transport.send(message);
				log.info("**activationCode**" + "\t" + activationCode);
				log.info("done");
				log.info("Message Sent");
			} catch (Exception ex) {
				log.fatal("Message not Sent", ex);
				throw new MailNotSendException("Issue with SMTP connection, Mail Cannot Be Sent");

			}
		} else {
			log.error("Email parameters are missing");
		}
	}

	/**
	 * Overloaded method sentMail to handle one more additional parameter for reset password
	 * validity time
	 * 
	 * @param name - user name
	 * @param activationCode - activation code
	 * @param recipients - recipients
	 * @param userRole - user role
	 * @param resetPasswordValidity - time in minutes
	 */
	public void sentMail(String name, String activationCode, String recipients, String userRole, int resetPasswordValidity) {
		if (null != fromMailId && null != password && !fromMailId.equals("") && !password.equals("")) {
			Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {

					return new PasswordAuthentication(fromMailId, password);

				}
			});

			try {
				String resetLinkPage = "/AdminPasswordResetLink";
				if(userRole.equals(Constant.ParentAdmin) || userRole.equals(Constant.ParentMember)) {
					resetLinkPage = "/PasswordResetLink";
				}
				
				String linkValidity = "1 hour";
				if (resetPasswordValidity < 60) {
					linkValidity = Integer.toString(resetPasswordValidity) + " minutes";
				}
				Message message = new MimeMessage(session);
				log.info("fromMailId" + "\t" + fromMailId);
				message.setFrom(new InternetAddress(fromMailId));
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
				String msg2 = "<html>\n" + "<head>\n" + "<title></title>\n" + "</head>\n" + "<body>\n" + "<p>\n"
						+ "Dear " + name + ",</p> \n" 
						+ "&nbsp;</p>\n" + "<p>\n"
						+ "You recently requested to reset your password for your iCampus Guardian account. "
						+ "Use this link below to reset your password. This password reset is only valid for the next "
						+ linkValidity + ".</p>\n"
						+ "<p>\n" + " <a href=" + baseUrl + resetLinkPage + "?key="
						+ activationCode + ">" + baseUrl + resetLinkPage + "?key=" + activationCode + "</a></p>\n"
						+ "If you did not request a password reset, please ignore this email or contact support if you have questions."
						+ "&nbsp;</p>\n" + "<p>\n" 
						+"<p>\n" + "Thanks,</p>\n" 
						+ "<p>" + "iCampus Guardian Support</p>" 
						+ "<p>\n" + "&nbsp;</p>"
						+ "Copyright &copy; 2017 iCampus Guardian. All Rights Reserved.</p>\n "
						+ "</body>\n"
						+ "</html>\n";
				message.setSubject("iCampus Guardian Password Reset");
				message.setContent(msg2, "text/html; charset=UTF-8;");
				Transport.send(message);
				log.info("**activationCode**" + "\t" + activationCode);
				log.info("done");
				log.info("Message Sent");
			} catch (Exception ex) {
				log.fatal("Message not Sent", ex);
				throw new MailNotSendException("Issue with SMTP connection, Mail Cannot Be Sent");

			}
		} else {
			log.error("Email parameters are missing");
		}
	}

	public void signupSentMail(String name, String activationCode, String recipients) {
		if (null != fromMailId && null != password && !fromMailId.equals("") && !password.equals("")) {
			Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(fromMailId, password);
				}
			});
			try {
				Message message = new MimeMessage(session);
				log.info("fromMailId" + "\t" + fromMailId);
				message.setFrom(new InternetAddress(fromMailId));
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
				String msg2 = "<html>\n" + "<head>\n" + "<title></title>\n" + "</head>\n" + "<body>\n" + "<p>\n"
						+ "Dear " + name + ",</p> \n" 
						+ "&nbsp;</p>\n" + "<p>\n"
						+ "Welcome to iCampus Guardian. To get started, please click on the link below to activate your account!</p>\n" + "<p>\n"
						+ "<a href=" + baseUrl + "/SignupActivationRequest?key=" + activationCode + ">"
						+ baseUrl + "/SignupActivationRequest?key=" + activationCode + "</a></p>\n" 
						+ "&nbsp;</p>\n" + "<p>\n" 
						+"<p>\n" + "Thanks,</p>\n" 
						+ "<p>" + "iCampus Guardian Support</p>" 
						+ "<p>\n" + "&nbsp;</p>"
						+ "Copyright &copy; 2017 iCampus Guardian. All Rights Reserved.</p>\n "
						+ "</body>\n"
						+ "</html>\n";
				message.setSubject("iCampus Guardian Account Activation ");
				message.setContent(msg2, "text/html; charset=UTF-8;");
				Transport.send(message);
				log.info("**activationCode**" + "\t" + activationCode);
				log.info("done");
				log.info("Message Sent");
			} catch (Exception ex) {
				log.fatal("Message not Sent", ex);
				throw new MailNotSendException("Issue with SMTP connection, Mail Cannot Be Sent");
			}
		} else {
			log.error("Email parameters are missing");
		}
	}

	public void userActivationByEmail(String name, String passwordActivationCode, String recipients) {
		if (null != fromMailId && null != password && !fromMailId.equals("") && !password.equals("")) {
			Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(fromMailId, password);
				}
			});
			try {
				Message message = new MimeMessage(session);
				log.info("fromMailId[" + fromMailId + "], password[" + password + "]");
				message.setFrom(new InternetAddress(fromMailId));
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
				String msg2 = "<html>\n" + "<head>\n" + "<title></title>\n" + "</head>\n" + "<body>\n" + "<p>\n"
						+ "Dear " + name + ",</p> \n" 
						+ "&nbsp;</p>\n" + "<p>\n"
						+ "Welcome to iCampus Guardian. To get started, please click on the link below to activate your account!</p>\n"
						+ "<p>\n" + " <a href=" + baseUrl + "/UserActivationRequest?key="
						+ passwordActivationCode + ">" + baseUrl + "/UserActivationRequest?key="
						+ passwordActivationCode + "</a></p>\n" 
						+ "&nbsp;</p>\n" + "<p>\n" 
						+"<p>\n" + "Thanks,</p>\n" 
						+ "<p>" + "iCampus Guardian Support</p>" 
						+ "<p>\n" + "&nbsp;</p>"
						+ "Copyright &copy; 2017 iCampus Guardian. All Rights Reserved.</p>\n "
						+ "</body>\n"
						+ "</html>\n";
				message.setSubject("iCampus Guardian Account Activation");
				message.setContent(msg2, "text/html; charset=UTF-8;");
				Transport.send(message);
				log.info("**activationCode**" + "\t" + passwordActivationCode);
				log.info("done");
				log.info("Message Sent");
			} catch (Exception ex) {
				log.fatal("Message not Sent", ex);
				throw new MailNotSendException("Issue with SMTP connection, Mail Cannot Be Sent");
			}
		} else {
			log.error("Email parameters are missing");
		}
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

}
