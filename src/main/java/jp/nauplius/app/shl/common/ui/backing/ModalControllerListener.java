package jp.nauplius.app.shl.common.ui.backing;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;

public interface ModalControllerListener extends Serializable {
    /**
     * Set implemented controller's instance to ModelController like below.
     *
     * <pre>
     * public void initModal() {
     *     // (some routines.. eg. this.commonConfirmModalBean.setVisible(false); )
     *     //
     *     this.commonConfirmModalController.setModalControllerListener(this);
     * }
     * </pre>
     *
     * And call using
     * <p>
     * f:viewAction
     * </p>
     * . Write to xhtml like below.
     *
     * <pre>
     * <f:metadata>
     *      <f:viewAction action="#{someImplementedController.initModal()}" />
     * </f:metadata>
     * </pre>
     *
     */
    public void initModal();

    public String fireAction(ModalController modalAction, String commandTypeName, String buttonName);

    default Method getActionMethod(String methodName) {
        Method[] methods = this.getClass().getDeclaredMethods();
        for (Method m : methods) {
            if (m.getName().equals(methodName)) {
                // this.commonConfirmModalBean.setMessage(m.toString());
                return m;
            }
        }
        return null;
    }

    /**
     * invoke method with args "true".
     *
     * @param methodName
     * @return
     */
    default String dispatchMethod(String methodName) {
        Method[] methods = this.getClass().getDeclaredMethods();
        Method invokedMethod = null;
        for (Method m : methods) {
            if (m.getName().equals(methodName)) {
                invokedMethod = m;
                break;
            }
        }
        if (Objects.isNull(invokedMethod)) {
            throw new SimpleHealthLogException(String.format("method: %s not found.", methodName));
        }
        String res = null;
        try {
            res = (String) invokedMethod.invoke(this, true);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new SimpleHealthLogException(e);
        }

        return res;
    }
}
