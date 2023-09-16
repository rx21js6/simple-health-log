package jp.nauplius.app.shl.maint.service;

import java.util.Properties;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.common.service.AbstractMailSender;

/**
 * カスタム設定サービス
 *
 */
@Named
@SessionScoped
public class CustomSettingMailSender extends AbstractMailSender {
    /**
     * パスワード変更メール送信
     *
     * @param mailAddress
     * @param adminMailAddress
     */
    public void sendPasswordChangedMail(String mailAddress, String adminMailAddress) {
        if (!this.mailSenderBean.isActive()) {
            return;
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", this.mailSenderBean.getHost());
        props.put("mail.smtp.port", this.mailSenderBean.getPort());

        Session session = this.createSession(props);

        try {
            String sender = this.getDefaultSender();

            MimeMessage messageContent = new MimeMessage(session);
            messageContent.setFrom(new InternetAddress(adminMailAddress, sender, CHARSET));
            messageContent.addRecipient(Message.RecipientType.TO, new InternetAddress(mailAddress));
            String messageSubject = this.messageBundle
                    .getString("contents.maint.settings.customSetting.msg.passwordChangedMailSubject");

            // 件名
            messageContent.setSubject(String.format("[%s]%s", sender, messageSubject), CHARSET);

            // 本文
            messageContent.setContent(this.buildPasswordChangedMailText(mailAddress), MAIL_CONTENT_TYPE);

            Transport.send(messageContent);
        } catch (Exception e) {
            throw new SimpleHealthLogException(this.messageBundle.getString("") + e);
        }
    }

    /**
     * テストメール送信
     *
     * @param maiAddress
     * @param adminMailAddress
     */
    public void sendTestMail(String maiAddress, String adminMailAddress) {
        if (!this.mailSenderBean.isActive()) {
            return;
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", this.mailSenderBean.getHost());
        props.put("mail.smtp.port", this.mailSenderBean.getPort());

        Session session = this.createSession(props);

        try {
            String sender = this.getDefaultSender();

            MimeMessage messageContent = new MimeMessage(session);
            messageContent.setFrom(new InternetAddress(adminMailAddress, sender, CHARSET));
            messageContent.addRecipient(Message.RecipientType.TO, new InternetAddress(maiAddress));
            String messageSubject = this.messageBundle
                    .getString("contents.maint.settings.customSetting.msg.testMailSubject");

            // 件名
            messageContent.setSubject(String.format("[%s]%s", sender, messageSubject), CHARSET);

            // 本文
            messageContent.setContent(this.buildTestMailText(maiAddress), MAIL_CONTENT_TYPE);

            Transport.send(messageContent);
        } catch (Exception e) {
            throw new SimpleHealthLogException(e);
        }
    }

    /**
     * アドレス変更メール送信
     *
     * @param mailAddress
     * @param adminMailAddress
     */
    public void sendAddressChangedMail(String mailAddress, String adminMailAddress) {
        if (!this.mailSenderBean.isActive()) {
            return;
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", this.mailSenderBean.getHost());
        props.put("mail.smtp.port", this.mailSenderBean.getPort());

        Session session = this.createSession(props);

        try {
            String sender = this.getDefaultSender();

            MimeMessage messageContent = new MimeMessage(session);
            messageContent.setFrom(new InternetAddress(adminMailAddress, sender, CHARSET));
            messageContent.addRecipient(Message.RecipientType.TO, new InternetAddress(mailAddress));
            String messageSubject = this.messageBundle
                    .getString("contents.maint.settings.customSetting.msg.addressChangedMailSubject");

            // 件名
            messageContent.setSubject(String.format("[%s]%s", sender, messageSubject), CHARSET);

            // 本文
            messageContent.setContent(this.buildAddressChangedMailText(mailAddress), MAIL_CONTENT_TYPE);

            Transport.send(messageContent);
        } catch (Exception e) {
            throw new SimpleHealthLogException(e);
        }
    }

    /**
     * パスワード変更メール本文生成
     *
     * @param mailAddress
     * @return
     */
    private String buildPasswordChangedMailText(String mailAddress) {
        StringBuilder mailMessageBuilder = new StringBuilder();

        mailMessageBuilder.append(
                this.messageBundle.getString("contents.maint.settings.customSetting.msg.passwordChangedMailMessage"));
        mailMessageBuilder.append("\n");

        return mailMessageBuilder.toString();
    }

    /**
     * テストメール本文生成
     *
     * @param mailAddress
     * @return 送信
     */
    private String buildTestMailText(String mailAddress) {
        StringBuilder mailMessageBuilder = new StringBuilder();

        mailMessageBuilder
                .append(this.messageBundle.getString("contents.maint.settings.customSetting.msg.testMailMessage"));
        mailMessageBuilder.append("\n");

        return mailMessageBuilder.toString();
    }

    /**
     * アドレス変更メール本文生成
     *
     * @param mailAddress
     * @return 送信
     */
    private String buildAddressChangedMailText(String mailAddress) {
        StringBuilder mailMessageBuilder = new StringBuilder();

        mailMessageBuilder.append(
                this.messageBundle.getString("contents.maint.settings.customSetting.msg.addressChangedMailMessage"));
        mailMessageBuilder.append("\n");

        return mailMessageBuilder.toString();
    }

}
