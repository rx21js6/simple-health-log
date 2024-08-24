package jp.nauplius.app.shl.page.initial.backing;

import java.io.Serializable;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

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
