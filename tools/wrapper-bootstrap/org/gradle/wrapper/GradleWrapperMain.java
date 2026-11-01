package org.gradle.wrapper;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;
import java.util.zip.*;

/**
 * Small transparent Gradle 8.11.1 bootstrapping implementation.
 * The verified Gradle distribution is downloaded from services.gradle.org.
 * For production repositories, replace the bootstrap JAR with the official
 * Gradle Wrapper (run `gradle wrapper --gradle-version 8.11.1`).
 */
public final class GradleWrapperMain {
    private static final String VERSION = "8.11.1";
    private static final String URL = "https://services.gradle.org/distributions/gradle-8.11.1-bin.zip";
    private static final String SHA256 = "f397b287023acdba1e9f6fc5ea72d22dd63669d59ed4a289a29b1a76eee151c6";

    public static void main(String[] args) throws Exception {
        Path dir = Paths.get(System.getProperty("user.home"), ".gradle", "wrapper", "dists", "btl-" + VERSION);
        Path home = dir.resolve("gradle-" + VERSION);
        boolean windows = System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win");
        Path executable = home.resolve("bin").resolve(windows ? "gradle.bat" : "gradle");
        if (!Files.isRegularFile(executable)) {
            Files.createDirectories(dir);
            Path archive = dir.resolve("gradle-" + VERSION + "-bin.zip");
            if (!Files.exists(archive) || !sha256(archive).equalsIgnoreCase(SHA256)) {
                Path partial = dir.resolve("download.tmp");
                System.out.println("Downloading Gradle " + VERSION + " from " + URL);
                try (InputStream in = URI.create(URL).toURL().openStream()) {
                    Files.copy(in, partial, StandardCopyOption.REPLACE_EXISTING);
                    if (!SHA256.equalsIgnoreCase(sha256(partial))) {
                        Files.deleteIfExists(partial);
                        throw new SecurityException("Gradle distribution SHA-256 did not match official checksum.");
                    }
                    Files.move(partial, archive, StandardCopyOption.REPLACE_EXISTING);
                }
            }
            System.out.println("Extracting Gradle " + VERSION + "...");
            try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(archive))) {
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    Path out = dir.resolve(entry.getName()).normalize();
                    if (!out.startsWith(dir)) throw new SecurityException("Invalid ZIP entry: " + entry.getName());
                    if (entry.isDirectory()) Files.createDirectories(out);
                    else {
                        Files.createDirectories(out.getParent());
                        Files.copy(zis, out, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
        }
        if (!Files.isRegularFile(executable)) throw new FileNotFoundException("Gradle launcher not found: " + executable);
        if (!windows) executable.toFile().setExecutable(true);
        List<String> command = new ArrayList<>();
        command.add(executable.toString());
        command.addAll(Arrays.asList(args));
        Process process = new ProcessBuilder(command).inheritIO().start();
        System.exit(process.waitFor());
    }

    private static String sha256(Path file) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        try (InputStream in = Files.newInputStream(file)) {
            byte[] buf = new byte[16384];
            for (int count; (count = in.read(buf)) > 0; ) md.update(buf, 0, count);
        }
        StringBuilder out = new StringBuilder();
        for (byte b : md.digest()) out.append(String.format(Locale.ROOT, "%02x", b & 0xFF));
        return out.toString();
    }
}
