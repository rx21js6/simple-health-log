package jp.nauplius.app.shl.page.initial.backing;

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;

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
