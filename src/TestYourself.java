import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.Border;

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

public class TestYourself extends JFrame implements ActionListener{
	/**
	 *
	 */
	private static final long serialVersionUID = -2872433631743628321L;

	//Needed objects to use the class.
	private FlashFile flashFile;

	//Initialization for vocab questions and definitions will show.
	private JPanel questionPanel;		//BorderLayout.left
	private JPanel checkPanel;
	private JPanel answerPanel;			//BorderLayout.Right
	private JButton checkBtn;			//BorderLayout.center
	private JTextArea questionText;
	private JTextArea answerText;
	private JTextArea progressText;
	private String progressString;
	
	//Tool panel for the tests.			
	private JPanel toolPanel;			//BorderLayout.Bottom
	private JButton mainMenuBtn;
	private JButton correctBtn;
	private JButton wrongBtn;
	private JButton passedBtn;

	//Score variables
	private Integer[] vocabOrder;
	private int currentVocabNumber;
	private int correctNum;
	private int wrongNum;
	private String gradePercent;
	private boolean testFinished;
	private boolean reviewingSkipped;
	private ArrayList<String> correctVocabs = new ArrayList<String>();
	private ArrayList<String> incorrectVocabs = new ArrayList<String>();
	private ArrayList<Integer> passedVocabs = new ArrayList<Integer>();
	
	public TestYourself(FlashFile fFile, Container container) {
		super("Test Yourself!");
		setLayout(new BorderLayout());
		flashFile = fFile;
		
		//Set up the vocab order.
		vocabOrder = HelperClass.scrambleVocab(flashFile);
		correctNum = wrongNum = 0;
		currentVocabNumber = 1;
		testFinished = reviewingSkipped = false;
		
		//Set up the first default look.
		setUpInitialPanel();
	
		
		//Settings for frame.
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		setSize(900, 600);
		setSize(600,400);
//		setResizable(false);
		setLocationRelativeTo(container);
		setVisible(true);
	}


	private void setUpInitialPanel() {

		GridBagConstraints gc = new GridBagConstraints();
		Border innerBorderQuestion = BorderFactory.createTitledBorder("Vocabulary");
		Border outerBorder = BorderFactory.createEmptyBorder(5,5,5,5);
		Border innerBorderAns = BorderFactory.createTitledBorder("Answer");
		
///////////////////Question Panel
		questionPanel = new JPanel();
//		questionPanel.setPreferredSize(new Dimension(405,600));
		questionPanel.setPreferredSize(new Dimension(250,400));
		questionPanel.setLayout(new GridBagLayout());
		questionPanel.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorderQuestion));
		questionPanel.setBackground(new Color(255,255,220));
		
		questionText = new JTextArea();
		questionText.setText(flashFile.getVocab(vocabOrder[0]));
		questionText.setFont(new Font("Arial", Font.PLAIN, 16));
		questionText.setLineWrap(true);
		questionText.setWrapStyleWord(true);
//		questionText.setSize(370, 100);
		questionText.setSize(220, 300);
		questionText.setOpaque(true);
		//This is a replacement for opaque if it doens't work.
		questionText.setBorder(BorderFactory.createEmptyBorder());
		questionText.setBackground(new Color(255,255,225));
		questionText.setForeground(new Color(153,0,0));
		questionText.setEditable(false);
		
		questionPanel.add(questionText,gc);
		add(questionPanel, BorderLayout.WEST);
		
