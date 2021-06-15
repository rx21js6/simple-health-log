package jp.nauplius.app.ttv1.common.ui.bean;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

@Named
@ViewScoped
public class CommonConfirmModalBean extends ModalBean {

	private String okButtonValue;
	private String cancelButtonValue;

	public CommonConfirmModalBean() {
		this.okButtonValue = "OK";
		this.cancelButtonValue = "CANCEL";
		this.visible = false;
	}
	public String getOkButtonValue() {
		return okButtonValue;
	}

	public void setOkButtonValue(String okButtonValue) {
		this.okButtonValue = okButtonValue;
	}

	public String getCancelButtonValue() {
		return cancelButtonValue;
	}

	public void setCancelButtonValue(String cancelButtonValue) {
		this.cancelButtonValue = cancelButtonValue;
	}
}
