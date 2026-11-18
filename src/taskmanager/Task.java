package taskmanager;

/**
 * Represents a single task with an id, title, description and status.
 *
 * @author Zahra Saboor, Bakhtawar Fatima
 */
public class Task {
    private final int id;
    private String title;
    private String description;
    private TaskStatus status;

    /**
     * Creates a new task with status {@link TaskStatus#PENDING}.
     *
     * @param id          unique identifier of the task
     * @param title       task title (must not be empty)
     * @param description task description (may be empty)
     * @throws IllegalArgumentException if the title is null or blank
     */
    public Task(int id, String title, String description) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty.");
        }
        this.id = id;
        this.title = title.trim();
        this.description = description == null ? "" : description.trim();
        this.status = TaskStatus.PENDING;
    }

    /**
     * Gets the task id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets a new title.
     *
     * @param title new title (must not be empty)
     * @throws IllegalArgumentException if the title is null or blank
     */
    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty.");
        }
        this.title = title.trim();
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets a new description.
     *
     * @param description the new description (null is treated as empty)
     */
    public void setDescription(String description) {
        this.description = description == null ? "" : description.trim();
    }

    /**
     * Gets the status.
     *
     * @return the current status
     */
    public TaskStatus getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the new status (must not be null)
     * @throws IllegalArgumentException if status is null
     */
    public void setStatus(TaskStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null.");
        }
        this.status = status;
    }

    /**
     * Returns a one line text form of the task.
     *
     * @return text such as "#1 Buy milk [Pending]"
     */
    @Override
    public String toString() {
        return "#" + id + " " + title + " [" + status + "]";
    }
}
