package kneadtoknow.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileStorage {

    private File requireFile(String filePath) throws IOException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IOException("File path cannot be null or empty.");
        }
        return new File(filePath.trim());
    }

    private void ensureParentDir(File file) {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }

    public void saveLines(String filePath, List<String> lines) throws IOException {
        if (lines == null) {
            return;
        }
        File file = requireFile(filePath);
        ensureParentDir(file);

        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(file, StandardCharsets.UTF_8))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        }
    }

    public void appendLine(String filePath, String line) throws IOException {
        if (line == null) {
            return;
        }
        File file = requireFile(filePath);
        ensureParentDir(file);

        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(file, StandardCharsets.UTF_8, true))) {
            bw.write(line);
            bw.newLine();
        }
    }

    public List<String> readLines(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();
        File file = requireFile(filePath);
        if (!file.exists()) {
            return lines;
        }

        try (BufferedReader br = new BufferedReader(
                new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isBlank()) {
                    lines.add(line);
                }
            }
        }
        return lines;
    }

    public void exportReport(String filePath, String title, String body) throws IOException {
        File file = requireFile(filePath);
        ensureParentDir(file);

        try (PrintWriter out = new PrintWriter(
                new FileWriter(file, StandardCharsets.UTF_8))) {
            out.println(title);
            out.println("==========================================");
            out.print(body != null ? body : "");
        }
    }
}