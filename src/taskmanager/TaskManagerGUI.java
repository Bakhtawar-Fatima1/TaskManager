package taskmanager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Swing graphical interface for the Task Management System.
 *
 * @author Zahra Saboor, Bakhtawar Fatima
 */
public class TaskManagerGUI extends JFrame {
    private final TaskManager manager = new TaskManager();
    private final TaskStorage storage = new TaskStorage(new File("tasks.txt"));
    private final JLabel countLabel = new JLabel(" ");
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Title", "Description", "Status"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);
    private final JTextField titleField = new JTextField(20);
    private final JTextField descField = new JTextField(20);
    private final JComboBox<TaskStatus> statusBox = new JComboBox<>(TaskStatus.values());
    private final JLabel messageLabel = new JLabel(" ");

    /**
     * Builds the main window.
     */
    public TaskManagerGUI() {
        super("Task Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedIntoForm();
            }
        });
        table.setRowHeight(24);
        table.getColumnModel().getColumn(3).setCellRenderer(new StatusRenderer());
        countLabel.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        add(countLabel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(3, 2, 5, 5));
        form.add(new JLabel("Title:"));
        form.add(titleField);
        form.add(new JLabel("Description:"));
        form.add(descField);
        form.add(new JLabel("Status:"));
        form.add(statusBox);

        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear");
        addBtn.addActionListener(e -> addTask());
        updateBtn.addActionListener(e -> updateTask());
        deleteBtn.addActionListener(e -> deleteTask());
        clearBtn.addActionListener(e -> clearForm());
        JPanel buttons = new JPanel(new FlowLayout());
        buttons.add(addBtn);
        buttons.add(updateBtn);
        buttons.add(deleteBtn);
        buttons.add(clearBtn);

        JPanel south = new JPanel(new BorderLayout(5, 5));
        south.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        south.add(form, BorderLayout.NORTH);
        south.add(buttons, BorderLayout.CENTER);
        south.add(messageLabel, BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);

        try {
            storage.load(manager);
        } catch (IOException ex) {
            error("Could not load saved tasks: " + ex.getMessage());
        }
        refreshTable();
        setSize(700, 520);
        setLocationRelativeTo(null);
    }

    private void addTask() {
        try {
            Task t = manager.addTask(titleField.getText(), descField.getText());
            manager.setStatus(t.getId(), (TaskStatus) statusBox.getSelectedItem());
            showMessage("Task added.");
            persist();
            clearForm();
            refreshTable();
        } catch (IllegalArgumentException ex) {
            error(ex.getMessage());
        }
    }

    private void updateTask() {
        Integer id = selectedId();
        if (id == null) {
            error("Please select a task to update.");
            return;
        }
        try {
            if (!manager.updateTask(id, titleField.getText(), descField.getText())) {
                error("Task not found.");
                refreshTable();
                return;
            }
            manager.setStatus(id, (TaskStatus) statusBox.getSelectedItem());
            showMessage("Task updated.");
            persist();
            refreshTable();
        } catch (IllegalArgumentException ex) {
            error(ex.getMessage());
        }
    }

    private void deleteTask() {
        Integer id = selectedId();
        if (id == null) {
            error("Please select a task to delete.");
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this, "Delete task #" + id + "?",
                "Confirm", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            if (manager.deleteTask(id)) {
                showMessage("Task deleted.");
            persist();
            } else {
                error("Task not found.");
            }
            clearForm();
            refreshTable();
        }
    }

    private Integer selectedId() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return null;
        }
        return (Integer) model.getValueAt(row, 0);
    }

    private void loadSelectedIntoForm() {
        Integer id = selectedId();
        if (id == null) {
            return;
        }
        Task t = manager.findById(id);
        if (t != null) {
            titleField.setText(t.getTitle());
            descField.setText(t.getDescription());
            statusBox.setSelectedItem(t.getStatus());
        }
    }

    private void clearForm() {
        titleField.setText("");
        descField.setText("");
        statusBox.setSelectedItem(TaskStatus.PENDING);
        table.clearSelection();
    }

    private void persist() {
        try {
            storage.save(manager);
        } catch (IOException ex) {
            error("Could not save tasks: " + ex.getMessage());
        }
    }

    private void refreshTable() {
        model.setRowCount(0);
        List<Task> tasks = manager.getAllTasks();
        for (Task t : tasks) {
            model.addRow(new Object[]{t.getId(), t.getTitle(), t.getDescription(), t.getStatus()});
        }
        int pending = 0, progress = 0, done = 0;
        for (Task t : tasks) {
            if (t.getStatus() == TaskStatus.PENDING) pending++;
            else if (t.getStatus() == TaskStatus.IN_PROGRESS) progress++;
            else done++;
        }
        countLabel.setText("Total: " + tasks.size() + "   |   Pending: " + pending
                + "   |   In Progress: " + progress + "   |   Completed: " + done);
        if (tasks.isEmpty()) {
            messageLabel.setText("No tasks available. Add a task to get started.");
        }
    }

    private void showMessage(String msg) {
        messageLabel.setForeground(new Color(0, 120, 0));
        messageLabel.setText(msg);
    }

    private void error(String msg) {
        messageLabel.setForeground(Color.RED);
        messageLabel.setText(msg);
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Colours the status cell depending on the task status.
     */
    private static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable tbl, Object value,
                boolean selected, boolean focus, int row, int col) {
            Component c = super.getTableCellRendererComponent(tbl, value, selected, focus, row, col);
            if (!selected) {
                if (value == TaskStatus.COMPLETED) {
                    c.setBackground(new Color(198, 239, 206));
                } else if (value == TaskStatus.IN_PROGRESS) {
                    c.setBackground(new Color(255, 235, 156));
                } else {
                    c.setBackground(new Color(230, 230, 230));
                }
                c.setForeground(Color.BLACK);
            }
            return c;
        }
    }

    /**
     * Application entry point.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // keep default look and feel
        }
        SwingUtilities.invokeLater(() -> new TaskManagerGUI().setVisible(true));
    }
}
