package jp.nauplius.app.shl.common.producer;

import java.io.Serializable;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.context.FacesContext;

@ApplicationScoped
public class FacesContextProducer implements Serializable {
    public FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }
}
