import taskmanager.*;
import java.io.File;

/**
 * Simple console test runner for the 7 OEL test cases.
 *
 * @author Zahra Saboor, Bakhtawar Fatima
 */
public class TaskManagerTest {
    private static int passed = 0, total = 0;

    private static void check(String name, String expected, boolean ok, String actual) {
        total++;
        if (ok) passed++;
        System.out.printf("TC%d | %-32s | Expected: %-28s | Actual: %-34s | %s%n",
                total, name, expected, actual, ok ? "PASS" : "FAIL");
    }

    /**
     * Runs all test cases.
     *
     * @param args not used
     */
    public static void main(String[] args) throws Exception {
        TaskManager m = new TaskManager();

        Task t = m.addTask("Buy milk", "2 litres");
        check("Add a valid task", "Task is added", m.getAllTasks().size() == 1, "List size = " + m.getAllTasks().size());

        String msg = "";
        try { m.addTask("   ", "x"); } catch (IllegalArgumentException e) { msg = e.getMessage(); }
        check("Add task with empty title", "Validation message shown", !msg.isEmpty() && m.getAllTasks().size() == 1, "\"" + msg + "\"");

        m.addTask("Study Java", "Chapter 5");
        check("View all tasks", "All tasks displayed", m.getAllTasks().size() == 2, m.getAllTasks().toString());

        check("View an empty list", "Appropriate message shown", new TaskManager().getAllTasks().isEmpty(), "Empty list -> 'No tasks available'");

        boolean upd = m.updateTask(t.getId(), "Buy bread", "1 loaf");
        check("Update an existing task", "Details updated", upd && m.findById(t.getId()).getTitle().equals("Buy bread"), "Title = " + m.findById(t.getId()).getTitle());

        boolean updMissing = m.updateTask(999, "X", "Y");
        check("Update a non-existing task", "Handled gracefully", !updMissing, "Returned false, no crash");

        boolean del = m.deleteTask(t.getId());
        check("Delete an existing task", "Task removed", del && m.findById(t.getId()) == null, "List size = " + m.getAllTasks().size());

        m.setStatus(2, TaskStatus.IN_PROGRESS);
        m.setStatus(2, TaskStatus.COMPLETED);
        check("Set status (extra)", "Status = Completed", m.findById(2).getStatus() == TaskStatus.COMPLETED, "Status = " + m.findById(2).getStatus());

        File f = File.createTempFile("tasks", ".txt");
        TaskManager a = new TaskManager();
        a.addTask("Line\\one", "tab\there");
        a.setStatus(1, TaskStatus.COMPLETED);
        new TaskStorage(f).save(a);
        TaskManager b = new TaskManager();
        new TaskStorage(f).load(b);
        boolean same = b.getAllTasks().size() == 1 && b.findById(1).getTitle().equals("Line\\one")
                && b.findById(1).getDescription().equals("tab\there")
                && b.findById(1).getStatus() == TaskStatus.COMPLETED;
        check("Save and load from file (extra)", "Same tasks after reload", same, "Reloaded " + b.getAllTasks().size() + " task(s)");
        f.delete();

        System.out.println("\nResult: " + passed + "/" + total + " tests passed");
    }
}