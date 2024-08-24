package jp.nauplius.app.shl.page.login.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.common.service.AbstractMailSender;

@Named
@SessionScoped
public class LoginMailSender extends AbstractMailSender {

    /**
     * パスワード初期化メール送信
     *
     * @param userInfo
     */
    public void sendPasswordResetMail(String passwordText, String toMailAddress, String adminMainAddress) {
        Properties props = new Properties();
        props.put("mail.smtp.host", this.mailSenderBean.getHost());
        props.put("mail.smtp.port", this.mailSenderBean.getPort());

        Session session = this.createSession(props);

        try {
            String sender = this.getDefaultSender();

            MimeMessage messageContent = new MimeMessage(session);
            messageContent.setFrom(new InternetAddress(adminMainAddress, sender, CHARSET));
            messageContent.addRecipient(Message.RecipientType.TO, new InternetAddress(toMailAddress));

            String messageSubject = this.messageBundle.getString("resetPassword.resetPassword.title");
            messageContent.setSubject(String.format("[%s]%s", sender, messageSubject), CHARSET);
            messageContent.setContent(createPasswordResetMail(passwordText), MAIL_CONTENT_TYPE);

            Transport.send(messageContent);
        } catch (Exception e) {
            throw new SimpleHealthLogException(e);
        }
    }

    /**
     * パスワード初期化メール作成
     *
     * @param passwordText
     * @return
     */
    private String createPasswordResetMail(String passwordText) {
        List<String> mailTexts = new ArrayList<>();
        mailTexts.add(this.messageBundle.getString("resetPassword.resetPassword.title"));
        mailTexts.add(StringUtils.EMPTY);
        mailTexts.add(passwordText);
        mailTexts.add(StringUtils.EMPTY);
        return mailTexts.stream().collect(Collectors.joining("\n"));
    }
}
