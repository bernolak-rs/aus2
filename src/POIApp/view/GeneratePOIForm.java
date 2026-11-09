package POIApp.view;

import javax.swing.*;
import java.awt.*;

/**
 * Form used to generate POIs based on user input
 */
public class GeneratePOIForm extends JDialog {
    private JPanel minCoordJPanel;
    private JTextField minLatJTextField;
    private JTextField minLonJTextField;
    private JPanel nameJPanel;
    private JTextField seedTextField;
    private JPanel buttonJPanel;
    private JButton clearButton;
    private JButton generateButton;
    private JPanel maxCoordJPanel;
    private JTextField maxLatJTextField;
    private JTextField maxLonJTextField;
    private JTextField countJTextField;

    private JPanel mainJPanel;

    public GeneratePOIForm(Window owner) {
        super(owner);

        System.out.println("mainJpanel: " + mainJPanel);
        setContentPane(mainJPanel);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(owner);
    }

    public String getMinLatJTextField() {
        return minLatJTextField.getText().trim();
    }

    public String getMinLonJTextField() {
        return minLonJTextField.getText().trim();
    }

    public String getSeedTextField() {
        return seedTextField.getText().trim();
    }

    public JButton getClearButton() {
        return clearButton;
    }

    public JButton getGenerateButton() {
        return generateButton;
    }

    public String getMaxLatJTextField() {
        return maxLatJTextField.getText().trim();
    }

    public String getMaxLonJTextField() {
        return maxLonJTextField.getText().trim();
    }

    public String getCountJTextField() {
        return countJTextField.getText().trim();
    }

    /**
     * Clear text fields
     */
    public void clearFields() {
        this.seedTextField.setText("");
        this.countJTextField.setText("");
        this.minLatJTextField.setText("");
        this.maxLatJTextField.setText("");
        this.maxLonJTextField.setText("");
        this.minLonJTextField.setText("");
    }
}
