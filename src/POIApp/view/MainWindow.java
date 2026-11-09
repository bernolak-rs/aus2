package POIApp.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 *
 */
public class MainWindow {
    private JPanel mainPanel;
    private JTextField minLatitudeField;
    private JTextField minLongitudeField;
    private JButton searchButton;
    private JTable poiTable;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton showAllButton;
    private JPanel resultsButtonJPanel;
    private JPanel resultsJPanel;
    private JPanel searchJPanel;
    private JPanel headJPanel;
    private JLabel structureJLabel;
    private JLabel titleJLabel;
    private JComboBox structComboBox;
    private JComboBox searchModeComboBox;
    private JLabel searchModeJLabel;
    private JButton openJButton;
    private JButton saveJButton;
    private JLabel statusJLabel;
    private JTextField nameField;
    private JTextField radiusField;
    private JTextField maxLatitudeField;
    private JTextField maxLongitudeField;

    private DefaultTableModel resultsTable;

    public MainWindow() {
        String[] tableColumns = {
                "ID",
                "Name",
                "Latitude",
                "Longitude",
                "Created At",
                "Updated At",
                "Address",
                "Description",
        };

        this.resultsTable = new DefaultTableModel(tableColumns, 0);

        poiTable.setModel(this.resultsTable);
        poiTable.setDefaultEditor(Object.class, null);

        String[] structOpts = {"KDTree", "QuadTree", "QuadKDTree"};
        structComboBox.setModel(new DefaultComboBoxModel<>(structOpts));

        String[] searchOpts = {"Single position", "Radius", "Name in rectangle"};
        searchModeComboBox.setModel(new DefaultComboBoxModel<>(searchOpts));
    }

    /**
     * Getters
     */
    public JPanel getMainPanel() {
        return mainPanel;
    }

    public String getMinLatitude() {
        return minLatitudeField.getText().trim();
    }

    public String getMinLongitude() {
        return minLongitudeField.getText().trim();
    }

    public JButton getSearchButton() {
        return searchButton;
    }

    public JTable getPoiTable() {
        return poiTable;
    }

    public JButton getAddButton() {
        return addButton;
    }

    public JButton getEditButton() {
        return editButton;
    }

    public JButton getDeleteButton() {
        return deleteButton;
    }

    public JButton getShowAllButton() {
        return showAllButton;
    }

    public JComboBox<?> getStructComboBox() {
        return structComboBox;
    }

    public JComboBox<?> getSearchModeComboBox() {
        return searchModeComboBox;
    }

    public JLabel getSearchModeJLabel() {
        return searchModeJLabel;
    }

    public JButton getOpenJButton() {
        return openJButton;
    }

    public JButton getSaveJButton() {
        return saveJButton;
    }

    public JLabel getStatusJLabel() {
        return statusJLabel;
    }

    public String getName() {
        return nameField.getText().trim();
    }

    public String getRadius() {
        return radiusField.getText().trim();
    }

    public String getMaxLatitude() {
        return maxLatitudeField.getText().trim();
    }

    public String getMaxLongitude() {
        return maxLongitudeField.getText().trim();
    }

    public int getSelectedRow() {
        return this.poiTable.getSelectedRow();
    }

    public void clearTable() {
        this.resultsTable.setRowCount(0);
    }

    public void addTableRow(Object[] row) {
        this.resultsTable.addRow(row);
    }

    public void setStatusJLabel(String status) {
        this.statusJLabel.setText(status);
    }

    /**
     * Shows Error message
     * @param message specif error that occurred on operation
     */
    public void showError(String message) {
        JOptionPane.showMessageDialog(this.mainPanel, message);
    }

    /**
     * Blocks unwanted fields based on search mode
     * @param maxLat
     * @param maxLon
     * @param radius
     * @param name
     */
    public void setSearchFields(boolean maxLat, boolean maxLon, boolean radius, boolean name) {
        this.maxLatitudeField.setEnabled(maxLat);
        this.maxLongitudeField.setEnabled(maxLon);
        this.radiusField.setEnabled(radius);
        this.nameField.setEnabled(name);
    }

}
