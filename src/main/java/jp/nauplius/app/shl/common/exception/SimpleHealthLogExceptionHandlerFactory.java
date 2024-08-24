package jp.nauplius.app.shl.common.exception;

import jakarta.faces.context.ExceptionHandler;
import jakarta.faces.context.ExceptionHandlerFactory;

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
