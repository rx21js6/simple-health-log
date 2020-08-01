package jp.nauplius.app.shl.page.initial.backing;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class InitialTwoStepVerificationController implements Serializable {
    @Getter
    @Setter
    private String loginId;

    @Getter
    @Setter
    private String pin;

    public void init() {

    }

    public String register() {
        return null;
    }
}
