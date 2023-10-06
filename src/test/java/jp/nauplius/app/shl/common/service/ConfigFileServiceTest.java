package jp.nauplius.app.shl.common.service;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import javax.inject.Inject;

import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.CdiRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import jp.nauplius.app.shl.common.producer.TestHomeDirectoryInfoProducer;
import jp.nauplius.app.shl.common.producer.TestLoggerProducer;
import jp.nauplius.app.shl.common.producer.TestMessageBundleProducer;
import jp.nauplius.app.shl.common.ui.bean.HomeDirectoryInfo;

@RunWith(CdiRunner.class)
@ActivatedAlternatives({ TestLoggerProducer.class, TestMessageBundleProducer.class,
        TestHomeDirectoryInfoProducer.class })
public class ConfigFileServiceTest extends AbstractServiceTest {
    @Inject
    private ConfigFileService configFileService;

    @Inject
    private HomeDirectoryInfo mockHomeDirectoryInfo;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * @throws IOException
     */
    @Test
    public void testCreateFile() throws IOException {
        Path path = Paths.get(this.mockHomeDirectoryInfo.getHomeDirectory(), "simple-health-log", "config.yml");
        Files.deleteIfExists(path);

        this.configFileService.createFile();
        assertTrue(Files.isReadable(path));
    }

    @Test
    public void testLoadSalt() throws IOException {
        this.createDummyConfigYml();
        this.configFileService.loadSalt();
    }

    @Test
    public void testGetConfigYmlByteArray() {
        this.createDummyConfigYml();
        this.configFileService.getConfigYmlByteArray();
    }

    private void createDummyConfigYml() {
        Path path = Paths.get(this.mockHomeDirectoryInfo.getHomeDirectory(), "simple-health-log", "config.yml");
        BufferedWriter bw = null;
        try {
            Files.createDirectory(path.getParent());
            bw = new BufferedWriter(new FileWriter(path.toFile()));
            bw.write("{salt: TEST1234}");
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            if (Objects.nonNull(bw)) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