////////////////Check Panel
		checkPanel = new JPanel();
		checkPanel.setLayout(new GridBagLayout());
		checkPanel.setBackground(new Color(255,255,220));
		
		progressString = "Progress:\n" + currentVocabNumber +"/" + flashFile.getCount() 
				+"\n\nCorrect:\n" + correctNum + "\n\nWrong:\n" 
				+ wrongNum + "\n\nPassed:\n" + passedVocabs.size();
		progressText = new JTextArea();
		progressText.setText(progressString);
		progressText.setEditable(false);
		progressText.setOpaque(true);
		progressText.setBackground(new Color(255,255,220));
		progressText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 0;
		gc.weighty = 1;
		gc.anchor = GridBagConstraints.NORTH;
		gc.insets = new Insets(20,0,0,0);
		checkPanel.add(progressText,gc);
		
		checkBtn = new JButton();
		checkBtn.setText("Check 'Q'");
		checkBtn.setToolTipText("Click here to check ANSWER");
		checkBtn.setFont(new Font("Arial", Font.PLAIN, 12));
		checkBtn.addActionListener(this);
		checkBtn.setMnemonic(KeyEvent.VK_Q);
		
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 0;
		gc.weighty = 0;
		gc.anchor = GridBagConstraints.SOUTH;
		gc.insets = new Insets(0,0,0,0);
		checkPanel.add(checkBtn,gc);
		
		add(checkPanel, BorderLayout.CENTER);
		
/////////////////Answer Panel
		answerPanel = new JPanel();
//		answerPanel.setPreferredSize(new Dimension(405,600));
		answerPanel.setPreferredSize(new Dimension(250,400));
		answerPanel.setLayout(new GridBagLayout());
		answerPanel.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorderAns));
		answerPanel.setBackground(new Color(255,255,220));
		
		answerText = new JTextArea();
		answerText.setText("Click on 'Check Q' to see the definition!!");
		answerText.setFont(new Font("Arial", Font.PLAIN, 16));
		answerText.setOpaque(true);
		answerText.setLineWrap(true);
		answerText.setWrapStyleWord(true);
//		answerText.setSize(370, 100);
		answerText.setSize(220, 300);
		//This is a replacement for opaque if it doesn't work.
		answerText.setBorder(BorderFactory.createEmptyBorder());
		answerText.setBackground(new Color(255,255,220));
		answerText.setEditable(false);
//		answerText.setForeground(new Color(153,0,0));
		
		answerPanel.add(answerText);
//		add(new JScrollPane(answerPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER) , BorderLayout.EAST);
		add(answerPanel,BorderLayout.EAST);

