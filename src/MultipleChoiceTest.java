import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
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

@SuppressWarnings("serial")
public class MultipleChoiceTest extends JFrame implements ActionListener{
	
/////////////////Class variables//////////////
	private FlashFile flashFile;
	private Integer numOfButtons; 
	private static final int FRAME_WIDTH = 800; 
	private static final int FRAME_HEIGHT = 600;
	
	
/////////////////Test Page////////////////////
	//mainPanel
	private JPanel mainPanel;
	private JScrollPane scroll;
	
	//Test Panel
	private JPanel[] testPanel;
	private JTextArea[] vocabQuestions;
	private JRadioButton[][] answers;
	private ButtonGroup[] group;
	
	private Integer[] vocabOrder;
	private Integer[] correctButton;
	private Integer[][] wrongButton;
	private String[][] wrongDef;
	
	//donePanel
	private JPanel testDonePanel;
	private JButton mainMenuButton;
	private JButton doneButton;
	
	
///////////////////Done Page/////////////////////////////////////
	
	//Result Panel
	private JPanel resultPanel;
	private JPanel resultDonePanel;
	private JTextField resultField;
	private JButton retestButton;
	private ArrayList<Integer> wrongQuestions = new ArrayList<Integer>();
	
	public MultipleChoiceTest(FlashFile fFile, Container container){
		super("Multiple Choice Test");
		setLayout(new BorderLayout());
		flashFile = fFile;
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		//Add test.
		addTest();
		//Add donePanel;
		addDonePanel();
		
		//Add mainPanel to frame w/ scroll
		scroll = new JScrollPane(mainPanel);
		vocabQuestions[0].setCaretPosition(0);
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(scroll, BorderLayout.CENTER);
		
		//Settings of frame.
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		setLocationRelativeTo(container);
		setVisible(true);
	}
	
