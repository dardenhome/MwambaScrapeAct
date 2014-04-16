package com.mwambachildrenschoir.email;


import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;

public class Email {
	public static void sendHtml(String subject, String html, String addressListName) throws Exception {
		XMLPropertiesConfiguration props = new XMLPropertiesConfiguration(System.getProperty("emailpropertiesfile"));
		
	    HtmlEmail htmlEmail = new HtmlEmail();
	    htmlEmail.setSmtpPort(props.getInt("smtp.port"));
	    htmlEmail.setAuthenticator(new DefaultAuthenticator(props.getString("smtp.user"), props.getString("smtp.pass")));
	    htmlEmail.setDebug(false);
	    htmlEmail.setHostName(props.getString("smtp.host"));
	    htmlEmail.setFrom(props.getString("smtp.from"));
	    htmlEmail.setSubject(subject);
	    htmlEmail.setHtmlMsg(html);
	    htmlEmail.setTextMsg("Your email client does not support HTML messages");
	    for (String address : props.getStringArray(addressListName)) htmlEmail.addBcc(address);
	    htmlEmail.setStartTLSEnabled(true);
	    htmlEmail.send();
	}
}
