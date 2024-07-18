package jp.nauplius.app.shl.common.ui.bean;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@XmlRootElement(name = "mailSetting")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class MailSenderBean {
    @XmlElement(name = "mail.active")
    private boolean active;
    @XmlElement(name = "mail.smtp.host")
    private String host;
    @XmlElement(name = "mail.smtp.port")
    private int port;
    @XmlElement(name = "mail.smtp.auth")
    private boolean auth;
    @XmlElement(name = "mail.smtp.connectiontimeout")
    private int connectiontimeout;
    @XmlElement(name = "mail.smtp.timeout")
    private int timeout;
    @XmlElement(name = "mail.smtp.userId")
    private String userId;
    @XmlElement(name = "mail.smtp.password")
    private String password;
}
