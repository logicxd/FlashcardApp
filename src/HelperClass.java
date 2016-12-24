import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

//FlashCard Program with flash card studying method, testing yourself, and multiple choice test.
//Copyright (C) 2015  Aung Moe
//
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU Affero General Public License as
//published by the Free Software Foundation, either version 3 of the
//License, or (at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU Affero General Public License for more details.
//
//You should have received a copy of the GNU Affero General Public License
//along with this program.  If not, see <http://www.gnu.org/licenses/>.

public class HelperClass{
	
	public static void addBackUp(FlashFile flashFile, String vocabTexts) {
		final String FOLDER_NAME = "BackUp";
		
		File folder = new File(FOLDER_NAME);
		folder.mkdir();		
		
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-YY hh-mm-ss");
		File saveFile = new File(folder.getAbsolutePath() + File.separator +
				flashFile.getFileName() + " " + sdf.format(cal.getTime()) + ".txt" );
		try {
			saveFile.createNewFile();
			FileWriter writer = new FileWriter(saveFile);
			writer.write(vocabTexts);
			writer.close();
		} catch (IOException e) {e.printStackTrace();}
		
		File[] listFiles = folder.listFiles();
		if (listFiles.length > 60)
			listFiles[0].delete();
	}
	
	public static void showError(String message)
	{
		//Settings of frame.
		JFrame frame = new JFrame("Error");
		frame.setLayout(new BorderLayout());
		
		//Add error message.
		JTextField errorField = new JTextField(message);
		errorField.setEditable(false);
		errorField.setHorizontalAlignment(JTextField.CENTER);
		errorField.setFont(new Font("Arial", Font.PLAIN, 15));
		frame.add(errorField, BorderLayout.CENTER);
		
		//Add button. 
		JButton button = new JButton("OK");
		button.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e)
			{
				frame.dispose();
			}
		});
		frame.add(button, BorderLayout.SOUTH);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.pack();
		Dimension dim = frame.getSize();
		dim.height += 70;
		dim.width += 50;
		frame.setSize(dim);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	private static JScrollPane finishedTestPage(ArrayList<String> words, String title, Color colorOfTitle, int width, int height) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBackground(new Color(40,40,40));
		
		//Add words
		JLabel[] label = new JLabel[words.size()];
		for (int index = 0; index < label.length; index++) {
			label[index] = new JLabel("  " + words.get(index));
			label[index].setFont(new Font("Arial", Font.PLAIN, 16));
			label[index].setForeground(Color.WHITE);
			panel.add(label[index]);
			
			//For space between each word
			JLabel emptyLabel = new JLabel(" ");
			emptyLabel.setFont(new Font("Arial", Font.PLAIN, 8));
			emptyLabel.setForeground(Color.GRAY);
			panel.add(emptyLabel); 
		}
		
		//Add scroll and border. 
		JScrollPane scroll = new JScrollPane(panel);
		scroll.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(new Color(255,255,220), 3), title, 
				TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, 
				new Font("Tahoma", Font.PLAIN, 20), colorOfTitle));
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setBackground(new Color(255,255,220));
		scroll.setPreferredSize(new Dimension(width,height));
		scroll.setSize(width,height);
		return scroll;
	}
	
	@SuppressWarnings("serial")
	public static void showFinishedTestPage(ArrayList<String> correct, ArrayList<String> incorrect, 
			JFrame container, FlashFile flashFile) {
		JFrame frame = new JFrame();
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		frame.getContentPane().setBackground(new Color(255,255,220));
		frame.setTitle("Results Page");
		
		//Show list of correct and incorrect vocabs
		JPanel wordsPanel = new JPanel();
		wordsPanel.setLayout(new BorderLayout());
		wordsPanel.setBackground(new Color(255,255,220));
		wordsPanel.add(finishedTestPage(correct, "Correct", Color.GREEN, 315,0), BorderLayout.WEST);
		wordsPanel.add(finishedTestPage(incorrect, "Incorrect", Color.RED, 315,0), BorderLayout.EAST);
		frame.add(wordsPanel);
		
		//Show Test Results
		JLabel testResultLabel = new JLabel();
		testResultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		testResultLabel.setText("Correct: " + correct.size() + "   Incorrect: " + incorrect.size()
				+ "   Grade: " + getPercentCorrect(correct.size(), correct.size() + incorrect.size()) + "%");
		testResultLabel.setFont(new Font("Arial", Font.PLAIN, 18));
		frame.add(testResultLabel);
		
		//Add Buttons
		JButton exportButton = new JButton("   Update 'Study'   ");
		exportButton.setFont(new Font("Arial", Font.PLAIN, 15));
		exportButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		exportButton.setFocusable(false);
		exportButton.setBackground(new Color(40,40,40));
		exportButton.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				flashFile.clearKnown();
				for (String eachString : correct) {
					flashFile.addKnown(eachString);
					flashFile.saveToFile();
				}
				int popUpOption = JOptionPane.showConfirmDialog(frame,
						"Updated! Do you want to go Study now?", "Study?", JOptionPane.YES_NO_OPTION);

				if (popUpOption == JOptionPane.YES_OPTION) {
					frame.dispose();
					container.dispose();
					new Study(flashFile, container);
				} else {
					frame.dispose();
				}
			}
		});
		frame.add(exportButton);
		
		
		JButton exitButton = new JButton("Don't Update");
		exitButton.setFont(new Font("Arial", Font.PLAIN, 15));
		exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		exitButton.setFocusable(false);
		exitButton.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});
		frame.add(exitButton);
		
		
		
		//Frame settings
		frame.setSize(650, 600);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(container);
		frame.setVisible(true);
	}
	
	
	
	public static JButton getExitButton()
	{
		JButton exitButton = new JButton("Exit");
		exitButton.setFont(new Font("Arial", Font.PLAIN, 15));
		exitButton.setMnemonic('E');
		exitButton.setToolTipText("Exit Program");
		return exitButton;
	}
	public static JButton getHelpButton()
	{
		JButton helpButton = new JButton("Help");
		helpButton.setFont(new Font("Arial", Font.PLAIN, 15));
		helpButton.setMnemonic('H');
		helpButton.setToolTipText("Open Helpful Information");
		return helpButton;
	}
	public static JButton getMainMenuButton()
	{
		JButton mainMenuButton = new JButton("Main Menu");
		mainMenuButton.setFont(new Font("Arial", Font.PLAIN, 15));
		mainMenuButton.setMnemonic('M');
		mainMenuButton.setToolTipText("Go to Main Menu");
		return mainMenuButton;
	}
	public static String getPercentCorrect(int correct, int total) 
	{
		DecimalFormat df = new DecimalFormat("0.00");
		double percent;
		percent = (double) (correct * 100)/total;
		return df.format(percent);
	}
	public static Integer[] scrambleVocab(FlashFile file)
	{
		Integer[] temp = new Integer[file.getCount()];
		for (int index = 0; index < file.getCount(); index++)
		{
			temp[index] = index;
		}
		Collections.shuffle(Arrays.asList(temp));
		return temp;
	}
	public static String getFileExtension(String name)
	{
		String tempString = null;
		int pointIndex = name.lastIndexOf(".");
		if (pointIndex == -1) {
		} else if (pointIndex == name.length()-1) {
		} else
			tempString = name.substring(pointIndex+1, name.length());
		return tempString;
	}
	public static String removeFileExtension(String name) {
		String tempName = name;
		String fileExtension = "vocab";
		int pointIndex = name.lastIndexOf(".");
		if (pointIndex != -1 && fileExtension.equals(name.substring(pointIndex+1, name.length()))) {
			tempName = name.substring(0, pointIndex);
		}
		return tempName;
	}
	public static void printHelp()
	{
		String loadVocabMsg = "Load Your Vocabs: (YOUR FIRST STEP!)\n" +
			"------------------------------------\n" +
			"**The format is:  [Vocabulary] (separator) [Definition]  without the brackets [] followed by Enter.\n"  +
			" *You can select which separator you want to use to split vocabularies and definitions.\n" +
			" *Click on the drop-down selection box to select the separator you want to use.\n" +
			" *If you used the wrong separator, it will delete the vocabularies.\n" +
			" *Top right is a search box to find your words. Press Enter to find next match.\n" +
			" *Ex. Aung Moe : the author of Moe's Flash Card (with ':' chosen as the separator).\n\n" +
			" *1) Click on 'New' or 'Open' on the top toolbar.\n" +
			" *2) Create a new file or load previous file.\n" +
			" *3) Start typing your vocabularies and definition!\n\n" +
			" *There's a backup folder in the same directory as the flashcard you can access.\n" + 
			" It saves a copy each time the load button is pressed, and stores up to 30 backup files.\n\n";
		
		String studyMsg = "Using The Study:\n" +
				"------------------------------------\n" +
				"**On the first page, you can select the vocabularies that you already know.\n"  +
				" *The second page, you can select the vocabularies that you want to study first.\n" +
				" *Then you'll be at the page where you can start to study.\n" +
				" *Hovering the mouse over the text area will show the definition for that vocabulary.\n" +
				" *The cards will shuffle at certain number of cards on screen.\n" + 
				" *Try to go from top-left to bottom-right, memorizing each before moving on.\n\n";
				
		String multiTestMsg = "Multiple Choice Test:\n" +
				"------------------------------------\n" +
				"**The test works better with a large list of vocabularies.\n" +
				" *The questions are all the vocabularies shuffled in different orders.\n" +
				" *Answer choices have 1 correct and 3 wrong answers from another vocab all randomly selected.\n" +
				" *Wrong answers will have their questions marked.\n\n";
		
		String testYourselfMsg = "Test Yourself:\n" +
				"------------------------------------\n" +
				"**If an error pops up, make sure you have at LEAST ONE VOCAB added.\n" +
				" *The left box holds the vocabulary question, and the right box holds the answer.\n" +
				" *Try to guess what the answer is, then click on the 'Check' button to check your answer.\n" +
				" *Click on Correct, Wrong, or Pass button depending on your guess.\n" +
				" *The statistics of your test can be seen in the middle.\n" +
				"TIP* WINDOWS ONLY - USE SHORT CUTS HOLDING ALT + Q,W,E,R.\n\n";
		
		String fillInTestMsg = "Fill In Test:\n"
				+ "------------------------------------\n"
				+ "**Similar to Test Yourself, this version has an option to type your answer.\n"
				+ " *You get to decide if your answer is correct or not.\n"
				+ " *Skipped vocabs will come back again later.\n\n"; 
		
		String updateLog = "**Update Log**\n"
				+ "------------------------------------\n"
				+ "3.6 - Added function to export data to Study the incorrect ones from tests. Other minor changes.\n"
				+ "3.5 - Minor changes to LoadVocab caret position, loading button, and scroll multiplechoice.\n"
				+ "3.4 - Changed from tab to shift enter in Fill In Test for viewing vocab/def.\n"
				+ "3.3 - Fixed Skipped Button bug for test yourself. Caret Position will stay in LoadVocab.\n"
				+ "3.2 - Backup works with Save and Load button. Increased to 60 files storage.\n"
				+ "3.1 - Minor changes"
				+ "3.0 - Added a 2nd version of Test Yourself. CHECK BUGS. More improvements later.\n"
				+ "2.5 - Added word finding function in Load Your Vocab.\n"
				+ "2.4 - Backup up to 30 vocabs on load, deleted warnings.\n"
				+ "2.3 - Hotkeys - Save, Undo, Redo.\n"
				+ "2.2 - Fixed Bugs.\n"
				+ "2.1 - Added clear all for separators. Fixed some bugs. Main menu title has folder name.\n"
				+ "2.0 - Major change! Added multiple vocab file support. Data storage from 3 files into 1.\n"
				+ "      Changed where the box would appear relative to the previous box.\n"
				+ "1.4 - Changed separator to only pick up the first occassion and color change.\n"
				+ "1.19.13 - Special Color Theme.\n"
				+ "1.3.2 - Color theme for the functions.\n"
				+ "1.3.1 - Added colored fonts to 'Load Your Vocabs' and background theme.\n"
				+ "1.2 - Changed the way separators worked.\n"
				+ "1.1 - Added choice of separators for vocab and definition.\n\n";
		
		String updateGui = "**Update Flashcard**\n"
				+ "------------------------------------\n"
				+ "http://1drv.ms/1XYWdB5\n"
				+ "You only need the 'Moe's Flashcard' file.\n";
		
		JFrame panel = new JFrame("Using this flash card");
		JTextArea content = new JTextArea(loadVocabMsg + studyMsg + testYourselfMsg + fillInTestMsg + multiTestMsg + updateLog + updateGui);
		
		//Settings for Text 
		content.setFont(new Font("Arial", Font.PLAIN, 18));
		content.setEditable(false);
		
		//Settings for Scroll
		JScrollPane scroll = new JScrollPane(content);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		panel.add(scroll);
		
		
		//Settings of Frame
		panel.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		panel.setSize(720,500);
		panel.setVisible(true);
	}

}
