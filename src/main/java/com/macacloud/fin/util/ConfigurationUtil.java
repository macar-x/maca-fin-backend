package com.macacloud.fin.util;

import io.quarkus.runtime.Startup;
import io.quarkus.runtime.annotations.StaticInitSafe;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Application Configurations Holder.
 *
 * @author Emmett
 * @since 2025/01/09
 */
@StaticInitSafe
public class ConfigurationUtil {

    // Using ConcurrentHashMap for thread safety
    private static final Map<String, String> configMap = new ConcurrentHashMap<>();


    // Check if a key exists
    public static boolean hasKey(String key) {
        return configMap.containsKey(key);
    }

    // Get a specific config value
    public static String getConfigValue(String key) {
        return configMap.get(key);
    }

    // Get all config values
    public static Map<String, String> getAllConfig() {
        // Return a copy to prevent modification
        return new HashMap<>(configMap);
    }

    @ApplicationScoped
    @Startup
    public static class ConfigHolder {
        ConfigHolder() {
            loadAllProperties();
        }

        private void loadAllProperties() {
            Config config = ConfigProvider.getConfig();

            // Iterate through all properties and store them in the map
            config.getPropertyNames().forEach(key -> {
                try {
                    String value = config.getValue(key, String.class);
                    configMap.put(key, value);
                } catch (Exception e) {
                    // Handle cases where the value cannot be converted to String
                    System.err.println("Could not load property: " + key + " - " + e.getMessage());
                }
            });
        }
    }

    // Optional: Add a method to reload configuration if needed
    public static void reloadConfiguration() {
        new ConfigHolder().loadAllProperties();
    }
}
