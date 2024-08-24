package jp.nauplius.app.shl.common.ui.bean;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@ApplicationScoped
@Named
public class HomeDirectoryInfo {
    public static final String PROP_USER_HOME = "user.home";

    public String getHomeDirectory() {
        return System.getProperty(PROP_USER_HOME);
    }
}
