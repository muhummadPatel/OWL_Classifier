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
import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.profiles.OWL2QLProfile;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import org.semanticweb.owlapi.profiles.OWLProfileViolation;
import org.semanticweb.owlapi.util.*;

import java.io.*;
import java.util.*;

public class UI extends JFrame// implements ComponentListener
{
	private JFrame frame;
	private JTextArea explanationArea;
	private JLabel speciesLabel;
	private JLabel expressivityLabel;
  private JTabbedPane expressivityPane;
  private JTabbedPane violationsPane;

	private String[] profiles = new String[]{"OWL 2", "OWL 2 EL", "OWL 2 DL", "OWL 2 QL", "OWL 2 RL"};
	private JCheckBox[] checkBoxes = new JCheckBox[profiles.length]; //The check box profiles

	private float fileNameAreaPercentage = 0.5f; //Percentage of the width of the screen that the file name text area occupies
	private float fontSize = 12.0f; //Approximation of the font size, to calculate the width of text components

  OWLOntology mainOntology;
  Set<OWLOntology> ontologies;

	//Constructor
    public UI()
    {
    	initialize();
    }

    private void initialize()
{
      //The main frame
      frame = new JFrame("Ontology Profile Checker");
      frame.setSize(700,900);
      frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
      frame.setLayout(new GridBagLayout());
      frame.addComponentListener(new Window()); //Window is defined further down. Mainly for doing stuff when the window is resized.

      //Creating the menu bar
      JMenuBar bar = new JMenuBar();
      JMenu fileMenu = new JMenu("File");
      JMenuItem loadItem = new JMenuItem("Load Ontology");
      JMenuItem helpItem = new JMenuItem("Help");
      loadItem.addActionListener(new OpenFileClickListener());
      helpItem.addActionListener(new HelpClickListener());
      fileMenu.add(loadItem);
      fileMenu.add(helpItem);
      bar.add(fileMenu);
      frame.setJMenuBar(bar);

      /* The rest is to create the indivual components and place them using gridbaglayout*/
      GridBagConstraints gbc = new GridBagConstraints();


      expressivityLabel = new JLabel("Expressivity: ");
      gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1; gbc.gridheight = 1;
      gbc.weighty = 0.14; gbc.weightx = 0;
      frame.add(expressivityLabel,gbc);

			for (int i = 0; i < profiles.length; ++i)
			{
				checkBoxes[i] = new JCheckBox(profiles[i]);
				checkBoxes[i].setModel(new DefaultButtonModel() //Only way to make check boxes read only. SetEnabled(false) greys out the component
				{
					@Override
					public void setSelected(boolean b)
					{
						super.setSelected(b); //This allows me to programatically change the check boxes, but not the user (I don't really know why)
				}});
					gbc.gridx = i; gbc.gridy = 1; gbc.gridwidth = 1; gbc.gridheight = 1;
			//		gbc.weighty = 0; gbc.weightx = 0;
					frame.add(checkBoxes[i],gbc);
			}

		/*	speciesLabel = new JLabel("OWL Species: ");
      gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1; gbc.gridheight = 1;
      gbc.weighty = 0; gbc.weightx = 0;
      frame.add(speciesLabel,gbc);*/



			//The explanation area before the expressivity information
			explanationArea = new JTextArea("");
			explanationArea.setEditable(false);
			JScrollPane scrollableArea = new JScrollPane (explanationArea);//, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 6; gbc.gridheight = 3;
			gbc.weighty = 1; gbc.weightx = 1;
			gbc.fill = GridBagConstraints.BOTH;
			frame.add(scrollableArea,gbc);

      expressivityPane = new JTabbedPane();
      JTextArea tempField1 = new JTextArea();
    //  expressivityPane.setSize(500,400);
      expressivityPane.addTab("Expressivity Information", tempField1);
      gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 6; gbc.gridheight = 4;
      gbc.weighty = 1; gbc.weightx = 1;
      gbc.fill = GridBagConstraints.BOTH;
      frame.add(expressivityPane,gbc);

      violationsPane = new JTabbedPane();
      JTextArea tempField2 = new JTextArea();
      //violationsPane.setSize(500,400);
      violationsPane.addTab("Violation Information",tempField2);
      gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 6; gbc.gridheight = 4;
      gbc.weighty = 1; gbc.weightx = 1;
      gbc.fill = GridBagConstraints.BOTH;
      frame.add(violationsPane,gbc);

      frame.setVisible(true);
    }

