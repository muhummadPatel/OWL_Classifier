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

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.*;
import java.util.*;

public class UI extends JFrame// implements ComponentListener
{
	private JFrame frame;
	private JTextField fileName;
	private JLabel speciesLabel;
	private JLabel expressivityLabel;
  private JTabbedPane expressivityPane;
  private JTabbedPane violationsPane;

	private float fileNameAreaPercentage = 0.5f; //Percentage of the width of the screen that the file name text area occupies
	private float fontSize = 12.0f; //Approximation of the font size, to calculate the width of text components

  OWLOntology mainOntology;
  Set<OWLOntology> ontologies;

	private String species; //OWL 2DL, QL, etc.
	private String[] expressivity; //['S','R','O','Q','(D)'] etc.
  String [][] axioms; //Buckets for axioms for each letter

	//Constructor
    public UI()
    {
    	initialize();
    }

    private void initialize()
    {
/*    	frame = new JFrame("Ontology Profile Checker");
    	frame.setSize(700,900);
    	frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    	frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));
    	frame.addComponentListener(new Window()); //Window is defined further down. Mainly for doing stuff when the window is resized.

    	//Panel for the open file buttons and text areas next to it
    	JPanel buttonPanel = new JPanel();
    	//Panel for expressivity and species
    	JPanel infoPanel = new JPanel();

    	fileName = new JTextField("[File Name]",(int)(frame.getBounds().width*fileNameAreaPercentage/fontSize));
    	//fileName.setLineWrap(true);
    	//fileName.setEditable(false);

    	infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));
    	speciesLabel = new JLabel("OWL Species: ");
    	expressivityLabel = new JLabel("Expressivity: ");
    	infoPanel.add(expressivityLabel);
    	infoPanel.add(speciesLabel);


    	JButton openFile = new JButton("Choose File");
    	openFile.addActionListener(new OpenFileClickListener());

    	JButton loadOntology = new JButton("Load Ontology");
    	loadOntology.addActionListener(new LoadOntologyClickListener());

    	buttonPanel.setLayout(new FlowLayout());
    	buttonPanel.add(openFile);
    	buttonPanel.add(fileName);
    	buttonPanel.add(loadOntology);

    	frame.add(buttonPanel);//, BorderLayout.NORTH);
    	frame.add(infoPanel);//, BorderLayout.CENTER);

    	frame.setVisible(true);*/

      frame = new JFrame("Ontology Profile Checker");
      frame.setSize(700,900);
      frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
      frame.setLayout(new GridBagLayout());
      frame.addComponentListener(new Window()); //Window is defined further down. Mainly for doing stuff when the window is resized.

      JMenuBar bar = new JMenuBar();
      JMenu fileMenu = new JMenu("File");
      JMenuItem loadItem = new JMenuItem("Load Ontology");
      loadItem.addActionListener(new OpenFileClickListener());
      fileMenu.add(loadItem);
      bar.add(fileMenu);
      frame.setJMenuBar(bar);

      GridBagConstraints gbc = new GridBagConstraints();

      speciesLabel = new JLabel("OWL Species: ");
      gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1; gbc.gridheight = 1;
      gbc.weighty = 0; gbc.weightx = 0;
      frame.add(speciesLabel,gbc);

      expressivityLabel = new JLabel("Expressivity: ");
      gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.gridheight = 1;
      gbc.weighty = 0.14; gbc.weightx = 0;
      frame.add(expressivityLabel,gbc);

      expressivityPane = new JTabbedPane();
      JTextArea tempField1 = new JTextArea();
    //  expressivityPane.setSize(500,400);
      expressivityPane.addTab("Expressivity Information", tempField1);
      gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4; gbc.gridheight = 4;
      gbc.weighty = 1; gbc.weightx = 1;
      gbc.fill = GridBagConstraints.BOTH;
      frame.add(expressivityPane,gbc);

      violationsPane = new JTabbedPane();
      JTextArea tempField2 = new JTextArea();
      //violationsPane.setSize(500,400);
      violationsPane.addTab("Violation Information",tempField2);
      gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 4; gbc.gridheight = 4;
      gbc.weighty = 1; gbc.weightx = 1;
      gbc.fill = GridBagConstraints.BOTH;
      frame.add(violationsPane,gbc);

      frame.setVisible(true);
    }


    //Just to test that I can dynamically populate the tabbed panes after loading the onto
    //pane = 1 or 2 (expressivityPane or violationsPane); titles = headings of tables; values = bucket of axiomns for each tab
    public void populatePane(int pane, String[] titles, String[][] values)
    {
      JTabbedPane tabbedPane;

      if (pane == 1)
        tabbedPane = expressivityPane;
      else
        tabbedPane = violationsPane;

      while (tabbedPane.getTabCount() > 0) //Remove current panes
        tabbedPane.remove(0);

      for (int i = 0; i < titles.length; ++i)
      {
        JTextArea area = new JTextArea();
        //violationsPane.setSize(500,400);
        tabbedPane.addTab(titles[i], area);
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

        mainOntology = OntologyLoader.loadOntology(filePath, false).iterator().next();
        ontologies = OntologyLoader.loadOntology(filePath, true);
    	}
    }

    //On click for the 'Load' button
    private class LoadOntologyClickListener implements ActionListener
    {
    	public void actionPerformed(ActionEvent e)
    	{
    		//Create Ontology Loader object
    		String fName = fileName.getText();
    		// ...

    		//Do expressivity checks
    		// ...
    		//Use dummy data in the mean time
    		//If the methods return different data structures, can just edit this or transform them into arrays or whatever
    		species = "OWL 2 DL";
    		expressivity = new String[]{"S","R","O"};
        axioms = new String[][]{
          {"bla","hey"},
          {"hi","bbs"},
          {"Ss","sass"}
        };
        populatePane(1, expressivity, axioms);
    	}
    }

    //For defining what happens when you resize the window
    private class Window implements ComponentListener
    {
    	//Adjust sizes of whatever should change over here
	    public void componentResized(ComponentEvent e)
	    {
	    	int newColumns = (int)(frame.getBounds().width*fileNameAreaPercentage/fontSize); //Calc the new width
	        fileName.setColumns(newColumns);
	    	fileName.setText(fileName.getText()+""); //Slight hack to force the text area to update its size. repaint() doesn't work for some reason
	    					//Does not work again for some reason! Will try fix later
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
