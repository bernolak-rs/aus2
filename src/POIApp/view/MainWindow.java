package POIApp.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MainWindow {
    private JPanel panel1;
    private JTextField latitudeField;
    private JTextField longitudeField;
    private JButton searchButton;
    private JTextField radiusField;
    private JTable poiTable;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton showAllButton;
    private JPanel resultsButtonJPanel;
    private JPanel resultsJPanel;
    private JPanel searchJPanel;
    private JPanel headJPanel;
    private JTextField nameField;
    private JLabel structureJLabel;
    private JLabel titleJLabel;
    private JComboBox comboBox1;
    private JComboBox searchModeComboBox;
    private JLabel searchModeJLabel;
    private JButton importJButton;
    private JButton openJButton;
    private JButton saveJButton;
    private JLabel statusJLabel;

    public MainWindow() {
        String[] columns = {
                "ID",
                "Name",
                "Description",
                "Latitude",
                "Longitude",
                "Address",
                "Created At",
                "Updated At"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0);

        poiTable.setModel(model);
    }
}
