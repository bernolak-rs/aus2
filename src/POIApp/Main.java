package POIApp;

import javax.swing.*;

import POIApp.controller.PoiController;
import POIApp.model.PoiManager;
import POIApp.model.PoiManager.TreeType;
import POIApp.view.MainWindow;

/**
 * Initialization of POI application
 */
public class Main {
    /**
     * Asks user to select initial tree structure and opens app
     */
    public static void main() {
        SwingUtilities.invokeLater(() -> {
            MainWindow view = new MainWindow();

            TreeType selectedTree = (TreeType) JOptionPane.showInputDialog(
                    null,
                    "Select initial tree:",
                    "POI Manager",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    TreeType.values(),
                    TreeType.KDTree
            );

            if (selectedTree == null) {
                return;
            }

            PoiManager manager = new PoiManager(selectedTree);

            JFrame frame = new JFrame("POI Manager");
            frame.setContentPane(view.getMainPanel());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1100, 700);
            frame.setLocationRelativeTo(null);

            new PoiController(view, manager);

            frame.setVisible(true);
        });
    }
}
