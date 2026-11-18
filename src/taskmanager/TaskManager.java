package taskmanager;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds the list of tasks and provides add, view, update and delete operations.
 *
 * @author Zahra Saboor, Bakhtawar Fatima
 */
public class TaskManager {
    private final List<Task> tasks = new ArrayList<>();
    private int nextId = 1;

    /**
     * Adds a new task.
     *
     * @param title       task title (must not be empty)
     * @param description task description
     * @return the newly created task
     * @throws IllegalArgumentException if the title is empty
     */
    public Task addTask(String title, String description) {
        Task task = new Task(nextId, title, description);
        tasks.add(task);
        nextId++;
        return task;
    }

    /**
     * Returns all tasks.
     *
     * @return a copy of the task list (empty if there are no tasks)
     */
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    /**
     * Finds a task by id.
     *
     * @param id the task id
     * @return the task, or {@code null} if it does not exist
     */
    public Task findById(int id) {
        for (Task t : tasks) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    /**
     * Updates the title and description of a task.
     *
     * @param id          id of the task to update
     * @param title       new title (must not be empty)
     * @param description new description
     * @return {@code true} if updated, {@code false} if no task has that id
     * @throws IllegalArgumentException if the new title is empty
     */
    public boolean updateTask(int id, String title, String description) {
        Task task = findById(id);
        if (task == null) {
            return false;
        }
        task.setTitle(title);
        task.setDescription(description);
        return true;
    }

    /**
     * Changes the status of a task.
     *
     * @param id     id of the task
     * @param status the new status
     * @return {@code true} if updated, {@code false} if no task has that id
     */
    public boolean setStatus(int id, TaskStatus status) {
        Task task = findById(id);
        if (task == null) {
            return false;
        }
        task.setStatus(status);
        return true;
    }

    /**
     * Deletes a task.
     *
     * @param id id of the task to delete
     * @return {@code true} if deleted, {@code false} if no task has that id
     */
    public boolean deleteTask(int id) {
        Task task = findById(id);
        return task != null && tasks.remove(task);
    }
}
