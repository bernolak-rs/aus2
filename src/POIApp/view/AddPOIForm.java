package POIApp.view;

import javax.swing.*;

/**
 * Form used to add or Edit POIs
 */
public class AddPOIForm extends JDialog {
    private JLabel titleJLabel;
    private JTextField enterPOINameTextField;
    private JTextField latJTextField;
    private JPanel mainJPanel;
    private JPanel nameJPanel;
    private JPanel descJPanel;
    private JPanel coordJPanel;
    private JLabel latJLabel;
    private JTextField longJTextField;
    private JLabel longJLabel;
    private JButton clearButton;
    private JButton addButton;
    private JTextField addressTextField;
    private JLabel addressJLabel;
    private JLabel descriptionJLabel;
    private JTextArea descriptionJTextArea;
    private JPanel addressJPanel;
    private JPanel buttonJPanel;
    private JButton generateButton;

    public AddPOIForm(JFrame owner) {
        super(owner);

        setContentPane(mainJPanel);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(owner);
    }

    /**
     * AI DOKUMENTACIA 4.6
     * 
     * Getters
     */
    public String getPoiName() {
        return enterPOINameTextField.getText().trim();
    }

    public String getLatitude() {
        return latJTextField.getText().trim();
    }

    public String getLongitude() {
        return longJTextField.getText().trim();
    }

    public String getAddress() {
        return addressTextField.getText().trim();
    }

    public String getDescription() {
        return descriptionJTextArea.getText().trim();
    }

    public JButton getAddButton() {
        return addButton;
    }

    public JButton getClearButton() {
        return clearButton;
    }

    public JButton getGenerateButton() {
        return generateButton;
    }

    /**
     * Clears Text Field in Form
     */
    public void clearFields() {
        enterPOINameTextField.setText("");
        latJTextField.setText("");
        longJTextField.setText("");
        addressTextField.setText("");
        descriptionJTextArea.setText("");
    }

    /**
     * Sets old POI values for editing
     * @param name
     * @param description
     * @param latitude
     * @param longitude
     * @param address
     */
    public void setValues(String name, String description,
                      double latitude, double longitude, String address) {
        enterPOINameTextField.setText(name);
        descriptionJTextArea.setText(description);
        latJTextField.setText(Double.toString(latitude));
        longJTextField.setText(Double.toString(longitude));
        addressTextField.setText(address);
    }

    /**
     * Changes title and fields to name to match Edit mode
     */
    public void setEditMode() {
        setTitle("Edit POI");
        titleJLabel.setText("Edit POI");
        addButton.setText("Save changes");
        generateButton.setEnabled(false);
    }
}
