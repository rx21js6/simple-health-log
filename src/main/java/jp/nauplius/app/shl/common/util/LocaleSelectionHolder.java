package jp.nauplius.app.shl.common.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.model.SelectItem;
import jakarta.inject.Named;

import lombok.Getter;

@Named
@ApplicationScoped
public class LocaleSelectionHolder implements Serializable {
    @Getter
    private List<SelectItem> selectLocales;

    public LocaleSelectionHolder() {
        this.selectLocales = new ArrayList<SelectItem>();
        this.selectLocales.add(new SelectItem("en", "English"));
        this.selectLocales.add(new SelectItem("ja", "Japanese"));
    }
}
