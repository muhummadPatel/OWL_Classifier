/***
 * Ontology Engineering: Profile Checker
 * User-interface class
 * March, 2016
 ***/

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

public class UI extends JFrame {
    private JFrame frame;
    private JTextArea explanationArea;
    private JLabel speciesLabel;
    //private JLabel expressivityLabel;
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

	private int letterWidth = 10; //Total length of the letter + spaces used to for expressivity tabs

    OWLOntology mainOntology;
    Set<OWLOntology> ontologies;

    //Constructor
    public UI() {
        initialize();
    }

    private void initialize() {
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
        frame = new JFrame("Ontology Profile Checker");
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
        profileList.addAll(ProfileChecker.PROFILE_NAMES);
        profileList.addAll(OWL1ProfileChecker.PROFILE_NAMES);
        profiles = new String[profileList.size()];
        profiles = profileList.toArray(profiles);
        checkBoxes = new JCheckBox[profiles.length];

		checkboxHeading = new JLabel("<html><b><u>OWL Profiles</u></b></html>");
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(10,10,10,10); //Padding
		gbc.gridwidth = 4;
		gbc.gridheight = 1;
		gbc.weighty = 0;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.BOTH;
		frame.add(checkboxHeading,gbc);

		gbc.insets = new Insets(0,0,0,0); //Padding
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
	    gbc.insets = new Insets(10,10,10,10); //Padding


        //The explanation area before the expressivity information
        explanationArea = new JTextArea("");
        explanationArea.setEditable(false);
        JScrollPane scrollableArea = new JScrollPane(explanationArea);//, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 9;
        gbc.gridheight = 3;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        frame.add(scrollableArea, gbc);

		expHeading = new JLabel("<html><b><u>Expressivity Axioms</u></b></html>");
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.weighty = 0;
		gbc.insets = new Insets(0,10,10,0); //Padding different for label
		gbc.gridheight = 1;
		frame.add(expHeading,gbc);

		gbc.insets = new Insets(10,10,10,10); //Reset padding

        expressivityPane = new JTabbedPane();
        JTextArea tempField1 = new JTextArea();
        //  expressivityPane.setSize(500,400);
        expressivityPane.addTab("          ", tempField1);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 9;
        gbc.gridheight = 4;
        gbc.weighty = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        frame.add(expressivityPane, gbc);

		profileHeading = new JLabel("<html><b><u>Profile Violation Axioms</u></b></html>");
		gbc.gridx = 0;
		gbc.gridy = 10;
		gbc.weighty = 0;
		gbc.insets = new Insets(0,10,10,0);
		gbc.gridheight = 1;
		frame.add(profileHeading,gbc);

		gbc.insets = new Insets(10,10,10,10);

        violationsPane = new JTabbedPane();
        JTextArea tempField2 = new JTextArea();
        //violationsPane.setSize(500,400);
        violationsPane.addTab("          ", tempField2);
        gbc.gridx = 0;
        gbc.gridy = 11;
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
                cleanedAxioms += counter + " - " + axiom.toString().replaceAll("(?<=:)[^#]*/", "").replaceAll("http:", "") + "\n"; //Not the most elegant
                // regex but it works
                fullAxioms += counter + " - " + axiom.toString() + "\n";
                area.append(counter + " - " + axiom.toString().replaceAll("(?<=:)[^#]*/", "").replaceAll("http:", "") + "\n"); //Populate this area with the list of axioms
                ++counter;
            }

