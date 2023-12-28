package jp.nauplius.app.shl.common.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;

import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.common.ui.bean.HomeDirectoryInfo;
import jp.nauplius.app.shl.common.util.PasswordUtil;
import jp.nauplius.app.shl.common.util.PasswordUtil.PasswordStrength;

@Named
@ApplicationScoped
public class ConfigFileService implements Serializable {
    public static final String CONFIG_BASE_DIR = "simple-health-log";
    public static final String CONFIG_FILE_NAME = "config.yml";
    private static final String KEY_SALT = "salt";
    private static final String PROP_OS_NAME = "os.name";

    @Inject
    private Logger logger;

    @Inject
    private ResourceBundle messageBundle;

    @Inject
    private HomeDirectoryInfo homeDirectoryInfo;

    public void createFile() throws IOException {
        this.logger.info("#createFile() begin");

        String homeDir = this.homeDirectoryInfo.getHomeDirectory();

        if (StringUtils.isEmpty(homeDir) || StringUtils.equals(homeDir, "/nonexistent")) {
            String message = this.messageBundle.getString("common.msg.homeDirectoryNotDeclared");
            this.logger.error(message);
            throw new IOException(message);
        }

        File dir = Paths.get(homeDir, CONFIG_BASE_DIR).toFile();
        if (!dir.exists()) {
            boolean result = dir.mkdir();
            if (!result) {
                String messageBase = this.messageBundle.getString("common.msg.mkdirFailed");
                MessageFormat format = new MessageFormat(messageBase);
                String message = format.format(new String[]{dir.getAbsolutePath()});

                this.logger.error(message);
                throw new IOException(message);
            }
        }

        File configFile = Paths.get(homeDir, CONFIG_BASE_DIR, CONFIG_FILE_NAME).toFile();
        if (configFile.exists()) {
            this.logger.info(String.format("Config file already exists. : %s ", configFile.getAbsolutePath()));
            return;
        }

        this.logger.info("Creating config file in: " + homeDir);
        String ramdomString = PasswordUtil.createRandomText(PasswordStrength.STRONG);
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put(KEY_SALT, ramdomString);

        Yaml yaml = new Yaml();
        FileWriter writer = null;
        try {
            writer = new FileWriter(configFile);
            yaml.dump(dataMap, writer);
        } catch (IOException e) {
            this.logger.error(e.getMessage());
            throw e;
        } finally {
            if (Objects.nonNull(writer)) {
                writer.close();
            }
        }

        String osName = System.getProperty(PROP_OS_NAME).toLowerCase();
        if (osName.startsWith("windows")) {
            this.logger.info("Running on windows? Permission settings skipped.");
        } else {
            // permission
            Set<PosixFilePermission> perms = new HashSet<>();
            perms.add(PosixFilePermission.OWNER_READ);
            perms.add(PosixFilePermission.OWNER_WRITE);
            Files.setPosixFilePermissions(configFile.toPath(), perms);

        }

        this.logger.info(configFile.getAbsolutePath() + ": created.");
        this.logger.info("#createFile() complete");
    }

    public synchronized String loadSalt() throws IOException {
        Yaml yaml = new Yaml();

        File configFile = Paths.get(this.homeDirectoryInfo.getHomeDirectory(), CONFIG_BASE_DIR, CONFIG_FILE_NAME)
                .toFile();
        FileReader reader = null;
        String salt = null;
        try {
            reader = new FileReader(configFile);
            Map<String, String> data = yaml.load(reader);
            salt = data.get(KEY_SALT);
        } catch (FileNotFoundException e) {
            this.logger.error(e.getMessage());
            throw e;
        } finally {
            if (Objects.nonNull(reader)) {
                reader.close();
            }
        }
        return salt;
    }

    /**
     * config.ymlのバイト列を返す。
     *
     * @return
     */
    public byte[] getConfigYmlByteArray() {
        File configFile = Paths.get(this.homeDirectoryInfo.getHomeDirectory(), CONFIG_BASE_DIR, CONFIG_FILE_NAME)
                .toFile();

        if (!Files.isReadable(configFile.toPath())) {
            this.logger.error("config.yml is not found or can not readable.");
            throw new SimpleHealthLogException(
                    this.messageBundle.getString("contents.maint.settings.adminSetting.msg.configYmlCannotReadable"));
        }

        try {
            byte[] byteArray = Files.readAllBytes(configFile.toPath());
            return byteArray;
        } catch (IOException e) {
            throw new SimpleHealthLogException(e);
        }
    }
}
