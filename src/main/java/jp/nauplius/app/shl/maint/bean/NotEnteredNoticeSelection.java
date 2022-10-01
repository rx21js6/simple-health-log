package jp.nauplius.app.shl.maint.bean;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotEnteredNoticeSelection implements Serializable {
    private int id;
    private String typeKey;
    private String messageId;
    private boolean checked;
}
