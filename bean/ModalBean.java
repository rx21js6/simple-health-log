package jp.nauplius.app.ttv1.common.ui.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class ModalBean implements Serializable {
	protected boolean visible;
	protected String commandTypeName;
	protected String title;
	protected List<String> messages;

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getCommandTypeName() {
		return commandTypeName;
	}

	public void setCommandTypeName(String commandTypeName) {
		this.commandTypeName = commandTypeName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}

	public void setMessage(String message) {
		this.messages = new ArrayList<>();
		this.messages.clear();
		this.messages.add(message);
	}

}