            //Add the final list for the letter as a tab in the tabbed pane. We want it to be scrollable.
            expAreas[index] = area;
            fullExpAxioms[index] = fullAxioms;
            cleanExpAxioms[index] = cleanedAxioms;
            JScrollPane scrollableArea = new JScrollPane(area);
			String formatString = String.format(letter + "%1$"+ letterWidth + "s", ""); //Add the padding
            expressivityPane.addTab(formatString, scrollableArea);
            counter = 1;
            ++index;
        }
    }

	//To populate the violations pane
			public void populateViolationsPane(HashMap<String, OWLProfileReport> ontologyProfileReports, HashMap<String, OWL1ProfileReport> owl1ontologyProfileReports)
			{
				while (violationsPane.getTabCount() > 0) //Remove current tabs
					violationsPane.remove(0);

				int counter = 1; //Counter of axioms for each letter

				vioAreas = new JTextArea[profiles.length];
				fullVioAxioms = new String[profiles.length];
				cleanVioAxioms = new String[profiles.length];
				int index = 0;
				for(String profileName : ProfileChecker.PROFILE_NAMES)
				{

					if (ontologyProfileReports.get(profileName).getViolations().size() == 0) //If there are no violations
					{
						checkBoxes[Arrays.asList(profiles).indexOf(profileName)].setSelected(true); //It falls under the respective profile
						++index;
						continue;
					}
					String fullAxioms = "";
					String cleanedAxioms = "";
					JTextArea area = new JTextArea(); //The list of axioms for this particular profile
					area.setEditable(false);

					for (OWLProfileViolation violation : ontologyProfileReports.get(profileName).getViolations())
					{
						cleanedAxioms += counter + " - " + violation.toString().replaceAll("(?<=:)[^#]*/", "").replaceAll("http:", "") + "\n"; //Not the most elegant regex but it works
						fullAxioms += counter + " - " + violation.toString() + "\n";
						area.append(counter + " - " + violation.toString().replaceAll("(?<=:)[^#]*/", "").replaceAll("http:", "") + "\n"); //Populate this area with the list of axioms
						++counter;
					}
					//Add the final list for the profile as a tab in the tabbed pane. We want it to be scrollable.
					vioAreas[index] = area;
					fullVioAxioms[index] = fullAxioms;
					cleanVioAxioms[index] = cleanedAxioms;
					JScrollPane scrollableArea = new JScrollPane (area);
					violationsPane.addTab(profileName,scrollableArea);
					counter = 1;
					++index;
				}

				//Now do the same for the OWL 1 profiles
				for(String profileName : OWL1ProfileChecker.PROFILE_NAMES)
				{
					if (owl1ontologyProfileReports.get(profileName).getViolations().size() == 0) //If there are no violations
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

					for(String violation : profileReport.getViolations())
					{
						cleanedAxioms += counter + " - " + violation.toString().replaceAll("(?<=:)[^#]*/","").replaceAll("http:","") + "\n"; //Not the most elegant regex but it works
						fullAxioms += counter + " - " + violation.toString() + "\n";
						area.append(counter + " - " + violation.toString().replaceAll("(?<=:)[^#]*/", "").replaceAll("http:", "") + "\n"); //Populate this area with the list of axioms
						++counter;
					}

					JScrollPane scrollableArea = new JScrollPane (area);
					violationsPane.addTab(profileName,scrollableArea);
					counter = 1;
					vioAreas[index] = area;
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

            System.out.println("Opened");
            try //Try to open owl file
            {
                mainOntology = OntologyLoader.loadOntology(filePath, false).iterator().next();
                ontologies = OntologyLoader.loadOntology(filePath, true);
                frame.setTitle(frame.getTitle() + " - " + filePath);
            } catch (Exception ex) { //The program still crashes if the owl file is invalid, maybe add a return boolean in the method to indicate success?
                JOptionPane.showMessageDialog(new JFrame(), "Invalid file!", "Dialog", JOptionPane.ERROR_MESSAGE);
                return;
            }

            for(JCheckBox checkbox : checkBoxes) {
                checkbox.setSelected(false);
            }
            ExpressivityChecker expChecker = new ExpressivityChecker(ontologies);
            //expressivityLabel.setText("Expresivity: " + expChecker.getDescriptionLogicName());
            ExpressivityChecker.AxiomClassificationResult result = expChecker.getAxiomClassifications();
            String displayExplaination = result.explanation;
            System.out.println(displayExplaination + " " + expChecker.getDescriptionLogicName());
            if(expChecker.getDescriptionLogicName().trim().equals("AL") && displayExplaination.trim().length()==0) {
                displayExplaination = "~ AL is the base language used";
            }
            explanationArea.setText("Expresivity: " + expChecker.getDescriptionLogicName() + "\n"+ "Explanation of description logic name: \n" + displayExplaination);
            HashMap<String, ArrayList<OWLAxiom>> axiomClassifications = result.classifications;

            HashMap<String, OWLProfileReport> ontologyProfileReports = ProfileChecker.calculateOntologyProfileReports(mainOntology);
            HashMap<String, OWL1ProfileReport> owl1ontologyProfileReports = OWL1ProfileChecker.calculateOntologyProfileReports(OntologyLoader.getOntologyURI(filePath));

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
                    if (toggleIriButton.isSelected())
                        expAreas[i].setText(fullExpAxioms[i]);
                    else
                        expAreas[i].setText(cleanExpAxioms[i]);
                }
				for (int i = 0; i < vioAreas.length; ++i){
					if (vioAreas[i] == null) //Not all of the profiles will be violated
						continue;

					if (toggleIriButton.isSelected())
						vioAreas[i].setText(fullVioAxioms[i]);
					else
						vioAreas[i].setText(cleanVioAxioms[i]);
				}
            } else {
                toggleIriButton.setSelected(false);
            }
        }
    }

    //On click for the 'Help' menu item
    private class HelpClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //Maybe just load a popup box here with information about the app, etc.
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
