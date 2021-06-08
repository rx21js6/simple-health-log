package jp.nauplius.app.shl.page.record.service;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.deltaspike.jpa.api.transaction.Transactional;

import jp.nauplius.app.shl.common.constants.ShlConstants;
import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.common.model.PhysicalCondition;
import jp.nauplius.app.shl.common.model.PhysicalConditionPK;
import jp.nauplius.app.shl.page.login.bean.LoginInfo;

@Named
@SessionScoped
public class DailyRecordService implements Serializable {
    @Inject
    private transient EntityManager em;

    @Inject
    private LoginInfo loginInfo;

    @PostConstruct
    public void init() {
        System.out.println("DailyRecordService#init() em: " + this.em);
    }

    /**
     * レコード取得
     * @param recordingDate
     * @return
     */
    public PhysicalCondition getRecord(LocalDate recordingDate) {
        String dateText = recordingDate.format(ShlConstants.RECORDING_DATE_FORMATTER);

        PhysicalConditionPK pk = new PhysicalConditionPK();
        pk.setId(this.loginInfo.getUserInfo().getId());
        pk.setRecordingDate(dateText);
        PhysicalCondition record = this.em.find(PhysicalCondition.class, pk);

        if (Objects.isNull(record)) {
            record = new PhysicalCondition();
            record.setId(pk);
        }

        return record;
    }

    /**
     * 登録
     *
     * @param physicalCondition
     */
    @Transactional
    public void register(PhysicalCondition physicalCondition) {
        PhysicalCondition tmpCondition = this.em.find(PhysicalCondition.class, physicalCondition.getId());
        LocalDateTime now = LocalDateTime.now();
        if (Objects.isNull(tmpCondition)) {
            // 新規
            this.em.persist(physicalCondition);
            physicalCondition.setModifiedBy(physicalCondition.getId().getId());
            physicalCondition.setModifiedDate(Timestamp.valueOf(now));
            physicalCondition.setModifiedBy(physicalCondition.getId().getId());
            physicalCondition.setModifiedDate(Timestamp.valueOf(now));
            this.em.merge(physicalCondition);
        } else {
            // 更新
            try {
                BeanUtils.copyProperties(tmpCondition, physicalCondition);
                tmpCondition.setModifiedBy(physicalCondition.getId().getId());
                tmpCondition.setModifiedDate(Timestamp.valueOf(now));
                this.em.merge(tmpCondition);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new SimpleHealthLogException(e);
            }
        }
        this.em.flush();
    }
}
