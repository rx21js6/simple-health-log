package jp.nauplius.app.shl.common.service;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.page.initial.backing.InitialSettingForm;

@Named
public class MailSenderService implements Serializable {
    private static final String CHARSET = StandardCharsets.UTF_8.toString();

    @Inject
    private FacesContext facesContext;

    public void sendDummymail() {
        Properties props = new Properties();
        props.put("mail.smtp.host", "localhost");
        props.put("mail.smtp.port", 25);

        Session session = Session.getInstance(props);

        try {
            MimeMessage messageContent = new MimeMessage(session);
            messageContent.setFrom(new InternetAddress("mail_sender@foo.com", "mail sender name", CHARSET));
            messageContent.addRecipient(Message.RecipientType.TO, new InternetAddress("to_address@foo.com"));
            messageContent.setSubject("標題", CHARSET);
            messageContent.setContent("メール本文。改行は\nで行う。", "text/plain;charset=UTF-8");

            Transport.send(messageContent);
        } catch (Exception e) {
            throw new SimpleHealthLogException(e);
        }
    }

    public void sendInitialSettingMail(InitialSettingForm initialSettingForm) {
        String varName = "msg";
        ResourceBundle bundle = PropertyResourceBundle.getBundle(varName);

        String mailMessage = this.buildInitialMailMessageText(initialSettingForm, bundle);
        Properties props = new Properties();
        props.put("mail.smtp.host", "localhost");
        props.put("mail.smtp.port", 25);

        Session session = Session.getDefaultInstance(props, null);

        try {
            MimeMessage messageContent = new MimeMessage(session);
            messageContent.setFrom(new InternetAddress(initialSettingForm.getMailAddress(), initialSettingForm.getName(), CHARSET));
            messageContent.addRecipient(Message.RecipientType.TO, new InternetAddress(initialSettingForm.getMailAddress()));
            String messageSubject = bundle.getString("initial.initialSetting.mail.subject");
            messageContent.setSubject(messageSubject, CHARSET);
            messageContent.setContent(mailMessage, "text/plain;charset=UTF-8");

            Transport.send(messageContent);
        } catch (Exception e) {
            throw new SimpleHealthLogException(e);
        }
    }

    private String buildInitialMailMessageText(InitialSettingForm initialSettingForm, ResourceBundle bundle) {

        try {
            StringBuilder mailMessageBuilder = new StringBuilder();
            ExternalContext externalContext = this.facesContext.getExternalContext();
            HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
            String contextPath = request.getContextPath();

            HttpSession session = request.getSession();
            Locale locale = (Locale) session.getAttribute("locale");

            InetAddress inet = InetAddress.getLocalHost();
            String hostName = inet.getHostName();
            String messageBase1 = bundle.getString("initial.initialSetting.mail.format1");
            MessageFormat format1 = new MessageFormat(messageBase1);
            format1.setLocale(locale);
            String message1 = format1
                    .format(new String[] { initialSettingForm.getLoginId(), initialSettingForm.getMailAddress() });

            String messageBase2 = bundle.getString("initial.initialSetting.mail.format2");
            MessageFormat format2 = new MessageFormat(messageBase2);
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append("http://");
            urlBuilder.append(hostName);
            urlBuilder.append(contextPath);
            urlBuilder.append("/");
            String message2 = format2.format(new String[] { urlBuilder.toString() });

            System.out.println(message1);
            System.out.println(message2);

            mailMessageBuilder.append(message1);
            mailMessageBuilder.append("\n");
            mailMessageBuilder.append(message2);

            return mailMessageBuilder.toString();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new SimpleHealthLogException(e);
        }
    }

}
