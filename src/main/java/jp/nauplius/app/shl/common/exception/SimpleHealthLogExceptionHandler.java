package jp.nauplius.app.shl.common.exception;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;
import javax.servlet.http.HttpSession;

import jp.nauplius.app.shl.common.service.LocaleService;

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

                FacesContext facesContext = FacesContext.getCurrentInstance();
                ExternalContext externalContext = facesContext.getExternalContext();
                Map<String, Object> requestMap = externalContext.getRequestMap();
                NavigationHandler navigationHandler = facesContext.getApplication().getNavigationHandler();

                requestMap.put("error-class", throwable.getClass().getName());
                requestMap.put("error-message", throwable.getMessage());
                requestMap.put("error-stack", throwable.getStackTrace());

                externalContext.getFlash().setKeepMessages(true);

                HttpSession httpSession = (HttpSession) externalContext.getSession(true);
                Locale locale = (Locale) httpSession.getAttribute(LocaleService.SESSION_KEY);

                String message = ResourceBundle.getBundle("i18n.messages", locale).getString("common.msg.sessionExpired");
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, message, null));
                navigationHandler.handleNavigation(facesContext, null,
                        "/contents/record/recordInput.xhtml?faces-redirect=true");
                facesContext.renderResponse();

            } finally {
                queue.remove();
            }
        }
    }
}
