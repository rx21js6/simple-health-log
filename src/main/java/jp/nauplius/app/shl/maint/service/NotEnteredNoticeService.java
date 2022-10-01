package jp.nauplius.app.shl.maint.service;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.TypedQuery;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.deltaspike.jpa.api.transaction.Transactional;
import org.slf4j.Logger;

import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.common.model.NotEnteredNotice;
import jp.nauplius.app.shl.common.service.AbstractService;
import jp.nauplius.app.shl.maint.backing.NotEnteredNoticeFormModel;
import jp.nauplius.app.shl.maint.bean.NotEnteredNoticeSelection;
import jp.nauplius.app.shl.page.login.bean.LoginInfo;

@Named
@SessionScoped
public class NotEnteredNoticeService extends AbstractService {
    @Inject
    private Logger logger;

    @Inject
    private LoginInfo loginInfo;

    @Inject
    private NotEnteredNoticeFormModel notEnteredNoticeFormModel;

    public void init() {
        this.logger.info("init() start");

        List<NotEnteredNoticeSelection> selections = new ArrayList<NotEnteredNoticeSelection>();
        this.notEnteredNoticeFormModel.setNotEnteredNoticeSelections(selections);

        TypedQuery<NotEnteredNotice> query = this.entityManager
                .createQuery("SELECT n FROM NotEnteredNotice n ORDER BY n.id", NotEnteredNotice.class);

        List<NotEnteredNotice> notEnteredNotices = query.getResultList();

        for (NotEnteredNotice notEnteredNotice : notEnteredNotices) {
            NotEnteredNoticeSelection selection = new NotEnteredNoticeSelection();
            try {
                BeanUtils.copyProperties(selection, notEnteredNotice);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new SimpleHealthLogException(e);
            }
            selections.add(selection);
        }

        this.logger.info("init() complete");
    }

    @Transactional
    public void update() {
        this.logger.info("update() start");

        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        for (NotEnteredNoticeSelection selection : this.notEnteredNoticeFormModel.getNotEnteredNoticeSelections()) {

            this.logger.debug(String.format("selection: typeKey: %s / checked: %b", selection.getTypeKey(), selection.isChecked()));

            TypedQuery<NotEnteredNotice> query = this.entityManager
                    .createQuery("SELECT n FROM NotEnteredNotice n where n.typeKey = :typeKey", NotEnteredNotice.class);
            query.setParameter("typeKey", selection.getTypeKey());

            NotEnteredNotice notEnteredNotice = query.getSingleResult();
            if (Objects.isNull(notEnteredNotice)) {
                throw new SimpleHealthLogException(String.format("typeKey: %s not found.", selection.getTypeKey()));
            }

            notEnteredNotice.setChecked(selection.isChecked());
            notEnteredNotice.setModifiedBy(this.loginInfo.getUserInfo().getId());
            notEnteredNotice.setModifiedDate(timestamp);
            this.entityManager.merge(notEnteredNotice);
            this.entityManager.flush();

        }

        this.logger.info("update() complete");
    }
}
