package jp.nauplius.app.shl.page.record.bean;

import lombok.Getter;
import lombok.Setter;

@Deprecated
public class HealthDetail {
    @Getter
    @Setter
    private int id;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private int value;
}
