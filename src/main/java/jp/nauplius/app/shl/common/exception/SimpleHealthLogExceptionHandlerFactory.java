package jp.nauplius.app.shl.common.exception;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

public class SimpleHealthLogExceptionHandlerFactory extends ExceptionHandlerFactory {
    private ExceptionHandlerFactory exceptionHandlerFactory;

    public SimpleHealthLogExceptionHandlerFactory() {
    }

    public SimpleHealthLogExceptionHandlerFactory(ExceptionHandlerFactory exceptionHandlerFactory) {
        this.exceptionHandlerFactory = exceptionHandlerFactory;
    }

    @Override
    public ExceptionHandler getExceptionHandler() {
        return new SimpleHealthLogExceptionHandler(exceptionHandlerFactory.getExceptionHandler());
    }

}
