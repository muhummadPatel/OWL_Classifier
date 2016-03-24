/***
 * Ontology Engineering: Profile Checker
 * User-interface class
 * March, 2016
 ***/

//package com.oe;
	/* This is commented out in the mean time because you need to specify
	   the class path and other package stuff when compiling it alone
	   from the command line */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;

public class UI extends JFrame// implements ComponentListener
{
	private JFrame frame;
	private JTextField fileName; //Area next to the 'choose file' button
	private JLabel speciesLabel;
	private JLabel expressivityLabel;

	private float fileNameAreaPercentage = 0.5f; //Percentage of the width of the screen that the file name text area occupies
	private float fontSize = 12.0f; //Approximation of the font size, to calculate the width of text components

	private String species; //OWL 2DL, QL, etc.
	private String[] expressivity; //['S','R','O','Q','(D)'] etc.

	//Constructor
    public UI()
    {
    	initialize();
    }

    private void initialize()
    {
    	frame = new JFrame("Ontology Profile Checker");
    	frame.setSize(700,900);
    	frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    	frame.setLayout(new BorderLayout());
    	frame.addComponentListener(new Window()); //Window is defined further down. Mainly for doing stuff when the window is resized.

    	//Panel for the open file buttons and text areas next to it
    	JPanel buttonPanel = new JPanel();
    	//Panel for expressivity and species
    	JPanel infoPanel = new JPanel();

    	fileName = new JTextField("[File Name]",(int)(frame.getBounds().width*fileNameAreaPercentage/fontSize));
    	//fileName.setLineWrap(true);
    	//fileName.setEditable(false);

    	infoPanel.setLayout(new BorderLayout());
    	speciesLabel = new JLabel("OWL Species: ");
    	expressivityLabel = new JLabel("Expressivity: ");
    	infoPanel.add(expressivityLabel, BorderLayout.NORTH);
    	infoPanel.add(speciesLabel, BorderLayout.SOUTH);


    	JButton openFile = new JButton("Choose File");
    	openFile.addActionListener(new OpenFileClickListener());

    	JButton loadOntology = new JButton("Load Ontology");
    	loadOntology.addActionListener(new LoadOntologyClickListener());

    	buttonPanel.setLayout(new FlowLayout());
    	buttonPanel.add(openFile);
    	buttonPanel.add(fileName);
    	buttonPanel.add(loadOntology);

    	frame.add(buttonPanel, BorderLayout.NORTH);
    	frame.add(infoPanel, BorderLayout.CENTER);

    	frame.setVisible(true);
    }

    //On click for the 'open file' button
    private class OpenFileClickListener implements ActionListener
    {
    	public void actionPerformed(ActionEvent e)
    	{
    		JFileChooser chooser = new JFileChooser();

    		File workingDirectory = new File(System.getProperty("user.dir"));
    		chooser.setCurrentDirectory(workingDirectory);

    		chooser.showOpenDialog(null);
    		try
    		{
    			String fName = chooser.getSelectedFile().getAbsolutePath();
    			fileName.setText(fName);
    			fileName.setColumns((int)(frame.getBounds().width*fileNameAreaPercentage/fontSize));
    		}
    		catch(Exception ex) //User opens dialog box but then doesn't select anything/exits
    		{}
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
    		expressivity = new String[]{"S","R","O","I","Q"};
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