    //Called when an owl file is loaded to populate the tabbed pane with the letters and their axioms
    public void populateExpressivityPane(HashMap<String, ArrayList<OWLAxiom>> axiomClassifications)
    {
      //Remove the current content in the expressivity tabbed pane
      while (expressivityPane.getTabCount() > 0) //Remove current tabs
        expressivityPane.remove(0);

      int counter = 1; //Counter of axioms for each letter
      for(String letter: axiomClassifications.keySet())
      {
          JTextArea area = new JTextArea(); //The list of axioms for this particular letter
          area.setEditable(false);
          for(OWLAxiom axiom: axiomClassifications.get(letter))
          {
              String cleanedAxiom =  axiom.toString().replaceAll("(?<=:)[^#]*/","").replaceAll("http:",""); //Not the most elegant regex but it works
              area.append(counter + " - " + cleanedAxiom + "\n"); //Populate this area with the list of axioms
              ++counter;
          }

          //Add the final list for the letter as a tab in the tabbed pane. We want it to be scrollable.
          JScrollPane scrollableArea = new JScrollPane (area);
          expressivityPane.addTab(letter,scrollableArea);
          counter = 1;
      }
    }

		//To populate the violations pane
		public void populateViolationsPane(HashMap<String, OWLProfileReport> ontologyProfileReports)
		{
			checkBoxes[0].setSelected(true);

			while (violationsPane.getTabCount() > 0) //Remove current tabs
				violationsPane.remove(0);

			int counter = 1; //Counter of axioms for each letter

			for(String profileName : ProfileChecker.PROFILE_NAMES)
			{
				if (ontologyProfileReports.get(profileName).getViolations().size() == 0) //If there are no violations
				{
					checkBoxes[Arrays.asList(profiles).indexOf(profileName)].setSelected(true); //It falls under the respective profile
					continue;
				}

				JTextArea area = new JTextArea(); //The list of axioms for this particular profile
				area.setEditable(false);

				for (OWLProfileViolation violation : ontologyProfileReports.get(profileName).getViolations())
				{
                    String cleanedViolation =  violation.toString().replaceAll("(?<=:)[^#]*/","").replaceAll("http:",""); //Not the most elegant regex but it works
					area.append(counter + " - " + cleanedViolation + "\n"); //Populate this area with the list of axioms
					++counter;
				}
				//Add the final list for the profile as a tab in the tabbed pane. We want it to be scrollable.
				JScrollPane scrollableArea = new JScrollPane (area);
				violationsPane.addTab(profileName,scrollableArea);
				counter = 1;
			}
		}

    //On click for the 'open file' button
    private class OpenFileClickListener implements ActionListener
    {
    	public void actionPerformed(ActionEvent e)
    	{
    		JFileChooser chooser = new JFileChooser();

    		File workingDirectory = new File(System.getProperty("user.dir"));
    		chooser.setCurrentDirectory(workingDirectory);

        String filePath = "";
    		chooser.showOpenDialog(null);
    		try
    		{
    			filePath = chooser.getSelectedFile().getAbsolutePath();
    		}
    		catch(Exception ex) //User opens dialog box but then doesn't select anything/exits
    		{}

        try //Try to open owl file
        {
          mainOntology = OntologyLoader.loadOntology(filePath, false).iterator().next();
          ontologies = OntologyLoader.loadOntology(filePath, true);
          frame.setTitle(frame.getTitle() + " - " + filePath);
        }
        catch(Exception ex)
        { //The program still crashes if the owl file is invalid, maybe add a return boolean in the method to indicate success?
          System.out.println("Invalid file!"); //Will change this to make a error window popup
        }

        ExpressivityChecker expChecker = new ExpressivityChecker(ontologies);
        expressivityLabel.setText("Expresivity: " + expChecker.getDescriptionLogicName());
        ExpressivityChecker.AxiomClassificationResult result = expChecker.getAxiomClassifications();
				explanationArea.setText("Explanation: \n\n" + result.explanation);
        HashMap<String, ArrayList<OWLAxiom>> axiomClassifications = result.classifications;

				System.out.println(result.explanation);
				OWLOntology mainOntology = OntologyLoader.loadOntology(filePath, false).iterator().next();
				HashMap<String, OWLProfileReport> ontologyProfileReports = ProfileChecker.calculateOntologyProfileReports(mainOntology);

        populateExpressivityPane(axiomClassifications);
				populateViolationsPane(ontologyProfileReports);
    	}
    }

    //On click for the 'Help' menu item
    private class HelpClickListener implements ActionListener
    {
    	public void actionPerformed(ActionEvent e)
    	{
          //Maybe just load a popup box here with information about the app, etc.
    	}
    }

    //For defining what happens when you resize the window
    private class Window implements ComponentListener
    {
    	//Adjust sizes of whatever should change over here
	    public void componentResized(ComponentEvent e)
	    {

	    }

	    //The rest of the methods just need to be included for the sake of the interface
	    public void paintComponent(Graphics g) {}

	    public void componentHidden(ComponentEvent e) {}

	    public void componentMoved(ComponentEvent e) {}

	    public void componentShown(ComponentEvent e) {}
    }

    public static void main(String[]args)
    {
    	UI ui = new UI();
    }
}
