import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

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

public class Study extends JFrame implements ActionListener, MouseListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6212704747715369339L;
	//Content Container for Study
	private JPanel masterPanel = new JPanel();
	private CardLayout cardLayout = new CardLayout();
	private String[] cardPanel = {"knownPanel", "studyPanel"};
	
	//Files
	private FlashFile flashFile;
	
	//Reusing components.
	private JPanel toolbar;	
	private JButton mainMenuButton;
	private JTextField descriptionText;
	private JButton clearAllButton;

//////////////////////////////KnowVocabs Page/////////////////////////////
	private JPanel knownVocabPanel;
	private JPanel checkBoxPanel;
	private JCheckBox[] vocabCheckBox;
	private JButton nextButton1;
	private JButton nextButton2;
	
//////////////////////////////Study page//////////////////////////////////
	private JPanel studyMain;
	private JPanel studyPanel;
	private String[] vocabOrder;
	private String[] defOrder;
	private int sizeVocab;
	private int indexOfSlide = 0;
	private boolean shuffled = false;
	private boolean shouldIncrement = true;
	private boolean endShown = false;
	private String[] message = { 
			"Read each vocabs out loud from top-left to bottom-right. (Clicking on the card will flip it to show the answer)",
			"Can you say these definition in order? (Check your answers!)" 
	};
	
	//These are for vocabs and definitions
	GridBagConstraints gc = new GridBagConstraints();
	private JTextArea[] vocabDefButton;
	private JScrollPane[] vocabDefScroll;
	private final static int vocabDefSize = 265;
	private final static int vocabDefFrameWidth = 900;
	private final static int vocabDefFrameHeight = 700;
	private JButton vocabDefNext;
	private JButton retestButton;
	
	public Study(FlashFile fFile, Container container){
		//Set up frame.
		super("Study!");
		masterPanel.setLayout(cardLayout);
		flashFile = fFile;
		
		//Add panels
		addKnownVocabs(1);
		//Add master Panel to the main frame.
		add(masterPanel);
		
		//First show the knownVocab panel.
		cardLayout.show(masterPanel, cardPanel[0]);
		
		//Settings for Frame
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(900,600);
		setLocationRelativeTo(container);
		setVisible(true);
	}
	
	
	//Preconditon: only valid modes are 1 and 2.
	//Mode 1 stores the known vocabs, mode 2 stores the vocabs that should be studied first.
	//Purpose: The user selects which vocabs they know so they'll be saved to known list.
	//Uses: FlashFile, KnownFile.
	//Postcondition: adds files checked vocabs to KnownFile so they'll not be used in this class.
	private void addKnownVocabs(int mode) {
		String message = "Wrong mode, must be 1 or 2";
		knownVocabPanel = new JPanel(new BorderLayout());
		
		if (mode == 1)
			message = "Check the boxes of the vocabs you already know.";
		else if (mode == 2)
			message = "Select the vocabs you wish to study first.";
		
		//JTextField: Description
		descriptionText = new JTextField(message);
		descriptionText.setFont(new Font("Arial", Font.PLAIN, 18));
		descriptionText.setHorizontalAlignment(JTextField.CENTER);
		descriptionText.setEditable(false);
		descriptionText.setForeground(Color.black);
		knownVocabPanel.add(descriptionText, BorderLayout.NORTH);
		knownVocabPanel.setPreferredSize(new Dimension());
		
		//JPanel: check boxes
		checkBoxPanel = new JPanel(new GridLayout(0,1));
//		checkBoxPanel = new JPanel(new GridLayout(5,0));
		vocabCheckBox = new JCheckBox[flashFile.getCount()];
		if (mode == 1)
		{
			//For all the vocabs stored in flashFile.
			for (int index = 0; index < flashFile.getCount(); index++)
			{
				vocabCheckBox[index] = new JCheckBox(flashFile.getVocab(index)) ;
				
				if (flashFile.findKnownWord(flashFile.getVocab(index)))
				{
					vocabCheckBox[index].setSelected(true);
				}
				vocabCheckBox[index].setFont(new Font("Arial", Font.PLAIN, 20));
				vocabCheckBox[index].setPreferredSize(new Dimension(0,40));
				vocabCheckBox[index].setForeground(Color.lightGray);
				vocabCheckBox[index].setBackground(new Color(75,0,0));
				checkBoxPanel.setBackground(new Color(75,0,0));
				checkBoxPanel.add(vocabCheckBox[index]);
			}
		}
		else if (mode == 2)
		{
			for (int index = 0; index < flashFile.getCount(); index++)
			{
				vocabCheckBox[index] = new JCheckBox(flashFile.getVocab(index));
				//If the vocabulary word at the index is NOT found in knownFile.
				//In other words, these are the vocabs that the person wishes to study.
				if (!flashFile.findKnownWord(flashFile.getVocab(index)))
				{
					//If any of the words were previously selected, reselect them.
					if (flashFile.findPriorityWord(flashFile.getVocab(index)))
					{
						vocabCheckBox[index].setSelected(true);
					}
					vocabCheckBox[index].setFont(new Font("Arial", Font.PLAIN, 20));
					vocabCheckBox[index].setPreferredSize(new Dimension(0,40));
					vocabCheckBox[index].setForeground(Color.lightGray);
					vocabCheckBox[index].setBackground(new Color(28,0,0));
					checkBoxPanel.setBackground(new Color(28,0,0));
					checkBoxPanel.add(vocabCheckBox[index]);
				}
			}
		}
		JScrollPane scroll = new JScrollPane(checkBoxPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		knownVocabPanel.add(scroll, BorderLayout.CENTER);
		
		//JPanel: bottom panel for done button and main menu.
		toolbar = new JPanel(new FlowLayout());
		//Main Button set once.
		mainMenuButton = HelperClass.getMainMenuButton();
		mainMenuButton.addActionListener(this);
		toolbar.add(mainMenuButton);
		clearAllButton = new JButton("Clear All");
		clearAllButton.setFont(new Font("Arial", Font.PLAIN, 15));
		clearAllButton.addActionListener(this);
		toolbar.add(clearAllButton);
		if (mode == 1)
		{
			nextButton1 = new JButton("Next");
			nextButton1.setFont(new Font("Arial", Font.PLAIN, 15));
			nextButton1.setMnemonic('N');
			nextButton1.setToolTipText("Vocab Preference Page");
			nextButton1.addActionListener(this);
			toolbar.add(nextButton1);
		}
		else if (mode == 2)
		{
			nextButton2 = new JButton("Next");
			nextButton2.setFont(new Font("Arial", Font.PLAIN, 15));
			nextButton2.setMnemonic('N');
			nextButton2.setToolTipText("Start Studying");
			nextButton2.addActionListener(this);
			toolbar.add(nextButton2);
		}
	
		knownVocabPanel.add(toolbar, BorderLayout.SOUTH);
		
		//Add to master panel.
		masterPanel.add(knownVocabPanel, cardPanel[0]);
	}
	//After knownVocab panel, this will add all the currently selected vocabs to the known list.
	private void storeKnownVocabs(String fileType)
	{
		//Add all the vocabs checked to the new KnownFile file.
		for (int index = 0; index < flashFile.getCount(); index++)
		{
			if (vocabCheckBox[index].isSelected()) {
				if (fileType.equals("Known")) {
					flashFile.addKnown(flashFile.getVocab(index));
				} else if (fileType.equals("Priority")) {
					flashFile.addPriority(flashFile.getVocab(index));
				}
			}
		}
		flashFile.saveToFile();
	}
	
	//Postcondition: changes vocabSet, defSet, and vocab.
	private void setOrderToStudy()
	{
		ArrayList<String> vocabSet = new ArrayList<String>();
		ArrayList<String> defSet = new ArrayList<String>();
		String vocab;
		
		//Add higher priority vocabs first.
		for (int index = 0; index < flashFile.getPriorityCount(); index++)
		{
			vocab = flashFile.getPriorityVocab(index);
			vocabSet.add(vocab);
			defSet.add(flashFile.getDefOf(vocab));
		}
		
		//Then add the ones that are not in knownFile.
		for (int index = 0; index < flashFile.getCount(); index++)
		{
			vocab = flashFile.getVocab(index);
			if (!flashFile.findKnownWord(vocab) && !vocabSet.contains(vocab))
			{
				vocabSet.add(vocab);
				defSet.add(flashFile.getDefinition(index));
			}
		}
		//Converts them to string[].
		//Need to check the argument. POSSIBLE INDEXING ERROR.
		vocabOrder = vocabSet.toArray(new String[0]);
		defOrder = defSet.toArray(new String[0]);
		sizeVocab = vocabSet.size();
	}
	
	private void setUpSlides() {
		studyMain = new JPanel(new BorderLayout());
		studyMain.add(descriptionText, BorderLayout.NORTH);
		studyPanel = new JPanel(new GridBagLayout());
		studyPanel.setBackground(new Color(20,20,20));
		studyMain.add(studyPanel, BorderLayout.CENTER);
		vocabDefNext = new JButton("Next");
		vocabDefNext.setFont(new Font("Arial", Font.PLAIN, 15));
		vocabDefNext.addActionListener(this);
		toolbar.removeAll();
		toolbar.add(mainMenuButton);
		toolbar.add(vocabDefNext);
		studyMain.add(toolbar, BorderLayout.SOUTH);
		
		vocabDefButton = new JTextArea[sizeVocab];
		vocabDefScroll = new JScrollPane[sizeVocab];
		for (int i = 0; i < sizeVocab; i++) {
			//TextArea
			vocabDefButton[i] = new JTextArea(vocabOrder[i]);
			vocabDefButton[i].setFont(new Font("Arial", Font.PLAIN, 19));
			vocabDefButton[i].setOpaque(true);
			vocabDefButton[i].setLineWrap(true);
			vocabDefButton[i].setWrapStyleWord(true);
			vocabDefButton[i].setBorder(BorderFactory.createLineBorder(Color.BLACK));
			vocabDefButton[i].setBackground(new Color(40,40,40));
			vocabDefButton[i].setForeground(Color.white);
			vocabDefButton[i].setEditable(false);
			vocabDefButton[i].setHighlighter(null);
			vocabDefButton[i].setCaretPosition(0);
			vocabDefButton[i].addMouseListener(this);
			//Adding Scroll
			vocabDefScroll[i] = new JScrollPane(vocabDefButton[i]);
			vocabDefScroll[i].setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			vocabDefScroll[i].setPreferredSize(new Dimension(vocabDefSize,vocabDefSize));
			
		}
	}
	private void setResizableFrame() {
		//Add Component listener 
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				Dimension d = getSize();
//				System.out.println(d);
				int width = (int)Math.round(d.getWidth() * (double)vocabDefSize/(double)vocabDefFrameWidth);
				int height = (int)Math.round(d.getHeight() * (double)vocabDefSize/(double)vocabDefFrameHeight);
				Dimension newD = new Dimension(width,height);
				for (int i = 0; i < sizeVocab; i++) {
					vocabDefScroll[i].setPreferredSize(newD);
				}
			}
		});
	}
	private boolean setUpPanel(int indexOfSetUp) {
		//First check to see if we're not done with the number of vocabularies.
		if (indexOfSlide < sizeVocab) {
			if (studyPanel.getComponentCount() == 0) {
				gc.gridx = 0;
				gc.gridy = 0;
				gc.insets = new Insets(20, 10, 0, 20);
				studyPanel.add(vocabDefScroll[indexOfSetUp], gc);
				descriptionText.setText(message[0]);
			}
			else if (studyPanel.getComponentCount() == 1) {
				gc.gridx = 1;
				gc.gridy = 0;
				gc.insets = new Insets(20, 0, 0, 20);
				studyPanel.add(vocabDefScroll[indexOfSetUp], gc);
				descriptionText.setText(message[0]);
			}
			else if (studyPanel.getComponentCount() == 2) {
				gc.gridx = 2;
				gc.gridy = 0;
				gc.insets = new Insets(20, 0, 0, 10);
				studyPanel.add(vocabDefScroll[indexOfSetUp], gc);
				descriptionText.setText(message[0]);
			}
			else if (studyPanel.getComponentCount() == 3) {
				if (!shuffled) {
					ArrayList<Integer> shuffleList = new ArrayList<Integer>();
					shuffleList.add(indexOfSlide-3);
					shuffleList.add(indexOfSlide-2);
					shuffleList.add(indexOfSlide-1);
					shuffle(shuffleList);
				}
				else {
					gc.gridx = 0;
					gc.gridy = 1;
					gc.insets = new Insets(20, 10, 20, 20);
					studyPanel.add(vocabDefScroll[indexOfSetUp], gc);
					descriptionText.setText(message[0]);
					shuffled = false;
					shouldIncrement = true;
				}
			}
			else if (studyPanel.getComponentCount() == 4) {
				gc.gridx = 1;
				gc.gridy = 1;
				gc.insets = new Insets(20, 0, 20, 20);
				studyPanel.add(vocabDefScroll[indexOfSetUp], gc);
				descriptionText.setText(message[0]);
			}
			else if (studyPanel.getComponentCount() == 5) {
				if (!shuffled) {
					ArrayList<Integer> shuffleList = new ArrayList<Integer>();
					shuffleList.add(indexOfSlide-5);
					shuffleList.add(indexOfSlide-4);
					shuffleList.add(indexOfSlide-3);
					shuffleList.add(indexOfSlide-2);
					shuffleList.add(indexOfSlide-1);
					shuffle(shuffleList);
				}
				else {
					gc.gridx = 2;
					gc.gridy = 1;
					gc.insets = new Insets(20, 0, 20, 10);
					studyPanel.add(vocabDefScroll[indexOfSetUp], gc);
					descriptionText.setText(message[0]);
					shuffled = false;
					shouldIncrement = true;
				}
			}
			else if (studyPanel.getComponentCount() == 6) {
				if (!shuffled) {
					ArrayList<Integer> shuffleList = new ArrayList<Integer>();
					shuffleList.add(indexOfSlide-6);
					shuffleList.add(indexOfSlide-5);
					shuffleList.add(indexOfSlide-4);
					shuffleList.add(indexOfSlide-3);
					shuffleList.add(indexOfSlide-2);
					shuffleList.add(indexOfSlide-1);
					shuffle(shuffleList);
				}
				else {
					studyPanel.removeAll();
					gc.gridx = 0;
					gc.gridy = 0;
					gc.insets = new Insets(20, 10, 0, 20);
					studyPanel.add(vocabDefScroll[indexOfSlide-3], gc);
					gc.gridx = 1;
					gc.gridy = 0;
					gc.insets = new Insets(20, 0, 0, 20);
					studyPanel.add(vocabDefScroll[indexOfSlide-2], gc);
					gc.gridx = 2;
					gc.gridy = 0;
					gc.insets = new Insets(20, 0, 0, 10);
					studyPanel.add(vocabDefScroll[indexOfSlide-1], gc);
					gc.gridx = 0;
					gc.gridy = 1;
					gc.insets = new Insets(20, 10, 20, 20);
					studyPanel.add(vocabDefScroll[indexOfSetUp], gc);
					descriptionText.setText(message[0]);
					shuffled = false;
					shouldIncrement = true;
				}
			}
		} 
		else if (!endShown){
			shouldIncrement = false;
			endShown = true;
			studyPanel.removeAll();
			
			//TextField
			JTextField doneField = new JTextField("That's all! Good Job, you deserve a break!");
			doneField.setFont(new Font("Arial", Font.PLAIN, 20));
			doneField.setOpaque(false);
			doneField.setBorder(BorderFactory.createEmptyBorder());
			doneField.setForeground(Color.white);
//			doneField.setBackground(new Color(0,0,0,0));
			doneField.setEditable(false);
			studyPanel.add(doneField);
			
			//Restart button
			retestButton = new JButton("Retest");
			retestButton.setFont(new Font("Arial", Font.PLAIN, 15));
			retestButton.addActionListener(this);
			toolbar.add(retestButton);
		}
		return shouldIncrement;
	}
	//Shuffle on 3,5,6 numOnScreen
	private void shuffle(ArrayList<Integer> components) {
		ArrayList<Integer> shuffleComponents = components;
		int count = 0;
		while (shuffleComponents.equals(components) && count < 10) {
			Collections.shuffle(shuffleComponents);
			count++;
		}
		studyPanel.removeAll();
		for (int i = 0; i < shuffleComponents.size(); i++) {
			setUpPanel(shuffleComponents.get(i));
			shuffled = true;
			shouldIncrement = false;
			descriptionText.setText(message[1]);
		}
	}
	private void flipToVocab() {
		int lowerBound = indexOfSlide - 6;
		if (lowerBound < 0)
			lowerBound = 0;
		while (lowerBound <= indexOfSlide && indexOfSlide < sizeVocab) {
			vocabDefButton[lowerBound].setText(vocabOrder[lowerBound]);
//			vocabDefButton[lowerBound].setForeground(new Color(153,0,0));
			vocabDefButton[lowerBound].setForeground(Color.white);
			vocabDefScroll[lowerBound].setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			lowerBound++;
		}
		
	}
	
	public void actionPerformed(ActionEvent e) {
		if (vocabDefNext == e.getSource()) {
			if (setUpPanel(indexOfSlide))
				indexOfSlide++;
			flipToVocab();
			repaint();
			revalidate();
		}
		else if (mainMenuButton == e.getSource())
		{
			new MainMenu(flashFile, this.getContentPane());
			dispose();
		}
		//Go to knownVocabs panel
		else if (nextButton1 == e.getSource())
		{
			flashFile.clearKnown();
			storeKnownVocabs("Known");		//Stores selected checkboxes in file.
			addKnownVocabs(2);					//Set up check panel for priority study file.
			cardLayout.show(masterPanel, cardPanel[0]);
		}
		//Go to study panel
		else if (nextButton2 == e.getSource())
		{
			flashFile.clearPriority();
			storeKnownVocabs("Priority");		//Stores selected checkboxes in file.
			setOrderToStudy();					//Set up the order of vocabs and defs to display.  Call once
			setUpSlides();						//Set up the slides. Call once.
			setSize(vocabDefFrameWidth, vocabDefFrameHeight);
			setMinimumSize(new Dimension(vocabDefFrameWidth,vocabDefFrameHeight));
			setLocationRelativeTo(null);
			if (setUpPanel(indexOfSlide))
				indexOfSlide++;
			setResizableFrame();
			masterPanel.add(studyMain, cardPanel[1]);
			cardLayout.show(masterPanel, cardPanel[1]);
		}
		else if (clearAllButton == e.getSource()) {
			for (int index = 0; index < vocabCheckBox.length; index++ ) {
				vocabCheckBox[index].setSelected(false);
			}
		}
		//Restart study
		else if (retestButton == e.getSource()) {
			new Study(flashFile, this.getContentPane());
			dispose();
		}
	}
	public void mousePressed(MouseEvent e) {
		for (int index = 0; index < sizeVocab; index++) {
			if (vocabDefButton[index] == e.getSource()) {
				if (vocabDefButton[index].getText().equals(vocabOrder[index])) {
					vocabDefButton[index].setText(defOrder[index]);
					vocabDefButton[index].setForeground(Color.lightGray);
					vocabDefButton[index].setCaretPosition(0);
				}
				else if (vocabDefButton[index].getText().equals(defOrder[index])) {
					vocabDefButton[index].setText(vocabOrder[index]);
//					vocabDefButton[index].setForeground(new Color(153,0,0));
					vocabDefButton[index].setForeground(Color.lightGray);
					vocabDefButton[index].setCaretPosition(0);
				}
				
			}
		}
	}

	public void mouseClicked(MouseEvent e) {
	}
	public void mouseEntered(MouseEvent e) {
//		for (int index = 0; index < sizeVocab; index++) {
//			if (vocabDefButton[index] == e.getSource()) {
//				vocabDefButton[index].setText(defOrder[index]);
//				vocabDefButton[index].setForeground(Color.BLUE);
//				repaint();
//				revalidate();
//			}
//		}
	}
	public void mouseExited(MouseEvent e) {
//		for (int index = 0; index < sizeVocab; index++) {
//			if (vocabDefButton[index] == e.getSource()) {
//				vocabDefButton[index].setText(vocabOrder[index]);
//				vocabDefButton[index].setForeground(Color.BLACK);
//				repaint();
//				revalidate();
//			}
//		}
	}
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
