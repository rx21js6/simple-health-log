package jp.nauplius.app.shl.common.ui.backing;

import java.io.Serializable;
import java.lang.reflect.Method;

public interface ModalControllerListener extends Serializable {
    /**
     * Set implemented controller's instance to ModelController like below.
     * <pre>
     * public void initModal() {
     *     // (some routines.. eg. this.commonConfirmModalBean.setVisible(false); )
     *     //
     *     this.commonConfirmModalController.setModalControllerListener(this);
     * }
     * </pre>
     *
     * And call using <p>f:viewAction</p>. Write to xhtml like below.
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
}
