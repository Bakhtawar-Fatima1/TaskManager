package taskmanager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

/**
 * Saves tasks to, and loads tasks from, a plain text file so that
 * they are not lost when the application is closed.
 * Each line is: title TAB description TAB status.
 *
 * @author Zahra Saboor, Bakhtawar Fatima
 */
public class TaskStorage {
    private final File file;

    /**
     * Creates a storage object for the given file.
     *
     * @param file the file used to store tasks
     */
    public TaskStorage(File file) {
        this.file = file;
    }

    /**
     * Writes all tasks of the manager to the file.
     *
     * @param manager the manager whose tasks are saved
     * @throws IOException if the file cannot be written
     */
    public void save(TaskManager manager) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            for (Task t : manager.getAllTasks()) {
                w.write(escape(t.getTitle()) + "\t" + escape(t.getDescription())
                        + "\t" + t.getStatus().name());
                w.newLine();
            }
        }
    }

    /**
     * Loads tasks from the file into the given manager.
     * If the file does not exist nothing is loaded. Broken lines are skipped.
     *
     * @param manager the manager that receives the loaded tasks
     * @throws IOException if the file exists but cannot be read
     */
    public void load(TaskManager manager) throws IOException {
        if (!file.exists()) {
            return;
        }
        try (BufferedReader r = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] p = line.split("\t", -1);
                if (p.length != 3) {
                    continue;
                }
                try {
                    Task t = manager.addTask(unescape(p[0]), unescape(p[1]));
                    manager.setStatus(t.getId(), TaskStatus.valueOf(p[2]));
                } catch (IllegalArgumentException ex) {
                    // skip invalid line
                }
            }
        }
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\t", "\\t").replace("\n", "\\n");
    }

    private static String unescape(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char n = s.charAt(++i);
                sb.append(n == 't' ? '\t' : n == 'n' ? '\n' : n);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
