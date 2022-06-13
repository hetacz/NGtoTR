package com.hetacz.ngtotr;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;

import java.util.Properties;

@Slf4j
final class ConfigLoader {

    private final Properties properties;

    private ConfigLoader() {
        properties = Utils.propertyLoader(Setup.getPath());
    }

    @Contract(pure = true)
    static ConfigLoader getInstance() {
        return Holder.INITIALIZER;
    }

    String getClient() {
        return properties.getProperty("ngtotr.client","");
    }

    String getUser() {
        return properties.getProperty("ngtotr.user","");
    }

    String getPassword() {
        return properties.getProperty("ngtotr.password","");
    }

    boolean isUsingRunCase() {
        String value = properties.getProperty("ngtotr.using.run.case",String.valueOf(Boolean.TRUE));
        return Utils.isNullOrBlank(value) || value // default TRUE if key is empty
                .strip() // if key value is anything than TRUE its FALSE;
                .equalsIgnoreCase(String.valueOf(Boolean.TRUE));
        // default is true, so if its null or blank left retuns true and shortcitcut
        // else, it just check if value equals true ignoring case, if equals then well true is true :D
    }

    private static class Holder {

        private static final ConfigLoader INITIALIZER = new ConfigLoader();
    }
}
