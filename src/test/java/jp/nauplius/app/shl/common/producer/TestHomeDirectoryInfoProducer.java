package jp.nauplius.app.shl.common.producer;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;

import org.mockito.Mockito;

import jp.nauplius.app.shl.common.ui.bean.HomeDirectoryInfo;
import lombok.Getter;

@Named
@Alternative
@ApplicationScoped
public class TestHomeDirectoryInfoProducer {
    @Getter
    private Path tmpDummyHomeDirPath;

    public TestHomeDirectoryInfoProducer() throws IOException {
        this.tmpDummyHomeDirPath = Files.createTempDirectory("TestHomeDirectoryInfoProducer_tmpDummyHomeDirPath");
    }

    @Produces
    public HomeDirectoryInfo getHomeDirectoryInfoMock() throws IOException {
        HomeDirectoryInfo mock = Mockito.mock(HomeDirectoryInfo.class);
        when(mock.getHomeDirectory()).thenReturn(this.tmpDummyHomeDirPath.toAbsolutePath().toString());
        return mock;
    }
}