	//adds all the multiple choice questions.
	protected void addTest(){
		//Initialize for adding test panels.
		if (flashFile.getCount() >= 4)
			numOfButtons = 4;
		else
			numOfButtons = flashFile.getCount();
		testPanel = new JPanel[flashFile.getCount()];
		vocabQuestions = new JTextArea[flashFile.getCount()];
		answers = new JRadioButton[flashFile.getCount()][4];
		group = new ButtonGroup[flashFile.getCount()];
		vocabOrder = HelperClass.scrambleVocab(flashFile);
		correctButton = setScrambledAns();
		wrongButton = getWrongButtons();
		wrongDef = getWrongDef();
		
		for (int index = 0; index < flashFile.getCount(); index++)
		{
			//Panels.
			testPanel[index] = new JPanel(new GridLayout(0,1,0,0));
			
			//Questions.
			vocabQuestions[index] = new JTextArea();
			vocabQuestions[index].setOpaque(false);
			vocabQuestions[index].setEditable(false);
			vocabQuestions[index].setSelectedTextColor(null);
			vocabQuestions[index].setHighlighter(null);
			vocabQuestions[index].setLineWrap(true);
			vocabQuestions[index].setWrapStyleWord(true);
			vocabQuestions[index].setText(index + 1 + ". " + flashFile.getVocab(vocabOrder[index]));
			vocabQuestions[index].setFont(new Font("Arial", Font.PLAIN, 18));
			vocabQuestions[index].setForeground(new Color(153,0,0));
			testPanel[index].add(vocabQuestions[index]);

			//Buttons.
			answers[index][0] = new JRadioButton();
			answers[index][1] = new JRadioButton();
			answers[index][2] = new JRadioButton();
			answers[index][3] = new JRadioButton();
			answers[index][0].setFont(new Font("helvetica", Font.PLAIN, 16));
			answers[index][1].setFont(new Font("helvetica", Font.PLAIN, 16));
			answers[index][2].setFont(new Font("helvetica", Font.PLAIN, 16));
			answers[index][3].setFont(new Font("helvetica", Font.PLAIN, 16));
			answers[index][0].setBackground(new Color(255,255,220));
			answers[index][1].setBackground(new Color(255,255,220));
			answers[index][2].setBackground(new Color(255,255,220));
			answers[index][3].setBackground(new Color(255,255,220));
			answers[index][0].setFocusPainted(false);
			answers[index][1].setFocusPainted(false);
			answers[index][2].setFocusPainted(false);
			answers[index][3].setFocusPainted(false);

			if (numOfButtons >= 1)
				answers[index][correctButton[index]].setText(flashFile.getDefinition(vocabOrder[index]));
			if (numOfButtons >= 2)
				answers[index][wrongButton[index][0]].setText(wrongDef[index][0]);
			if (numOfButtons >= 3)
				answers[index][wrongButton[index][1]].setText(wrongDef[index][1]);
			if (numOfButtons >= 4)
				answers[index][wrongButton[index][2]].setText(wrongDef[index][2]);
			group[index] = new ButtonGroup();
			group[index].add(answers[index][0]);
			group[index].add(answers[index][1]);
			group[index].add(answers[index][2]);
			group[index].add(answers[index][3]);
			testPanel[index].add(answers[index][0]);
			testPanel[index].add(answers[index][1]);
			testPanel[index].add(answers[index][2]);
			testPanel[index].add(answers[index][3]);
			testPanel[index].setBackground(new Color(255,255,225));
			testPanel[index].setBorder(BorderFactory.createLineBorder(Color.lightGray, 1));


			//Add Panel
			mainPanel.add(testPanel[index]);
		}
		
	}
	private Integer[] setScrambledAns() {
		Integer[] temp = new Integer[flashFile.getCount()];
		Random random = new Random();
		for (int index = 0; index < flashFile.getCount(); index++)
		{
			temp[index] = random.nextInt(numOfButtons);
		}
		return temp;
	}
	private Integer[][] getWrongButtons(){
		Integer[][] temp = new Integer[flashFile.getCount()][3];
		
		for (int index = 0; index < flashFile.getCount(); index++)
		{
			for (int i = 0; i < 4; i++)
			{
				if (correctButton[index] == 0)
				{
					temp[index][0] = 1;
					temp[index][1] = 2;
					temp[index][2] = 3;
				}
				
				if (correctButton[index] == 1)
				{
					temp[index][0] = 0;
					temp[index][1] = 2;
					temp[index][2] = 3;
				}
				
				if (correctButton[index] == 2)
				{
					temp[index][0] = 0;
					temp[index][1] = 1;
					temp[index][2] = 3;
				}
				
				if (correctButton[index] == 3)
				{
					temp[index][0] = 0;
					temp[index][1] = 1;
					temp[index][2] = 2;
				}
			}
		}
		return temp;
	}
	private String[][] getWrongDef() {
		String temp[][] = new String[flashFile.getCount()][3];
		String correctString;
		String randomedString;
		boolean used;
		Random random = new Random();
		int countFilled;
		
		//For loop runs through each of the vocabularies
		for (int index = 0; index < flashFile.getCount(); index++)
		{
			//One correct answer. 
			correctString = flashFile.getDefinition(vocabOrder[index]);
			countFilled = 0;
			
			//Runs through each of the 4 possible answers and fills in with non-duplicating answer.
			while (countFilled < numOfButtons-1)
			{
				randomedString = flashFile.getDefinition(random.nextInt(flashFile.getCount()));
				used = false;
				
				if (randomedString == temp[index][0])
					used = true;
				if (randomedString == temp[index][1])
					used = true;
				if (randomedString == temp[index][2])
					used = true;
				if (randomedString == correctString)
					used = true;
				
				if (!used)
				{
//					randomedString = setWordWrap(randomedString);
					temp[index][countFilled] = randomedString;
					countFilled++;
				}
			}
		}
		
		return temp;
	}
	
//	private String setWordWrap(String word) {
//		StringBuilder wordWrap = new StringBuilder(word);
//		Integer startingIndex = 0;
//		Integer endingIndex = 0;
//		int targetIndex = 20;
//		int substringBegin = 0;
//	
//		wordWrap.insert(0, "-html-");
//		System.out.println(wordWrap);
//		while (targetIndex < word.length()) {
//			if (wordAt(word, targetIndex, startingIndex, endingIndex)) {
//				if (endingIndex - startingIndex > targetIndex)
//					targetIndex = wordWrap.indexOf(" ", startingIndex);
////		System.out.println("substringBegin: " + substringBegin);
//				else {
//					word = word.substring(substringBegin, startingIndex) + "-br-" + word.substring(startingIndex, word.length());
//					substringBegin += startingIndex - substringBegin + 4;
//				}
//			}
//			
//			
//			else {
//				word = word.substring(substringBegin, targetIndex) + "-br-" + word.substring(targetIndex+1, word.length());
//				substringBegin += targetIndex + 5;
//			}
//			
//			targetIndex += 24;
//		}
//		word = word + "-html-";
//		return wordWrap.toString();
//	}
//	
//	//Precondition: word is a valid String, targetIndex is valid.
//	//Postcondition: returns true if the targetIndex is part of a word, startingIndex and ending Index will be
//	//set to the values of the beginnning index inclusively and the endingindex exclusively. 
//	private boolean wordAt(String word, int targetIndex, Integer startingIndex, Integer endingIndex) {
//		//Check if its a space
//		if (word.charAt(targetIndex) == ' ' ) {
//			return false;
//		}
//		//The method continues if it's a word.
//		startingIndex = targetIndex;
//		
//		//Search backward to find the beginning of the word.
//		//Possible that the word reaches to index 0 where a space will never be found.
//		targetIndex--;
//		while (targetIndex >= 0 && word.charAt(startingIndex) != ' ') {
//			startingIndex = targetIndex;
//			targetIndex--;
//		}
//		if (targetIndex > 0)
//			startingIndex++;
//		
//		//Ending index;
//		endingIndex = word.indexOf(' ', startingIndex);
//		
//		System.out.println(word + "\n" + word.charAt(startingIndex) + " : " + startingIndex);
//		return true;
//	}
	
