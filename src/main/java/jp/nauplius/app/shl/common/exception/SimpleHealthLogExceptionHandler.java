package jp.nauplius.app.shl.common.exception;

import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

public class SimpleHealthLogExceptionHandler extends ExceptionHandlerWrapper {

    private ExceptionHandler exceptionHandler;

    public SimpleHealthLogExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public ExceptionHandler getWrapped() {
        return exceptionHandler;
    }

    @Override
    public void handle() throws FacesException {
        final Iterator<ExceptionQueuedEvent> queue = getUnhandledExceptionQueuedEvents().iterator();

        while (queue.hasNext()) {
            ExceptionQueuedEvent item = queue.next();
            ExceptionQueuedEventContext exceptionQueuedEventContext = (ExceptionQueuedEventContext) item.getSource();

            try {
                Throwable throwable = exceptionQueuedEventContext.getException();
                System.err.println("Exception: " + throwable.getMessage());

                FacesContext context = FacesContext.getCurrentInstance();
                Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
                NavigationHandler navigationHandler = context.getApplication().getNavigationHandler();

                requestMap.put("error-class", throwable.getClass().getName());
                requestMap.put("error-message", throwable.getMessage());
                requestMap.put("error-stack", throwable.getStackTrace());

                context.getExternalContext().getFlash().setKeepMessages(true);

                String message = ResourceBundle.getBundle("i18n.messages").getString("common.msg.sessionExpired");
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, message, null));
                navigationHandler.handleNavigation(context, null,
                        "/contents/record/recordInput.xhtml?faces-redirect=true");
                context.renderResponse();

            } finally {
                queue.remove();
            }
        }
    }
}
