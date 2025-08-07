package se233.chapter3.model;

import java.nio.file.Paths;

public class FileFreq {
    private String name;
    private String path;
    private Integer freq;

    public FileFreq(String name, String path, Integer freq) {
        this.name = name;
        this.path = path;
        this.freq = freq;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public Integer getFreq() {
        return freq;
    }

    // Get just the filename without full path
    public String getFileName() {
        return Paths.get(path).getFileName().toString();
    }

    @Override
    public String toString() {
        // Display only filename and frequency
        return String.format("{%s:%d}", getFileName(), freq);
    }
}