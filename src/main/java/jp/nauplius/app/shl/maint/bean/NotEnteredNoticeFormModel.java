package jp.nauplius.app.shl.maint.bean;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import lombok.Data;

@Named
@ViewScoped
@Data
public class NotEnteredNoticeFormModel implements Serializable {
    List<NotEnteredNoticeSelection> notEnteredNoticeSelections;

    public NotEnteredNoticeFormModel() {
        this.notEnteredNoticeSelections = Collections.emptyList();
    }
}
