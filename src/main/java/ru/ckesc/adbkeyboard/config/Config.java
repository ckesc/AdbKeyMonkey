package ru.ckesc.adbkeyboard.config;

import javafx.scene.input.KeyCode;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>Configuration of app.</p>
 * <p>Must be POJO-object because reads from json by {@link com.google.gson.Gson}</p>
 */
@SuppressWarnings("UnusedDeclaration")
public class Config {
    int reconnectPeriod;
    Map<KeyCode, String> keyMap;


    public Config(int reconnectPeriod, Map<KeyCode, String> keyMap) {
        this.reconnectPeriod = reconnectPeriod;
        this.keyMap = keyMap;
    }

    private Config() {
        keyMap = new LinkedHashMap<>();
    }

    public int getReconnectPeriod() {
        return reconnectPeriod;
    }

    public Map<KeyCode, String> getKeyMap() {
        return keyMap;
    }

    /**
     * Builder for Config
     */
    public static class Builder {
        Config config;

        public Builder() {
            this.config = new Config();
        }

        public Builder setReconnectPeriod(int period) {
            config.reconnectPeriod = period;
            return this;
        }

        public Builder addKeyMapItem(KeyCode pcKey, String androidKey) {
            config.keyMap.put(pcKey, androidKey);
            return this;
        }

        public Config build() {
            return config;
        }

    }
}
