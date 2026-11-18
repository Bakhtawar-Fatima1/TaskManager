package taskmanager;

/**
 * Possible states of a {@link Task}.
 *
 * @author Zahra Saboor, Bakhtawar Fatima
 */
public enum TaskStatus {
    /** Task has not been started. */
    PENDING("Pending"),
    /** Task is currently being worked on. */
    IN_PROGRESS("In Progress"),
    /** Task is finished. */
    COMPLETED("Completed");

    private final String label;

    TaskStatus(String label) {
        this.label = label;
    }

    /**
     * Returns the human readable name of the status.
     *
     * @return display label such as "In Progress"
     */
    @Override
    public String toString() {
        return label;
    }
}
