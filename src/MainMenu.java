import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

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

public class MainMenu extends JFrame implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7059623930798035683L;
	//Menu buttons
	private JButton studyButton;
	private JButton testButton;
	private JButton fillInAnsButton;
	private JButton multipleChoiceButton;
	private JButton loadVocabButton;
	
	//Toolbar buttons and panel
	private JPanel toolPanel;
	private JButton helpButton;
	private JButton exitButton;
	
	//Data storage
	private FlashFile flashFile;
	
	public MainMenu() {
		//Creating a Frame with title
		super("Moe's Flashcard");
		flashFile = new FlashFile();
		initializeGui();
	}
	
	public MainMenu(FlashFile file, Container container) {
        //Creating a Frame with title
        super(HelperClass.removeFileExtension(file.getFileName()));
        this.flashFile = file;
        initializeGui();
        setLocationRelativeTo(container);
    }
	
	public void initializeGui() {
		//Design of Frame
		setLayout(new GridLayout(6,1));
	
		//Add menu buttons
		addButtons();
		//Add tool bar
		addToolPanel();

		//Settings of Frame
		setDefaultLookAndFeelDecorated(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(350, 400);
		setLocationRelativeTo(null);
		setVisible(true);
		setResizable(false);
	}

	private void addButtons() {
		//Initialize buttons
		studyButton = new JButton("Study");
		testButton = new JButton("Test Yourself");
		fillInAnsButton = new JButton("Fill In Test");
		multipleChoiceButton = new JButton("Multiple Choice Test");
		loadVocabButton = new JButton("Load Your Vocabs");
	
		//Settings of buttons
		studyButton.setFont(new Font("Arial", Font.PLAIN, 15));
		testButton.setFont(new Font("Arial", Font.PLAIN, 15));
		fillInAnsButton.setFont(new Font("Arial", Font.PLAIN, 15));
		multipleChoiceButton.setFont(new Font("Arial", Font.PLAIN, 15));
		loadVocabButton.setFont(new Font("Arial", Font.PLAIN, 15));
		
		studyButton.setMnemonic('S');
		testButton.setMnemonic('T');
		fillInAnsButton.setMnemonic('F');
		multipleChoiceButton.setMnemonic('M');
		loadVocabButton.setMnemonic('L');
		
		studyButton.setToolTipText("A study method for memorizing vocabs");
		testButton.setToolTipText("Test yourself how well you know");
		fillInAnsButton.setToolTipText("Type your answers in and check.");
		multipleChoiceButton.setToolTipText("Vocabularies mixed with other definitions");
		loadVocabButton.setToolTipText("Put your vocabs and definitions in here.");
		
		
		//When clicked, perform these
		studyButton.addActionListener(this);
		testButton.addActionListener(this);
		fillInAnsButton.addActionListener(this);
		multipleChoiceButton.addActionListener(this);
		loadVocabButton.addActionListener(this);
		
		//Add to frame	
		add(studyButton);
		add(testButton);
		add(fillInAnsButton);
		add(multipleChoiceButton);
		add(loadVocabButton);
	}
	
	private void addToolPanel(){
		toolPanel = new JPanel();
		
		//Make buttons
		helpButton = HelperClass.getHelpButton();
		exitButton = HelperClass.getExitButton();

		//Set layout.
		toolPanel.setLayout(new GridLayout());

		//When clicked, perform these
		helpButton.addActionListener(this);
		exitButton.addActionListener(this);
		
		//Add buttons to the toolbar
		toolPanel.add(helpButton);
		toolPanel.add(exitButton);
		
		//Add panel to frame
		add(toolPanel);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JButton temp = new JButton();
		temp = (JButton)e.getSource();

		//Study
		if(temp == studyButton){
			if (flashFile.getCount() > 0) {
				new Study(flashFile, this.getContentPane());
				dispose();
			}
			else
				HelperClass.showError("You haven't put any vocabs yet! Refer to the help menu for more information.");	
			
		}
		//Test yourself
		else if(temp == testButton){
			if (flashFile.getCount() > 0){
				new TestYourself(flashFile, this.getContentPane());
				dispose();
			}
			else
				HelperClass.showError("You haven't put any vocabs yet! Refer to the help menu for more information.");	
		}
		//Test yourself v2
		else if(temp == fillInAnsButton) {
			if (flashFile.getCount() > 0){
				new FillInTest(flashFile, this.getContentPane());
				dispose();
			}
			else
				HelperClass.showError("You haven't put any vocabs yet! Refer to the help menu for more information.");	
		}
		//Multiple choice test
		else if(temp == multipleChoiceButton){
			if (flashFile.getCount() > 0) {
				new MultipleChoiceTest(flashFile, this.getContentPane());
				dispose();
			}
			else 
				HelperClass.showError("You haven't put any vocabs yet! Refer to the help menu for more information.");	
		}
		//Load vocabs
		else if(temp == loadVocabButton){
			new LoadVocab(flashFile, this.getContentPane());
			dispose();
		}
		else if(temp == helpButton){
			HelperClass.printHelp();
		}
		else if(temp == exitButton){
			dispose();
		}
	}
	
}



	


