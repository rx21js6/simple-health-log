package jp.nauplius.app.shl.common.service;

import java.io.InputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.common.ui.bean.MailSenderBean;
import jp.nauplius.app.shl.page.initial.backing.InitialSettingForm;

@Named
public class MailSenderService implements Serializable {
    private static final String DEFAULT_MAIL_SENDER = "simple-health-log";
    private static final String CHARSET = StandardCharsets.UTF_8.toString();
    private static final String SETTING_XML_PATH = "/META-INF/mail-setting.xml";
    private static final String MAIL_CONTENT_TYPE = "text/plain;charset=UTF-8";

    @Inject
    private Logger logger;

    @Inject
    private transient ResourceBundle messageBundle;

    @Inject
    private LocaleService localeService;

    // @Inject
    // private FacesContext facesContext;

    private MailSenderBean mailSenderBean;
    @PostConstruct
    public void init() {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        factory.setIgnoringElementContentWhitespace(true);
        try {
            // XML読み込み
            InputStream is = this.getClass().getResourceAsStream(SETTING_XML_PATH);
            JAXBContext jaxbContext = JAXBContext.newInstance(MailSenderBean.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            this.mailSenderBean = (MailSenderBean) unmarshaller.unmarshal(is);
            this.logger.info("host: " + this.mailSenderBean.getHost());

        } catch (Throwable e) {
            throw new SimpleHealthLogException(e);
        }
    }

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
            String sender = DEFAULT_MAIL_SENDER + "@" + InetAddress.getLocalHost();

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

    /**
     * セッション生成
     *
     * @param props
     * @return
     */
    private Session createSession(Properties props) {
        Session session = null;
        if (this.mailSenderBean.isAuth()) {
            // 587？
            session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(mailSenderBean.getUserId(), mailSenderBean.getPassword());
                }
            });
        } else {
            // 25？
            session = Session.getInstance(props, null);
        }
        return session;
    }
}
