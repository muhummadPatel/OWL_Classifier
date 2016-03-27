package com.oe;
    /* This is commented out in the mean time because you need to specify
	   the class path and other package stuff when compiling it alone
	   from the command line */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import org.semanticweb.owlapi.profiles.OWLProfileViolation;

import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Ontology Engineering: Profile Checker
 * User-interface class
 * March, 2016
 *
 * @author Aashiq Parker
 * @author Brian Mc George
 * @author Muhummad Patel⁠⁠⁠⁠⁠
 */
public class UI extends JFrame {
    private JFrame frame;
    private JTextArea explanationArea;
    private JLabel speciesLabel;
    private JTabbedPane expressivityPane;
    private JTabbedPane violationsPane;

    private String[] profiles;
    private JCheckBox[] checkBoxes; //The check box profiles

    private JCheckBoxMenuItem toggleIriButton;

    private float fileNameAreaPercentage = 0.5f; //Percentage of the width of the screen that the file name text area occupies
    private float fontSize = 12.0f; //Approximation of the font size, to calculate the width of text components

    //For the toggling, can't reverse a regex so we need this
    private String[] fullExpAxioms;
    private String[] fullVioAxioms;
    private String[] cleanExpAxioms;
    private String[] cleanVioAxioms;

    //Heading
    private JLabel checkboxHeading;
    private JLabel profileHeading;
    private JLabel expHeading;

    private JTextArea[] expAreas;
    private JTextArea[] vioAreas;
    private boolean printResultsToTerminal;

    private int letterWidth = 10; //Total length of the letter + spaces used to for expressivity tabs

    OWLOntology mainOntology;
    Set<OWLOntology> ontologies;

    //Constructor
    public UI() {
        initialize();
    }

    private void initialize() {
        // Default to printing results to terminal
        printResultsToTerminal = false;

        try {
            // Set System L&F
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        //The main frame
        frame = new JFrame("OntoCheck");
        frame.setSize(1000, 900);
        frame.setResizable(true);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        frame.addComponentListener(new Window()); //Window is defined further down. Mainly for doing stuff when the window is resized.

        //Creating the menu bar
        JMenuBar bar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu viewMenu = new JMenu("View");
        JMenuItem loadItem = new JMenuItem("Load Ontology");
        JMenuItem helpItem = new JMenuItem("Help");
        toggleIriButton = new JCheckBoxMenuItem("Show full IRI");
        toggleIriButton.addActionListener(new ToggleIRIClickListener());
        loadItem.addActionListener(new OpenFileClickListener());
        helpItem.addActionListener(new HelpClickListener());
        fileMenu.add(loadItem);
        fileMenu.add(helpItem);
        viewMenu.add(toggleIriButton);
        bar.add(fileMenu);
        bar.add(viewMenu);
        frame.setJMenuBar(bar);

      /* The rest is to create the individual components and place them using gridbaglayout*/
        GridBagConstraints gbc = new GridBagConstraints();

        List<String> profileList = new ArrayList<>();
        profileList.addAll(OWL2ProfileChecker.PROFILE_NAMES);
        profileList.addAll(OWL1ProfileChecker.PROFILE_NAMES);
        profiles = new String[profileList.size()];
        profiles = profileList.toArray(profiles);
        checkBoxes = new JCheckBox[profiles.length];

        checkboxHeading = new JLabel("<html><b><u>OWL Profiles</u></b></html>");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 10, 0, 0); //Padding
        gbc.gridwidth = 4;
        gbc.gridheight = 1;
        gbc.weighty = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        frame.add(checkboxHeading, gbc);

        gbc.insets = new Insets(0, 6, 5, 0); //Padding
        for (int i = 0; i < profiles.length; ++i) {
            checkBoxes[i] = new JCheckBox(profiles[i]);
            checkBoxes[i].setModel(new DefaultButtonModel() //Only way to make check boxes read only. SetEnabled(false) greys out the component
            {
                @Override
                public void setSelected(boolean b) {
                    super.setSelected(b); //This allows me to programatically change the check boxes, but not the user (I don't really know why)
                }
            });
            gbc.gridx = i;
            gbc.gridy = 1;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            //		gbc.weighty = 0; gbc.weightx = 0;

            //Remove the checkBoxes MouseListener so that it doesnt seem as if you can click it
            MouseListener[] ml = (MouseListener[]) checkBoxes[i].getListeners(MouseListener.class);

            for (int j = 0; j < ml.length; ++j)
                checkBoxes[i].removeMouseListener(ml[j]);

            frame.add(checkBoxes[i], gbc);
        }

		//Explanation area heading
		JLabel explanationHeading = new JLabel("<html><b><u>Expressivity Information</u></b></html>");
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weighty = 0;
		gbc.insets = new Insets(0, 10, 2, 0); //Padding different for label
		gbc.gridheight = 1;
		gbc.gridwidth = 5;
		frame.add(explanationHeading, gbc);

        //The explanation area before the expressivity information
        explanationArea = new JTextArea("");
        explanationArea.setEditable(false);
        JScrollPane scrollableArea = new JScrollPane(explanationArea);//, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 9;
        gbc.gridheight = 3;
		gbc.insets = new Insets(0, 10, 8, 0);
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        frame.add(scrollableArea, gbc);

        expHeading = new JLabel("<html><b><u>Expressivity Axioms</u></b></html>");
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 10,2, 0); //Padding different for label
        gbc.gridheight = 1;
        frame.add(expHeading, gbc);