	protected void addDonePanel() {
		testDonePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		//Create buttons
		mainMenuButton = HelperClass.getMainMenuButton();
		doneButton = new JButton("Done");
		
		//Settings
		doneButton.setFont(new Font("Arial", Font.PLAIN, 15));
		doneButton.setMnemonic('D');
		doneButton.setToolTipText("Finished with test");
		doneButton.addActionListener(this);
		mainMenuButton.addActionListener(this);
		
		//Add
		testDonePanel.add(mainMenuButton);
		testDonePanel.add(doneButton);
		add(testDonePanel, BorderLayout.SOUTH);
	}

	
	private void addResultPanel() {
		//Results
		resultPanel = new JPanel();
		resultPanel.setLayout(new BorderLayout(0,10));
		
		//Text field.
		resultField = new JTextField();
		resultField.setFont(new Font("Arial", Font.PLAIN, 18));
		resultField.setText("Score: " + getCorrectAndWrongQuestions() + "/" + flashFile.getCount()
				+ " which is "	+ HelperClass.getPercentCorrect(getCorrectAndWrongQuestions(),flashFile.getCount()) + "%" );
		resultField.setEditable(false);
		resultPanel.add(resultField, BorderLayout.CENTER);
		
		add(resultPanel, BorderLayout.NORTH);
	}
	private void addResultDonePanel() {
		resultDonePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		//Buttons.
		retestButton = new JButton("Retest");
		retestButton.setFont(new Font("Arial", Font.PLAIN, 15));
		retestButton.setMnemonic('R');
		retestButton.setToolTipText("Take test again.");
		retestButton.addActionListener(this);
		
		//Add to done Panel
		resultDonePanel.add(mainMenuButton);
		resultDonePanel.add(retestButton);
		
		//Add to frame.
		add(resultDonePanel, BorderLayout.SOUTH);
	}
	private int getCorrectAndWrongQuestions() {
		int index = 0;
		int numCorrect= 0;
		while (index < flashFile.getCount())
		{
			if (answers[index][correctButton[index]].isSelected())
				numCorrect++;
			else
				wrongQuestions.add(index);
			index++;
		}
		return numCorrect;
	}
	@SuppressWarnings("unchecked")
	private void showCorrectAndWrong() {
		@SuppressWarnings("rawtypes")
		Map attributes = (new Font("Arial", Font.PLAIN, 18)).getAttributes();
		attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
		
		for (int index = 0; index < wrongQuestions.size(); index++)
		{
			vocabQuestions[wrongQuestions.get(index)].setFont(new Font(attributes));
		}
		
		for (int index = 0; index < flashFile.getCount(); index++)
		{
			answers[index][correctButton[index]].setFont(new Font("Arial", Font.BOLD, 18));
		}
	}
	
	
	public void actionPerformed(ActionEvent e){
		if (e.getSource() == mainMenuButton)
		{
			new MainMenu(flashFile, this.getContentPane());
			dispose();
		}
		else if (e.getSource() == doneButton)
		{
			remove(testDonePanel);
			addResultPanel();
			addResultDonePanel();
			showCorrectAndWrong();
			revalidate();
			repaint();
		}
		else if (e.getSource() == retestButton)
		{
			new MultipleChoiceTest(flashFile, this.getContentPane());
			dispose();
		}
		
	}

}
