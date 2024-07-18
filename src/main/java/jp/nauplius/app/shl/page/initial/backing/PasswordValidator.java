package jp.nauplius.app.shl.page.initial.backing;

import java.io.Serializable;
import java.util.ResourceBundle;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@FacesValidator("passwordValidator")
public class PasswordValidator implements Validator, Serializable {
    @Inject
    private transient ResourceBundle messageBundle;

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        UIInput passwordInput = (UIInput) context.getViewRoot().findComponent("password");
        String password = (String) passwordInput.getSubmittedValue();

        UIInput passwordReenterInput = (UIInput) context.getViewRoot().findComponent("passwordReenter");
        String passwordReenter = (String) passwordReenterInput.getSubmittedValue();

        if (password.equals(passwordReenter)) {
            FacesMessage message = new FacesMessage(this.messageBundle.getString("common.msg.passwordUnmatch"));
            throw new ValidatorException(message);
        }
    }

}
