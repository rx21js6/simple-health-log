package jp.nauplius.app.shl.maint.service;

import java.util.Properties;

import javax.inject.Named;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.common.service.MailSenderService;

/**
 * カスタム設定サービス
 *
 */
@Named
public class CustomSettingMailSender extends MailSenderService {
    /**
     * テストメール送信
     *
     * @param maiAddress
     */
    public void sendTestMail(String maiAddress) {
        Properties props = new Properties();
        props.put("mail.smtp.host", this.mailSenderBean.getHost());
        props.put("mail.smtp.port", this.mailSenderBean.getPort());

        Session session = this.createSession(props);

        try {
            MimeMessage messageContent = new MimeMessage(session);
            messageContent.setFrom(new InternetAddress(maiAddress, maiAddress, CHARSET));
            messageContent.addRecipient(Message.RecipientType.TO, new InternetAddress(maiAddress));
            String messageSubject = this.messageBundle
                    .getString("contents.maint.settings.cutomSetting.msg.testMailTitle");
            messageContent.setSubject(messageSubject, CHARSET);
            messageContent.setContent(this.buildTestMailText(maiAddress), MAIL_CONTENT_TYPE);

            Transport.send(messageContent);
        } catch (Exception e) {
            throw new SimpleHealthLogException(e);
        }
    }

    /**
     * アドレス変更メール送信
     *
     * @param maiAddress
     */
    public void sendChangedMail(String maiAddress) {
        Properties props = new Properties();
        props.put("mail.smtp.host", this.mailSenderBean.getHost());
        props.put("mail.smtp.port", this.mailSenderBean.getPort());

        Session session = this.createSession(props);

        try {
            MimeMessage messageContent = new MimeMessage(session);
            messageContent.setFrom(new InternetAddress(maiAddress, maiAddress, CHARSET));
            messageContent.addRecipient(Message.RecipientType.TO, new InternetAddress(maiAddress));
            String messageSubject = this.messageBundle
                    .getString("contents.maint.settings.cutomSetting.msg.testMailTitle");
            messageContent.setSubject(messageSubject, CHARSET);
            messageContent.setContent(this.buildChangedMailText(maiAddress), MAIL_CONTENT_TYPE);

            Transport.send(messageContent);
        } catch (Exception e) {
            throw new SimpleHealthLogException(e);
        }
    }

    /**
     * テストメール本文生成
     *
     * @param mailAddress
     * @return 送信
     */
    private String buildTestMailText(String mailAddress) {
        StringBuilder mailMessageBuilder = new StringBuilder();

        mailMessageBuilder.append(mailAddress);
        mailMessageBuilder.append("\n");

        return mailMessageBuilder.toString();
    }

    /**
     * アドレス変更メール本文生成
     *
     * @param mailAddress
     * @return 送信
     */
    private String buildChangedMailText(String mailAddress) {
        StringBuilder mailMessageBuilder = new StringBuilder();

        mailMessageBuilder.append(mailAddress);
        mailMessageBuilder.append("\n");

        return mailMessageBuilder.toString();
    }
}
