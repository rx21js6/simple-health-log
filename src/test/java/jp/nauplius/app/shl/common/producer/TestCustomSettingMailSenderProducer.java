package jp.nauplius.app.shl.common.producer;

import static org.mockito.Mockito.when;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;

import org.mockito.Mockito;

import jp.nauplius.app.shl.maint.service.CustomSettingMailSender;

@Named
@Alternative
@ApplicationScoped
public class TestCustomSettingMailSenderProducer {
    @Produces
    public CustomSettingMailSender getCustomSettingMailSenderMock() {
        CustomSettingMailSender mock = Mockito.mock(CustomSettingMailSender.class);
        when(mock.isActive()).thenReturn(false);
        return mock;
    }
}
