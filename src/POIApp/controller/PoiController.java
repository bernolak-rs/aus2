package POIApp.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import POIApp.model.Poi;
import POIApp.model.PoiManager;
import POIApp.model.PoiManager.TreeType;
import POIApp.view.GeneratePOIForm;
import POIApp.view.MainWindow;
import POIApp.view.AddPOIForm;

import javax.swing.*;

/**
 * Controller managing communication between model(PoiManager) and view(Windows and Forms)
 */
public class PoiController {
    private final MainWindow view;
    private final PoiManager poiManager;
    private List<Poi> displayedPois;

    /**
     * Creates PoiController
     * @param view
     * @param poiManager
     */
    public PoiController(MainWindow view, PoiManager poiManager) {
        this.view = view;
        this.poiManager = poiManager;

        this.registerListeners();

        this.changeFieldsAccess();
        this.displayedPois = new ArrayList<>();

        this.showAll();
    }

    /**
     * Registers listeners for buttons and comboboxes
     */
    private void registerListeners() {
        this.view.getSearchButton().addActionListener(e -> searchPois());
        this.view.getShowAllButton().addActionListener(e -> showAll());
        this.view.getDeleteButton().addActionListener(e -> deleteSelected());
        this.view.getAddButton().addActionListener(e -> openAddForm());
        this.view.getEditButton().addActionListener(e -> openEditForm());
        this.view.getStructComboBox().addActionListener(e -> changeTree());
        this.view.getOpenJButton().addActionListener(e -> openCsv());
        this.view.getSaveJButton().addActionListener(e -> saveCsv());
        this.view.getSearchModeComboBox().addActionListener(e -> changeFieldsAccess());
    }

    /**
     * Disables fields that are not supposed to be used for different search modes
     */
    private void changeFieldsAccess() {
        String selected = (String) view.getSearchModeComboBox().getSelectedItem();

        switch (selected) {
            case "Single position":
                this.view.setSearchFields(false, false, false, false);
                break;

            case "Radius":
                this.view.setSearchFields(false, false, true, false);
                break;

            case "Name in rectangle":
                this.view.setSearchFields(true, true, false, true);
                break;
        }
    }

    /**
     * AI DOKUMENTACIA 4.5
     * 
     * Searches and displays results for each mode
     */
    private void searchPois() {
        List<Poi> results = new LinkedList<>();
        try {
            String selected = (String) view.getSearchModeComboBox().getSelectedItem();

            double minLat = Double.parseDouble(view.getMinLatitude());
            double minLon = Double.parseDouble(view.getMinLongitude());


            if (selected != null) {
                switch (selected) {
                    case "Single position":
                        results = this.poiManager.findAtPosition(minLat, minLon);
                        break;

                    case "Radius":
                        double radius = Double.parseDouble(view.getRadius());
                        results = this.poiManager.findInRadius(minLat, minLon, radius);

                        break;

                    case "Name in rectangle":
                        double maxLat = Double.parseDouble(view.getMaxLatitude());
                        double maxLon = Double.parseDouble(view.getMaxLongitude());

                        results = this.poiManager.findByName(minLat, maxLat, minLon, maxLon, this.view.getName());
                        break;

                    default:
                        throw new IllegalArgumentException("Invalid search mode");
                }
            }
            displayResults(results);
        } catch (NumberFormatException e) {
            this.view.showError("Enter valid coordinates");
        } catch (IllegalArgumentException e) {
            this.view.showError(e.getMessage());
        }
    }

    /**
     * Displays results in GUI
     * @param results
     */
    public void displayResults(List<Poi> results) {
        this.view.clearTable();

        this.displayedPois = results;

        for (var poi : results) {
            view.addTableRow(new Object[] {
                    poi.getId(),
                    poi.getName(),
                    poi.getLatitude(),
                    poi.getLongitude(),
                    poi.getCreatedTime(),
                    poi.getUpdatedTime(),
                    poi.getAddress(),
                    poi.getDescription(),
            });
        }
        this.view.setStatusJLabel("POI count: " + results.size());
    }

    /**
     * Displays all POIs in tree to GUI
     */
    public void showAll() {
        try {
            displayResults(this.poiManager.getAll());
        } catch (Exception e) {
            this.view.showError(e.getMessage());
        }
    }

    /**
     * Deletes selected POI from POI manager
     */
    public void deleteSelected() {
        int row = this.view.getSelectedRow();

        if (row < 0) {
            this.view.showError("Select POI to delete first");
            return;
        }

        Poi poi = this.displayedPois.get(row);

        try {
            boolean deleted = this.poiManager.deletePoi(poi);
            if (!deleted) {
                this.view.showError("Delete wasn't successful");
            }
            this.displayedPois.remove(row);
            displayResults(this.displayedPois);
        } catch (Exception e) {
            view.showError(e.getMessage());
        }
    }

