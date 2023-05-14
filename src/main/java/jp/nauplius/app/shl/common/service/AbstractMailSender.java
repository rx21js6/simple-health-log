package jp.nauplius.app.shl.common.service;

import java.io.InputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;

import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.common.ui.bean.MailSenderBean;

public abstract class AbstractMailSender implements Serializable {
    public static final String DEFAULT_MAIL_SENDER = "simple-health-log";
    public static final String CHARSET = StandardCharsets.UTF_8.toString();
    public static final String SETTING_XML_PATH = "/META-INF/mail-setting.xml";
    public static final String MAIL_CONTENT_TYPE = "text/plain;charset=UTF-8";

    @Inject
    protected Logger logger;

    @Inject
    protected transient ResourceBundle messageBundle;

    @Inject
    protected LocaleService localeService;

    protected MailSenderBean mailSenderBean;

    @PostConstruct
    public void init() {
        this.logger.info("#init() being");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        factory.setIgnoringElementContentWhitespace(true);
        try {
            // XML読み込み
            InputStream is = this.getClass().getResourceAsStream(SETTING_XML_PATH);
            JAXBContext jaxbContext = JAXBContext.newInstance(MailSenderBean.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            this.mailSenderBean = (MailSenderBean) unmarshaller.unmarshal(is);
            this.logger.debug("host: " + this.mailSenderBean.getHost());

        } catch (Throwable e) {
            throw new SimpleHealthLogException(e);
        }

        this.logger.info("#init() complete");
    }

    /**
     * メール機能有効確認
     *
     * @return 有効な場合true
     */
    public boolean isActive() {
        return Objects.isNull(this.mailSenderBean) ? false : this.mailSenderBean.isActive();
    }

    /**
     * セッション生成
     *
     * @param props
     * @return
     */
    protected Session createSession(Properties props) {
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

    /**
     * Sender文字列取得
     *
     * @return Sender文字列
     * @throws UnknownHostException
     */
    protected String getDefaultSender() throws UnknownHostException {
        return DEFAULT_MAIL_SENDER + "@" + InetAddress.getLocalHost();
    }
}
