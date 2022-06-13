package com.hetacz.ngtotr;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@UtilityClass
class Utils {

    @NotNull Properties propertyLoader(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath, StandardCharsets.UTF_8))) {
            Properties properties = new Properties();
            properties.load(reader);
            return properties;
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Properties file not found at: %s".formatted(filePath));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load a properties file: %s".formatted(filePath));
        }
    }

    /**
     * Check if string is null or blank (empty, or with only whitespaces)
     *
     * @param input string
     * @return true if one of the above is true, otherwise false
     */
    boolean isNullOrBlank(String input) {
        return input == null || input.isBlank();
    }

    /**
     * Check if file with given pathname is a file (ano not directory)
     * @param filename filename or path and filename
     * @return true if ok
     */
    boolean isFile(String filename) {
        return new File(filename).isFile();
    }
}