        gbc.insets = new Insets(0, 10, 8, 0); //Reset padding

        expressivityPane = new JTabbedPane();
        JTextArea tempField1 = new JTextArea();
        //  expressivityPane.setSize(500,400);
        expressivityPane.addTab("          ", tempField1);
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 9;
        gbc.gridheight = 4;
        gbc.weighty = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        frame.add(expressivityPane, gbc);

        profileHeading = new JLabel("<html><b><u>Profile Violations</u></b></html>");
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 10, 2, 0);
        gbc.gridheight = 1;
        frame.add(profileHeading, gbc);

        gbc.insets = new Insets(0, 10, 8, 0);

        violationsPane = new JTabbedPane();
        JTextArea tempField2 = new JTextArea();
        //violationsPane.setSize(500,400);
        violationsPane.addTab("          ", tempField2);
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.gridwidth = 9;
        gbc.gridheight = 4;
        gbc.weighty = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        frame.add(violationsPane, gbc);

        frame.setVisible(true);
    }

    //Called when an owl file is loaded to populate the tabbed pane with the letters and their axioms
    public void populateExpressivityPane(HashMap<String, ArrayList<OWLAxiom>> axiomClassifications) {
        //Remove the current content in the expressivity tabbed pane
        while (expressivityPane.getTabCount() > 0) //Remove current tabs
            expressivityPane.remove(0);

        int size = axiomClassifications.keySet().size();
        expAreas = new JTextArea[size];
        fullExpAxioms = new String[size];
        cleanExpAxioms = new String[size];

        int counter = 1; //Counter of axioms for each letter
        int index = 0;
        for (String letter : axiomClassifications.keySet()) {
            JTextArea area = new JTextArea(); //The list of axioms for this particular letter
            area.setEditable(false);
            String fullAxioms = "";
            String cleanedAxioms = "";
            for (OWLAxiom axiom : axiomClassifications.get(letter)) {
                if(axiom.toString().contains("#")){
                    cleanedAxioms += counter + " - " + axiom.toString().replaceAll("http:[^#]*/", "") + "\n"; //Not the most elegant
                    // regex but it works
                    area.append(counter + " - " + axiom.toString().replaceAll("http:[^#]*/", "") + "\n"); //Populate this area with the
                    // list of axioms
                }else{
                    cleanedAxioms += counter + " - " + axiom.toString() + "\n";
                    area.append(counter + " - " + axiom.toString() + "\n"); //Populate this area with the
                    // list of axioms
                }
                fullAxioms += counter + " - " + axiom.toString() + "\n";
                ++counter;
            }

            //Add the final list for the letter as a tab in the tabbed pane. We want it to be scrollable.
            expAreas[index] = area;
            expAreas[index].setCaretPosition(0);
            if(printResultsToTerminal) {
                System.out.println("=== " + letter + " ===");
                System.out.println(area.getText());
            }
            fullExpAxioms[index] = fullAxioms;
            cleanExpAxioms[index] = cleanedAxioms;
            JScrollPane scrollableArea = new JScrollPane(area);
            String formatString = String.format(letter + "%1$" + letterWidth + "s", ""); //Add the padding
            expressivityPane.addTab(formatString, scrollableArea);
            counter = 1;
            ++index;
        }
    }

    //To populate the violations pane
    public void populateViolationsPane(HashMap<String, OWLProfileReport> ontologyProfileReports, HashMap<String, OWL1ProfileReport>
            owl1ontologyProfileReports) {
        while (violationsPane.getTabCount() > 0) //Remove current tabs
            violationsPane.remove(0);

        int counter = 1; //Counter of axioms for each letter

        vioAreas = new JTextArea[profiles.length];
        fullVioAxioms = new String[profiles.length];
        cleanVioAxioms = new String[profiles.length];
        int index = 0;
        for (String profileName : OWL2ProfileChecker.PROFILE_NAMES) {
            if(printResultsToTerminal) {
                System.out.println("Is in " + profileName + "? " + ontologyProfileReports.get(profileName).isInProfile());
            }
            if (ontologyProfileReports.get(profileName).isInProfile()) //If there are no violations
            {
                checkBoxes[Arrays.asList(profiles).indexOf(profileName)].setSelected(true); //It falls under the respective profile
                ++index;
                continue;
            }
            String fullAxioms = "";
            String cleanedAxioms = "";
            JTextArea area = new JTextArea(); //The list of axioms for this particular profile
            area.setEditable(false);

            for (OWLProfileViolation violation : ontologyProfileReports.get(profileName).getViolations()) {
                if(violation.toString().contains("#")){
                    cleanedAxioms += counter + " - " + violation.toString().replaceAll("http:[^#]*/", "") + "\n"; //Not the most elegant
                    // regex but it works
                    area.append(counter + " - " + violation.toString().replaceAll("http:[^#]*/", "") + "\n"); //Populate this area with
                    // the list of axioms
                }else{
                    cleanedAxioms += counter + " - " + violation.toString() + "\n";
                    area.append(counter + " - " + violation.toString() + "\n"); //Populate this area with
                    // the list of axioms
                }
                fullAxioms += counter + " - " + violation.toString() + "\n";
                ++counter;
            }
            //Add the final list for the profile as a tab in the tabbed pane. We want it to be scrollable.
            vioAreas[index] = area;
            vioAreas[index].setCaretPosition(0);
            if(printResultsToTerminal) {
                System.out.println(area.getText());
            }
            fullVioAxioms[index] = fullAxioms;
            cleanVioAxioms[index] = cleanedAxioms;
            JScrollPane scrollableArea = new JScrollPane(area);
            violationsPane.addTab(profileName, scrollableArea);
            counter = 1;
            ++index;
        }

        //Now do the same for the OWL 1 profiles
        for (String profileName : OWL1ProfileChecker.PROFILE_NAMES) {
            if(printResultsToTerminal) {
                System.out.println("Is in " + profileName + "? " + owl1ontologyProfileReports.get(profileName).isInProfile());
            }
            if (owl1ontologyProfileReports.get(profileName).isInProfile()) //If there are no violations
            {
                checkBoxes[Arrays.asList(profiles).indexOf(profileName)].setSelected(true); //It falls under the respective profile
                ++index;
                continue;
            }
            JTextArea area = new JTextArea(); //The list of axioms for this particular profile
            area.setEditable(false);
            OWL1ProfileReport profileReport = owl1ontologyProfileReports.get(profileName);

            String fullAxioms = "";
            String cleanedAxioms = "";

            for (String violation : profileReport.getViolations()) {
                if(violation.toString().contains("#")){
                    cleanedAxioms += counter + " - " + violation.toString().replaceAll("http:[^#]*/", "") + "\n"; //Not the most elegant
                    // regex but it works
                    area.append(counter + " - " + violation.toString().replaceAll("http:[^#]*/", "") + "\n"); //Populate this area with
                    // the list of axioms
                }else{
                    cleanedAxioms += counter + " - " + violation.toString() + "\n";
                    area.append(counter + " - " + violation.toString() + "\n"); //Populate this area with
                    // the list of axioms
                }
                fullAxioms += counter + " - " + violation.toString() + "\n";
                ++counter;
            }

            JScrollPane scrollableArea = new JScrollPane(area);
            violationsPane.addTab(profileName, scrollableArea);
            counter = 1;
            vioAreas[index] = area;
            vioAreas[index].setCaretPosition(0);
            if(printResultsToTerminal) {
                System.out.println(area.getText());
            }
            fullVioAxioms[index] = fullAxioms;
            cleanVioAxioms[index] = cleanedAxioms;
            ++index;
        }
    }

    //On click for the 'open file' button
    private class OpenFileClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();

            File workingDirectory = new File(System.getProperty("user.dir"));
            chooser.setCurrentDirectory(workingDirectory);

            String filePath = "";
            chooser.showOpenDialog(null);
            try {
                filePath = chooser.getSelectedFile().getAbsolutePath();
            } catch (NullPointerException ex) //User opens dialog box but then doesn't select anything/exits
            {
                return;
            }

            try //Try to open owl file
            {
                mainOntology = OntologyLoader.loadOntology(filePath, false).iterator().next();
                ontologies = OntologyLoader.loadOntology(filePath, true);
                frame.setTitle("OntoCheck" + " - " + filePath);
            } catch (Exception ex) { //The program still crashes if the owl file is invalid, maybe add a return boolean in the method to indicate success?
                JOptionPane.showMessageDialog(new JFrame(), "Invalid file!", "Dialog", JOptionPane.ERROR_MESSAGE);
                return;
            }

            for (JCheckBox checkbox : checkBoxes) {
                checkbox.setSelected(false);
            }
            ExpressivityChecker expChecker = new ExpressivityChecker(ontologies);
            ExpressivityChecker.AxiomClassificationResult result = expChecker.getAxiomClassifications();
            String displayExplaination = result.explanation;
            if (expChecker.getDescriptionLogicName().trim().equals("AL") && displayExplaination.trim().length() == 0) {
                displayExplaination = "~ AL is the base language used";
            }

            String explainMessage = "Expresivity: " + expChecker.getDescriptionLogicName();
            if(displayExplaination.trim().length() != 0) {
                explainMessage += "\n" + "Explanation of description logic name: \n" + displayExplaination;
            }
            explanationArea.setText(explainMessage);
            explanationArea.setCaretPosition(0);
            System.out.println("\n" + explainMessage);
            HashMap<String, ArrayList<OWLAxiom>> axiomClassifications = result.classifications;

            HashMap<String, OWLProfileReport> ontologyProfileReports = OWL2ProfileChecker.calculateOntologyProfileReports(mainOntology);
            HashMap<String, OWL1ProfileReport> owl1ontologyProfileReports = OWL1ProfileChecker.calculateOntologyProfileReports(OntologyLoader.getOntologyURI
                    (filePath));

            populateExpressivityPane(axiomClassifications);
            populateViolationsPane(ontologyProfileReports, owl1ontologyProfileReports);
        }
    }

    //On click for the 'View full IRI's" toggle check box
    private class ToggleIRIClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (expAreas != null) {
                //Toggle between full and cleaned axioms
                for (int i = 0; i < expAreas.length; ++i) {
                    if (toggleIriButton.isSelected()) {
                        expAreas[i].setText(fullExpAxioms[i]);
                    } else {
                        expAreas[i].setText(cleanExpAxioms[i]);
                    }
                    expAreas[i].setCaretPosition(0);
                }
                for (int i = 0; i < vioAreas.length; ++i) {
                    if (vioAreas[i] == null) { //Not all of the profiles will be violated
                        continue;
                    }

                    if (toggleIriButton.isSelected()) {
                        vioAreas[i].setText(fullVioAxioms[i]);
                    } else {
                        vioAreas[i].setText(cleanVioAxioms[i]);
                    }
                    vioAreas[i].setCaretPosition(0);
                }
            } else {
                toggleIriButton.setSelected(false);
            }
        }
    }

    //On click for the 'Help' menu item
    private class HelpClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            HelpWindow window = new HelpWindow();
            window.setVisible(true);
        }
    }

    private class HelpWindow extends JFrame {
        public HelpWindow() {
            initialize();
        }

        //To close only this frame and not the main application
        WindowListener exitListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        };

        public void initialize() {
            setTitle("Help");
            setSize(400, 550);
            setResizable(false);
			setLayout(new BorderLayout());
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            addWindowListener(exitListener);

            //The actual help text
			String text =  "<html>******************************************************"
			         		   + "<center>OntoCheck v1.0</center>"
			         		   + "******************************************************<br>"
			         		   + "<p align = justify>OntoCheck is a lightweight ontology profiler tool that classifies <br>"
			         		   + "OWL 1 and OWL 2 ontologies and provides more information about  <br>"
			         		   + "the expressivity and axioms. <br><br>"

					 		   + "Using the tool, users can check the profiles that ontologies fall<br>"
					 		   + "under. The expressivity of the ontology and an explanation is<br>"
					 		   + "also provided. Lastly, users can also check the axioms that<br>"
					 		   + "cause the expressivity of the ontology and the axioms that<br>"
							   + "violate the other OWL 1 and OWL 2 profiles.<br><br>"

							   + "OntoCheck was created by a group of 3 university students at<br>"
							   + "UCT as part of an Ontology Engineering course.<br>"
							   + "<br>"
							   + "<br>"
					 		   + "</p></html>";
            JLabel info = new JLabel(text);
			info.setHorizontalAlignment(JLabel.CENTER);
			info.setVerticalAlignment(JLabel.CENTER);

            add(info, BorderLayout.CENTER);
        }
    }

    //For defining what happens when you resize the window
    private class Window implements ComponentListener {
        //Adjust sizes of whatever should change over here
        public void componentResized(ComponentEvent e) {

        }

        //The rest of the methods just need to be included for the sake of the interface
        public void paintComponent(Graphics g) {
        }

        public void componentHidden(ComponentEvent e) {
        }

        public void componentMoved(ComponentEvent e) {
        }

        public void componentShown(ComponentEvent e) {
        }
    }

    public static void main(String[] args) {
        UI ui = new UI();
    }
}
