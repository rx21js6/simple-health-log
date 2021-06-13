package jp.nauplius.app.shl.page.initial.backing;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;

@Named
@FacesValidator("passwordValidator")
public class PasswordValidator implements Validator, Serializable {
    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        UIInput passwordInput = (UIInput) context.getViewRoot().findComponent("password");
        String password = (String) passwordInput.getSubmittedValue();

        UIInput passwordReEnterInput = (UIInput) context.getViewRoot().findComponent("passwordReEnter");
        String passwordReEnter = (String) passwordReEnterInput.getSubmittedValue();

        if  (password.equals(passwordReEnter)) {
            FacesMessage message = new FacesMessage("ぱすわーどふいっち");
            throw new ValidatorException(message);
        }
    }

}