///////////////////////Tool panel
		toolPanel = new JPanel();
		toolPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		toolPanel.setFocusable(true);
	
		mainMenuBtn = HelperClass.getMainMenuButton();
		mainMenuBtn.addActionListener(this);
		
		correctBtn = new JButton();
		correctBtn.setFont(new Font("Arial", Font.PLAIN, 15));
		correctBtn.setText("Correct 'W'");
		correctBtn.setToolTipText("Click this if you got the correct answer.");
		correctBtn.setMnemonic(KeyEvent.VK_W);
		correctBtn.addActionListener(this);
		
		wrongBtn = new JButton();
		wrongBtn.setFont(new Font("Arial", Font.PLAIN, 15));
		wrongBtn.setText("Wrong 'E'");
		wrongBtn.setToolTipText("Click this if you got the wrong answer.");
		wrongBtn.addActionListener(this);
		wrongBtn.setMnemonic(KeyEvent.VK_E);
		
		passedBtn = new JButton();
		passedBtn.setFont(new Font("Arial", Font.PLAIN, 15));
		passedBtn.setText("Pass 'R'");
		passedBtn.setToolTipText("Click this if you passed.");
		passedBtn.addActionListener(this);
		passedBtn.setMnemonic(KeyEvent.VK_R);

		toolPanel.add(mainMenuBtn, FlowLayout.LEFT);
		toolPanel.add(correctBtn);
		toolPanel.add(wrongBtn);
		toolPanel.add(passedBtn);
		add(toolPanel, BorderLayout.SOUTH);
	}
	private void refreshPage()
	{
		progressText.setText(progressString);
		revalidate();
		repaint();
	}
	private void loadNextPage(String vocabText)
	{
		questionText.setText(vocabText);
		answerText.setText("Click on 'Check Q' to see the definition!!");
	}
	private void checkCurrentNumberAndRefresh()
	{
		if (!testFinished && (currentVocabNumber < flashFile.getCount()))
		{
			currentVocabNumber++;
			progressString = "Progress:\n" + currentVocabNumber +"/" + flashFile.getCount() 
					+"\n\nCorrect:\n" + correctNum + "\n\nWrong:\n" 
					+ wrongNum + "\n\nPassed:\n" + passedVocabs.size();
			loadNextPage(flashFile.getVocab(vocabOrder[currentVocabNumber-1]));
		}
		else if (passedVocabs.size() > 0) 
		{
			reviewingSkipped = true;
			progressString = "Skipped\nRemaining:\n" + passedVocabs.size() 
					+"\n\nCorrect:\n" + correctNum + "\n\nWrong:\n" 
					+ wrongNum + "\n\nPassed:\n" + passedVocabs.size();
			loadNextPage(flashFile.getVocab(vocabOrder[passedVocabs.get(0)]));
		}
		else
		{
			questionText.setText("YOU'RE DONE!");
			gradePercent = "Grade: " + HelperClass.getPercentCorrect(correctNum, flashFile.getCount())
					+ "%";
			progressString = "Progress:\n" + "FINISHED"
					+"\n\nCorrect:\n" + correctNum + "\n\nWrong:\n" 
					+ wrongNum + "\n\nPassed:\n" + passedVocabs.size();
			answerText.setText(gradePercent);
			checkBtn.setEnabled(false);
			testFinished = true;
			reviewingSkipped = false;
			correctBtn.setEnabled(false);
			wrongBtn.setEnabled(false);
			passedBtn.setEnabled(false);
			
			HelperClass.showFinishedTestPage(correctVocabs, incorrectVocabs, this, flashFile);
			
		}
		refreshPage();
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == mainMenuBtn)
		{
			int popUpMsg = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?\n You will lose all your progress.",
					"Exit?", JOptionPane.YES_NO_OPTION);
			if (popUpMsg == JOptionPane.YES_OPTION) {
				new MainMenu(flashFile,this.getContentPane());
				dispose();
			}
		}
		else if(e.getSource() == correctBtn)
		{
			if (!testFinished) {
				correctNum++;
				if (!reviewingSkipped)
					correctVocabs.add(flashFile.getVocab(vocabOrder[currentVocabNumber-1]));
			} 
			if (reviewingSkipped) {
				correctVocabs.add(flashFile.getVocab(vocabOrder[passedVocabs.remove(0)]));
			}
			checkCurrentNumberAndRefresh();
		}
		else if(e.getSource() == wrongBtn)
		{
			if (!testFinished) {
				wrongNum++;
				if (!reviewingSkipped)
					incorrectVocabs.add(flashFile.getVocab(vocabOrder[currentVocabNumber-1]));
			}
			if (reviewingSkipped) {
				incorrectVocabs.add(flashFile.getVocab(vocabOrder[passedVocabs.remove(0)]));
			}
			checkCurrentNumberAndRefresh();
		}
		else if(e.getSource() == passedBtn)
		{
			if (!testFinished && !reviewingSkipped) {
				passedVocabs.add(currentVocabNumber-1);
			} else if (!testFinished && reviewingSkipped) {
				passedVocabs.add(passedVocabs.get(0));
			}
			if (reviewingSkipped) {
				passedVocabs.remove(0);
			}
			checkCurrentNumberAndRefresh();
		}
		else if(e.getSource() == checkBtn)
		{
			if (!testFinished && !reviewingSkipped) {
				answerText.setText(flashFile.getDefinition(vocabOrder[currentVocabNumber-1]));
			}
			else if (!testFinished && reviewingSkipped) {
				answerText.setText(flashFile.getDefinition(vocabOrder[passedVocabs.get(0)]));
			}
			refreshPage();
		}

		
	}
	
}
