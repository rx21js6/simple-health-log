package jp.nauplius.app.shl.page.initial.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;

import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.common.service.AbstractMailSender;
import jp.nauplius.app.shl.page.initial.backing.InitialSettingForm;

@Named
public class InitialSettingMailSender extends AbstractMailSender {
    @Inject
    private Logger logger;

    /**
     * 初期設定メール送信
     *
     * @param initialSettingForm
     */
    public void sendInitialSettingMail(String contextPath, InitialSettingForm initialSettingForm) {
        String mailMessage = this.buildInitialMailMessageText(contextPath, initialSettingForm);
        Properties props = new Properties();
        props.put("mail.smtp.host", this.mailSenderBean.getHost());
        props.put("mail.smtp.port", this.mailSenderBean.getPort());

        Session session = this.createSession(props);

        try {
            MimeMessage messageContent = new MimeMessage(session);
            messageContent.setFrom(
                    new InternetAddress(initialSettingForm.getMailAddress(), initialSettingForm.getName(), CHARSET));
            messageContent.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(initialSettingForm.getMailAddress()));
            String messageSubject = this.messageBundle.getString("initial.initialSetting.mail.subject");
            messageContent.setSubject(messageSubject, CHARSET);
            messageContent.setContent(mailMessage, MAIL_CONTENT_TYPE);

            Transport.send(messageContent);
        } catch (Exception e) {
            throw new SimpleHealthLogException(e);
        }
    }

    /**
     * 初期登録メール生成
     *
     * @param contextPath
     * @param initialSettingForm
     * @return
     */
    private String buildInitialMailMessageText(String contextPath, InitialSettingForm initialSettingForm) {

        try {
            StringBuilder mailMessageBuilder = new StringBuilder();
            // ExternalContext externalContext = this.facesContext.getExternalContext();
            // HttpServletRequest request = (HttpServletRequest)
            // externalContext.getRequest();
            // String contextPath = request.getContextPath();

            // HttpSession session = request.getSession();
            // Locale locale = (Locale) session.getAttribute("locale");

            InetAddress inet = InetAddress.getLocalHost();
            String hostName = inet.getHostName();
            String messageBase1 = this.messageBundle.getString("initial.initialSetting.mail.format1");
            MessageFormat format1 = new MessageFormat(messageBase1);
            format1.setLocale(this.localeService.getLocale());
            String message1 = format1
                    .format(new String[] { initialSettingForm.getLoginId(), initialSettingForm.getMailAddress() });

            String messageBase2 = this.messageBundle.getString("initial.initialSetting.mail.format2");
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
