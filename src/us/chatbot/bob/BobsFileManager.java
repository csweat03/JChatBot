package us.chatbot.bob;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BobsFileManager {

    private final String currentWorkingDir = System.getProperty("user.dir") + "\\src\\us\\chatbot\\bob\\bobsDictionary.txt";
    private Path path = Paths.get(currentWorkingDir);
    private List<String> lines;

    public boolean isPresent(String key) {
        readAll();
        for (String line : lines)
            if (line.startsWith(key + ": "))
                return true;
        return false;
    }

    public String[] readAll() {
        try {
            return (lines = Files.readAllLines(path, StandardCharsets.UTF_8)).toArray(new String[]{});
        } catch (IOException ignored) {
        }
        return new String[]{""};
    }

    public String getString(String key) {
        for (String line : readAll())
            if (line.startsWith(key + ": ") && !line.startsWith(key + ": a: "))
                return line.substring(key.length() + 2);
        return "";
    }

    public String[] getArray(String key) {
        for (String line : readAll())
            if (line.startsWith(key + ": a:"))
                return new ArrayList<>(Arrays.asList(line.substring(key.length() + 4).split(",,,"))).toArray(new String[0]);
        return new String[]{""};
    }

    public void addOrChangePersonContent(String name, int emotion, int relationship) {
        try {
            if (isPersonInDatabase(name)) {
                changePersonContent(name, emotion, relationship);
            } else {
                addPersonContent(name, emotion, relationship);
            }
        } catch (IOException ignored) {
        }
    }

    public void addValueToField(String key, String value) {
        try {
            addValueToField(key, value, new Object());
        } catch (Exception ignore) {
        }
    }

    public void addField(String key, String value) {
        try {
            addField(key, value, new Object());
        } catch (IOException ignored) {
        }
    }

    private void addValueToField(String key, String value, Object... params) throws IOException {
        int position = -1;

        for (String line : readAll())
            if (line.startsWith(key + ": "))
                position = Arrays.asList(readAll()).indexOf(line);

        String temp = lines.get(position) + ",,," + value;
        lines.remove(position);
        lines.add(position, temp);
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    private void addField(String key, String value, Object... params) throws IOException {
        readAll();

        lines.add(key + ": " + value);
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    private void addPersonContent(String name, int emotion, int relationship, Object... params) throws IOException {
        readAll();

        lines.add("(((((" + name + ":" + emotion + ":" + relationship + ")))))");
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    private void changePersonContent(String name, int emotion, int relationship, Object... params) throws IOException {
        for (String line : readAll()) {
            if (line.startsWith("(((((" + name.toLowerCase() + ":")) {
                lines.remove(line);
            }
        }
        lines.add("(((((" + name + ":" + emotion + ":" + relationship + ")))))");
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    public int getPersonEmotion(String name) {
        for (String line : readAll()) {
            if (line.startsWith("(((((" + name.toLowerCase() + ":")) {
                return Integer.parseInt(line.substring(name.length() + 6, name.length() + 7));
            }
        }
        return 1;
    }

    public int getPersonRelationship(String name) {
        for (String line : readAll()) {
            if (line.startsWith("(((((" + name.toLowerCase() + ":")) {
                return Integer.parseInt(line.substring(name.length() + 8, name.length() + 9));
            }
        }
        return 1;
    }

    public boolean isPersonInDatabase(String name) {
        for (String line : readAll())
            if (line.startsWith("(((((" + name.toLowerCase() + ":"))
                return true;
        return false;
    }

}