    /**
     * AI DOKUMENTACIA 4.7
     * 
     * Opens form to add new POI
     */
    public void openAddForm() {
        JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(view.getMainPanel());
        AddPOIForm form = new AddPOIForm(owner);

        form.setTitle("Add POI");
        form.setModal(true);

        form.getClearButton().addActionListener(e -> form.clearFields());
        form.getGenerateButton().addActionListener(e -> this.openGenerateForm(form));

        form.getAddButton().addActionListener(e -> {
            try {
                String name = form.getPoiName();
                if (name.isEmpty()) {
                    throw new IllegalArgumentException("Enter POI name");
                }

                double lat = Double.parseDouble(form.getLatitude());
                double lon = Double.parseDouble(form.getLongitude());

                if (!Double.isFinite(lat) || lat < -90 || lat > 90) {
                    throw new IllegalArgumentException(
                            "Latitude must be between -90 and 90");
                }

                if (!Double.isFinite(lon) || lon < -180 || lon > 180) {
                    throw new IllegalArgumentException(
                            "Longitude must be between -180 and 180");
                }

                boolean added = poiManager.addPoi(
                        name,
                        form.getDescription(),
                        lat,
                        lon,
                        form.getAddress()
                );

                if (!added) {
                    JOptionPane.showMessageDialog(form, "Could not add POI");
                    return;
                }

                form.dispose();
                showAll();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(form, "Enter valid coordinates");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(form, ex.getMessage());
            }
        });

        form.setVisible(true);
    }

    /**
     * AI DOKUMENTACIA 4.9
     * 
     * Opens generate form to generate random POIs
     * @param owner
     */
    private void openGenerateForm(AddPOIForm owner) {
        GeneratePOIForm form = new GeneratePOIForm(owner);

        form.setTitle("Generate POI");
        form.setModal(true);

        form.getClearButton().addActionListener(e -> form.clearFields());

        form.getGenerateButton().addActionListener(e -> {
            try {
                this.poiManager.generatePois(
                        Long.parseLong(form.getSeedTextField()),
                        Integer.parseInt(form.getCountJTextField()),
                        Double.parseDouble(form.getMinLatJTextField()),
                        Double.parseDouble(form.getMaxLatJTextField()),
                        Double.parseDouble(form.getMinLonJTextField()),
                        Double.parseDouble(form.getMaxLonJTextField())

                );
                form.dispose();
                owner.dispose();

            } catch (IllegalArgumentException error) {
                this.view.showError(error.getMessage());
            }
        });
        form.setVisible(true);
    }

    /**
     * AI DOKUMENTACIA 4.8
     * 
     * Opens edit form to edit existing POI
     */
    public void openEditForm() {
        int row = view.getSelectedRow();

        if (row < 0) {
            view.showError("Select POI to edit first");
            return;
        }

        int modelRow = view.getPoiTable().convertRowIndexToModel(row);
        Poi poi = displayedPois.get(modelRow);

        JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(view.getMainPanel());
        AddPOIForm form = new AddPOIForm(owner);

        form.setEditMode();
        form.setValues(
                poi.getName(),
                poi.getDescription(),
                poi.getLatitude(),
                poi.getLongitude(),
                poi.getAddress()
        );
        form.setModal(true);

        form.getClearButton().addActionListener(e -> form.clearFields());

        form.getAddButton().addActionListener(e -> {
            try {
                String name = form.getPoiName();
                if (name.isEmpty()) {
                    throw new IllegalArgumentException("Enter POI name");
                }

                double lat = Double.parseDouble(form.getLatitude());
                double lon = Double.parseDouble(form.getLongitude());

                if (!Double.isFinite(lat) || lat < -90 || lat > 90) {
                    throw new IllegalArgumentException(
                            "Latitude must be between -90 and 90");
                }

                if (!Double.isFinite(lon) || lon < -180 || lon > 180) {
                    throw new IllegalArgumentException(
                            "Longitude must be between -180 and 180");
                }

                boolean updated = poiManager.updatePoi(
                        poi,
                        name,
                        form.getDescription(),
                        lat,
                        lon,
                        form.getAddress()
                );

                if (!updated) {
                    JOptionPane.showMessageDialog(form, "Could not update POI");
                    return;
                }

                form.dispose();
                showAll();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(form, "Enter valid coordinates");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(form, ex.getMessage());
            }
        });

        form.setVisible(true);
    }

    /**
     * Changes tree structure used in POI manager
     */
    private void changeTree() {
        String tree = (String)this.view.getStructComboBox().getSelectedItem();

        TreeType newTree;
        try {
            switch (tree) {
                case "KDTree":
                    newTree = TreeType.KDTree;
                    break;
                case "QuadTree":
                    newTree = TreeType.QuadTree;
                    break;
                case "QuadKDTree":
                    newTree = TreeType.QuadKDTree;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown tree structure");
            }
            poiManager.switchTree(newTree);
        } catch (IllegalArgumentException e) {
            view.showError(e.getMessage());
        }
        this.showAll();
    }

    /**
     * Opens file chooser to select CSV file to load into a tree
     */
    public void openCsv() {
        JFileChooser chooser = new JFileChooser();

        if (chooser.showOpenDialog(view.getMainPanel()) == JFileChooser.APPROVE_OPTION) {
            try {
                poiManager.loadFromCsv(chooser.getSelectedFile().getAbsolutePath());
                showAll();
            } catch (Exception e) {
                view.showError(e.getMessage());
            }
        }
    }

    /**
     * Opens file chooser to save current POI into a CSV file
     */
    public void saveCsv() {
        JFileChooser chooser = new JFileChooser();

        if (chooser.showSaveDialog(view.getMainPanel()) == JFileChooser.APPROVE_OPTION) {
            try {
                poiManager.saveToCsv(chooser.getSelectedFile().getAbsolutePath());
            } catch (Exception e) {
                view.showError(e.getMessage());
            }
        }
    }
}
