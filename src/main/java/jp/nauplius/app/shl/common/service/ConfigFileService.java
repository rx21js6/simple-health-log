package jp.nauplius.app.shl.common.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
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

import jp.nauplius.app.shl.common.util.PasswordUtil;
import jp.nauplius.app.shl.common.util.PasswordUtil.PasswordStrength;

@Named
@ApplicationScoped
public class ConfigFileService implements Serializable {
    private final String CONFIG_BASE_DIR = "/simple-health-log/";
    private final String CONFIG_FILE_NAME = "config.yml";
    private final String KEY_SALT = "salt";

    @Inject
    private Logger logger;

    @Inject
    private ResourceBundle messageBundle;

    public void createFile() throws IOException {
        String homeDir = System.getProperty("user.home");
        logger.info("Creating config file in : " + homeDir);

        if (StringUtils.isEmpty(homeDir) || StringUtils.equals(homeDir, "/nonexistent")) {
            throw new IOException(this.messageBundle.getString("common.msg.homeDirectoryNotDeclared"));
        }

        File dir = new File(System.getProperty("user.home") + CONFIG_BASE_DIR);
        if (!dir.exists()) {
            boolean result = dir.mkdir();
            if (!result) {
                String messageBase = this.messageBundle.getString("common.msg.mkdirFailed");
                MessageFormat format = new MessageFormat(messageBase);
                String message = format.format(new String[] { dir.getAbsolutePath() });

                throw new IOException(message);
            }
        }

        File configFile = new File(System.getProperty("user.home") + CONFIG_BASE_DIR + CONFIG_FILE_NAME);
        if (configFile.exists()) {
            logger.info(configFile.getAbsolutePath() + ": exists");
            return;
        }

        String ramdomString = PasswordUtil.createRandomText(PasswordStrength.STRONG);
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put(KEY_SALT, ramdomString);

        Yaml yaml = new Yaml();
        FileWriter writer = null;
        try {
            writer = new FileWriter(configFile);
            yaml.dump(dataMap, writer);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (Objects.nonNull(writer)) {
                writer.close();
            }
        }

        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.startsWith("windows")) {
            logger.info("Running on windows? Permission settings skipped.");
        } else {
            // permission
            Set<PosixFilePermission> perms = new HashSet<>();
            perms.add(PosixFilePermission.OWNER_READ);
            perms.add(PosixFilePermission.OWNER_WRITE);
            Files.setPosixFilePermissions(configFile.toPath(), perms);

        }

        logger.info(configFile.getAbsolutePath() + ": created.");
    }

    public synchronized String loadSalt() throws IOException {
        Yaml yaml = new Yaml();

        File configFile = new File(System.getProperty("user.home") + CONFIG_BASE_DIR + CONFIG_FILE_NAME);
        FileReader reader = null;
        String salt = null;
        try {
            reader = new FileReader(configFile);
            Map<String, String> data = yaml.load(reader);
            salt = data.get(KEY_SALT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (Objects.nonNull(reader)) {
                reader.close();
            }
        }
        return salt;
    }
}
