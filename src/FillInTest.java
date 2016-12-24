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
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;


public class FillInTest extends JFrame implements ActionListener{
	private static final long serialVersionUID = -6531954435437937712L;

	//Class required objects and other helper variables
	private FlashFile flashFile;
	private GridBagConstraints gc = new GridBagConstraints();
	private static final int WIDTH_OF_VIEWINGPANEL = 620;
	private static final int HEIGHT_OF_VIEWINGPANEL = 240;
	private static final int WIDTH_OF_ANSWERPANEL = 620;
	private static final int HEIGHT_OF_ANSWERPANEL = 240;
	
	
	//TopPanel
	private JTextField progressField;
	
	//MainPanel
	private JPanel mainPanel;	//Hold all the components
	private JTextArea viewingText; //Show vocabularies and definitions
	private JScrollPane viewingScroll; //Add Scroll option to viewingText.
	private MouseListener viewingMouseListener;
	private JTextArea answerText;	//Text area for user to type answer. 
	private JScrollPane answerScroll;	//Add scroll option to answerText.
	private JLayeredPane layeredPane;	//For answerScroll and enterBtn. 
	private boolean showingVocab; //True if the viewingTextis showing the vocab. False for definition.
	
	//Buttons
	private JPanel buttonPanel;
	private JButton mainMenuBtn;
	private JButton correctBtn;	
	private JButton incorrectBtn;
	private JButton skipBtn;
	private JButton enterBtn;
	private JButton retestBtn;
	
	//Class variables
	private Integer[] vocabOrder; //Order of vocabs
	private int currentVocabIndex = 0;	//The index of vocab
	private ArrayList<String> correctVocab = new ArrayList<String>();
	private ArrayList<String> incorrectVocab = new ArrayList<String>();
	private ArrayList<Integer> skipVocab = new ArrayList<Integer>();
	private boolean testFinished = false;
	private boolean hasSkipedVocabs = false;
	
	
	public FillInTest(FlashFile fFile, Container container) {
		super("Fill In Test");
		flashFile = fFile;
		vocabOrder = HelperClass.scrambleVocab(flashFile);
		setLayout(new BorderLayout());
		
////////////TopPanel
		progressField = new JTextField("Progress: " 
				+ (currentVocabIndex+1) + "/" + flashFile.getCount());
		progressField.setEditable(false);
		progressField.setOpaque(true);
		progressField.setHorizontalAlignment(JTextField.CENTER);
		progressField.setBackground(new Color(255,255,220));
		progressField.setFont(new Font("Monospaced", Font.PLAIN, 13));
		progressField.setBorder(BorderFactory.createLineBorder(new Color(255,255,220)));
		
		add(progressField, BorderLayout.NORTH);
////////////MainPanel
		mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBackground(new Color(255,255,220));
		
		//Viewing Panel for showing vocabs and definition.
		//This doesn't add the vocabs or definitions. 
		setUpViewingText();
		
		//Area to write your answer. 
		setUpAnswerText();
		
		//Buttons
		setUpButtonPanel();
		
		add(mainPanel, BorderLayout.CENTER);
/////////////Settings of Frame.
		setSize(650,600);
		setResizable(false);
		setLocationRelativeTo(container);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	private void setUpViewingText() {
		//ViewingMouseListener
		viewingMouseListener = new MouseAdapter() {	
			@Override
			public void mousePressed(MouseEvent e) {
				//Change to Show Definition
				if (!testFinished) {
					if (viewingText.getText().equals(flashFile.getVocab(vocabOrder[currentVocabIndex]))) {
						setViewingTextAsDefinition();
					} else {
						setViewingTextAsVocab();
					}
				} else if (hasSkipedVocabs) {
					if (viewingText.getText().equals(flashFile.getVocab(skipVocab.get(0)))) {
						setViewingTextAsDefinition();
					} else {
						setViewingTextAsVocab();
					}
				}
				repaint();
				revalidate();
			}
		};
		
		//ViewingText
		viewingText = new JTextArea(flashFile.getVocab(vocabOrder[0]));
		showingVocab = true;
		viewingText.setFont(new Font("Arial", Font.PLAIN, 18));
		viewingText.setLineWrap(true);
		viewingText.setWrapStyleWord(true);
		viewingText.setOpaque(true);
		viewingText.setBackground(new Color(40,40,40));
		viewingText.setForeground(Color.white);
		viewingText.setEditable(false);
		viewingText.setHighlighter(null);
		viewingText.setCaretPosition(0);
		viewingText.addMouseListener(viewingMouseListener);
		viewingText.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEmptyBorder(4, 4, 4, 4), "Vocabulary - Click to Flip",
				TitledBorder.CENTER, TitledBorder.ABOVE_TOP,
				new Font("Tahoma", Font.PLAIN, 11), Color.WHITE));
		
		//Adding Scroll
		viewingScroll = new JScrollPane(viewingText);
		viewingScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		viewingScroll.setPreferredSize(new Dimension(WIDTH_OF_VIEWINGPANEL,HEIGHT_OF_VIEWINGPANEL));
		
		//Add to MainPanel
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 0;
		gc.weighty = 1;
		gc.gridwidth = 3;
		gc.insets = new Insets(0, 0, 0, 0);
		gc.anchor = GridBagConstraints.CENTER;
		mainPanel.add(viewingScroll, gc);
	}
	
	private void setUpAnswerText() {
		//Key Listener
		KeyListener answerKeyListener = new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				repaint();
				revalidate();
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				//Tab for Answer
//				if (e.getKeyCode() == KeyEvent.VK_TAB) {
//					enterBtn.doClick();
//				}
				repaint();
				revalidate();
			}
		};
		
		//AnswerText Action
		Action answerTextAction = new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 2468496948688219596L;
			@Override
			public void actionPerformed(ActionEvent e) {
				if (showingVocab) {
					setViewingTextAsDefinition();
					answerText.setBorder(BorderFactory.createTitledBorder(
							BorderFactory.createEmptyBorder(4, 4, 4, 4), "Compare Your Answers - Correct, Incorrect, or Skip to Come Back Later",
							TitledBorder.CENTER, TitledBorder.ABOVE_TOP));
				} else if (!showingVocab) {
					setViewingTextAsVocab();
					answerText.setBorder(BorderFactory.createTitledBorder(
							BorderFactory.createEmptyBorder(4, 4, 4, 4), "Type Your Guess - Press Shift Enter to Check Answer",
							TitledBorder.CENTER, TitledBorder.ABOVE_TOP));
				}
				enterBtn.doClick();
				revalidate();
				repaint();
			}
		};
		
		//AnswerText
		answerText = new JTextArea();
		answerText.setFont(new Font("Arial", Font.PLAIN, 18));
		answerText.setLineWrap(true);
		answerText.setWrapStyleWord(true);
		answerText.setBackground(Color.white);
		answerText.addKeyListener(answerKeyListener);
		answerText.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEmptyBorder(4, 4, 4, 4), "Type Your Guess - Press Shift Enter to Check Answer",
				TitledBorder.CENTER, TitledBorder.ABOVE_TOP));
		
		//Key Binding Shift-Enter
		answerText.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.SHIFT_DOWN_MASK), "tab");
		answerText.getActionMap().put("tab", answerTextAction);
		
		//Adding Scroll
		answerScroll = new JScrollPane(answerText);
		answerScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		answerScroll.setBounds(0, 0, WIDTH_OF_ANSWERPANEL, HEIGHT_OF_ANSWERPANEL);
		
		//Enter Button
		enterBtn = new JButton("Shift Enter");
		enterBtn.setBackground(Color.LIGHT_GRAY);
		enterBtn.setBounds(WIDTH_OF_ANSWERPANEL - 120, HEIGHT_OF_ANSWERPANEL -40, 100, 30);
//		enterBtn.addActionListener(answerTextAction);
		
		//Layer answerScroll and enterButton
		layeredPane = new JLayeredPane();
		layeredPane.add(enterBtn, JLayeredPane.TOP_ALIGNMENT);		//TOP
		layeredPane.add(answerScroll, JLayeredPane.BOTTOM_ALIGNMENT);	//BOTTOM
		layeredPane.setPreferredSize(new Dimension(WIDTH_OF_ANSWERPANEL,HEIGHT_OF_ANSWERPANEL));
		
		//Add to MainPanel
		gc.gridx = 0;
		gc.gridy = 1;
		gc.weightx = 0;
		gc.weighty = 5;
		gc.gridwidth = 3;
		gc.insets = new Insets(0, 0, 0, 0);
		gc.anchor = GridBagConstraints.CENTER;
		mainPanel.add(layeredPane, gc);
	}

	@SuppressWarnings("serial")
	private void setUpButtonPanel() {
		//Actions 
		Action correctAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (!testFinished) {
					correctVocab.add(flashFile.getVocab(vocabOrder[currentVocabIndex]));
					loadNextVocab();
				} else if (hasSkipedVocabs) {
					correctVocab.add(flashFile.getVocab(skipVocab.remove(0)));
					loadSkipVocab();
				}
				revalidate();
				repaint();
			}
		};
		Action incorrectAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (!testFinished) {
					incorrectVocab.add(flashFile.getVocab(vocabOrder[currentVocabIndex]));
					loadNextVocab();
				} else if (hasSkipedVocabs) {
					incorrectVocab.add(flashFile.getVocab(skipVocab.remove(0)));
					loadSkipVocab();
				}
				revalidate();
				repaint();
			}
		};
		Action skipAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (!testFinished) {
					skipVocab.add(vocabOrder[currentVocabIndex]);
					loadNextVocab();
				} else if (hasSkipedVocabs) {
					skipVocab.add(skipVocab.get(0));
					skipVocab.remove(0);
					loadSkipVocab();
				}
				revalidate();
				repaint();
			}
		};
		
		//Set up Buttons
		mainMenuBtn = HelperClass.getMainMenuButton();
		mainMenuBtn.addActionListener(this);
		
		correctBtn = new JButton();
		correctBtn.setFont(new Font("Arial", Font.PLAIN, 15));
		correctBtn.setText("Correct F1");
		correctBtn.setToolTipText("You answered correctly");
		correctBtn.addActionListener(correctAction);
		correctBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke("F1"), "correctAction");
		correctBtn.getActionMap().put("correctAction", correctAction);
		
		incorrectBtn = new JButton();
		incorrectBtn.setFont(new Font("Arial", Font.PLAIN, 15));
		incorrectBtn.setText("Incorrect F2");
		incorrectBtn.setToolTipText("You answered incorrectly");
		incorrectBtn.addActionListener(incorrectAction);
		incorrectBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke("F2"), "incorrectAction");
		incorrectBtn.getActionMap().put("incorrectAction", incorrectAction);
		
		skipBtn = new JButton();
		skipBtn.setFont(new Font("Arial", Font.PLAIN, 15));
		skipBtn.setText("Skip F3");
		skipBtn.setToolTipText("Come back to the question later");
		skipBtn.addActionListener(this);
		skipBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke("F3"), "skipAction");
		skipBtn.getActionMap().put("skipAction", skipAction);
		
		retestBtn = new JButton();
		retestBtn.setFont(new Font("Arial", Font.PLAIN, 15));
		retestBtn.setText("Retest");
		retestBtn.setToolTipText("Click to retake the test");
		retestBtn.addActionListener(this);
		retestBtn.setEnabled(false);
		
		//Add MainMenu Button to MainPanel
		gc.gridx = 0;
		gc.gridy = 2;
		gc.weightx = .5;
		gc.weighty = 0;
		gc.gridwidth = 1;
		gc.insets = new Insets(0, 10, 0, 0);
		gc.anchor = GridBagConstraints.WEST;
		mainPanel.add(mainMenuBtn, gc);
		
		//Set Up Button Panel
		buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
		buttonPanel.setBackground(new Color(255,255,220));
		buttonPanel.add(correctBtn);
		buttonPanel.add(incorrectBtn);
		buttonPanel.add(skipBtn);
		
		//Add Button Panel to Main Panel.
		gc.gridx = 1;
		gc.gridy = 2;
		gc.weightx = .5;
		gc.weighty = 0;
		gc.gridwidth = 1;
		gc.insets = new Insets(0, 0, 0, 0);
		gc.anchor = GridBagConstraints.CENTER;
		mainPanel.add(buttonPanel,gc);
		
		//Add Retest Button to MainPanel
		gc.gridx = 2;
		gc.gridy = 2;
		gc.weightx = .5;
		gc.weighty = 0;
		gc.gridwidth = 1;
		gc.insets = new Insets(0, 0, 0, 10);
		gc.anchor = GridBagConstraints.EAST;
		mainPanel.add(retestBtn, gc);
	}
	
	private void loadNextVocab() {
		currentVocabIndex++;
		//Main test not finished.
		if (currentVocabIndex < flashFile.getCount()) {
			setViewingTextAsVocab();
			setAnswerTextAsEmpty();
			progressField.setText("Progress: " 
					+ (currentVocabIndex+1) + "/" + flashFile.getCount());
		} else if (skipVocab.size() > 0) {
			//Main test is finished but has skipped vocabs to come back and review.
			progressField.setText("Going Over Skipped: " 
					+ skipVocab.size() + " left");
			progressField.setForeground(Color.RED);
			viewingText.setText(flashFile.getVocab(skipVocab.get(0)));
			setAnswerTextAsEmpty();
			hasSkipedVocabs = true;
			testFinished = true;
		} else {
			//Main test is done, and no more skips. 
			testFinished = true;
			hasSkipedVocabs = false;
			loadFinishedScreen();
		}
	}
	
	private void loadSkipVocab() {
		//Still has skipped vocabs, load next skipped vocab.
		if (skipVocab.size() > 0) {
			progressField.setText("Going Over Skipped: " 
					+ skipVocab.size() + " left");
			viewingText.setText(flashFile.getVocab(skipVocab.get(0)));
			setAnswerTextAsEmpty();
		} else {
			//No more skipped vocabs, end test. 
			hasSkipedVocabs = false;
			loadFinishedScreen();
		}
	}
	
	private void loadFinishedScreen() {
		//Show scores
		progressField.setText("Correct: " + correctVocab.size() + " Incorrect: " + incorrectVocab.size()
				+ " Grade: " + HelperClass.getPercentCorrect(correctVocab.size(), correctVocab.size() + incorrectVocab.size()));
		progressField.setForeground(Color.BLACK);
		setAnswerTextAsEmpty();
		
		//Set to finished screen.
		viewingText.setText("Test has been completed.");
		answerText.setEnabled(false);
		enterBtn.setEnabled(false);
		retestBtn.setEnabled(true);
		
		//Show screen for corrects and in-corrects.
		HelperClass.showFinishedTestPage(correctVocab, incorrectVocab, this, flashFile);
	}
	
	private void setViewingTextAsVocab() {
		//Change to Show Vocabulary
		viewingText.setForeground(Color.WHITE);
		viewingText.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEmptyBorder(4, 4, 4, 4), "Vocabulary - Click to Flip",
				TitledBorder.CENTER, TitledBorder.ABOVE_TOP,
				new Font("Tahoma", Font.PLAIN, 11), Color.WHITE));
		if (!testFinished) {
			viewingText.setText(flashFile.getVocab(vocabOrder[currentVocabIndex]));
		} else if (hasSkipedVocabs){
			viewingText.setText(flashFile.getVocab(skipVocab.get(0)));
		} 
		showingVocab = true;
	}
	private void setViewingTextAsDefinition() {
		//Change to Show Definition
		viewingText.setForeground(Color.LIGHT_GRAY);
		viewingText.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEmptyBorder(4, 4, 4, 4), "Definition - Click to Flip",
				TitledBorder.CENTER, TitledBorder.ABOVE_TOP,
				new Font("Tahoma", Font.PLAIN, 11), Color.WHITE));
		if (!testFinished) {
			viewingText.setText(flashFile.getDefinition(vocabOrder[currentVocabIndex]));
		} else if (hasSkipedVocabs){
			viewingText.setText(flashFile.getDefinition(skipVocab.get(0)));
		}
		showingVocab = false;
	}
	private void setAnswerTextAsEmpty() {
		answerText.setText("");
		answerText.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEmptyBorder(4, 4, 4, 4), "Type Your Guess - Press Shift Enter to Check Answer",
				TitledBorder.CENTER, TitledBorder.ABOVE_TOP));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton actionButton = (JButton)e.getSource();
		
		if (correctBtn == actionButton) {
			if (!testFinished) {
				correctVocab.add(flashFile.getVocab(vocabOrder[currentVocabIndex]));
				loadNextVocab();
			} else if (hasSkipedVocabs) {
				correctVocab.add(flashFile.getVocab(skipVocab.remove(0)));
				loadSkipVocab();
			}
		} else if (incorrectBtn == actionButton) {
			if (!testFinished) {
				incorrectVocab.add(flashFile.getVocab(vocabOrder[currentVocabIndex]));
				loadNextVocab();
			} else if (hasSkipedVocabs) {
				incorrectVocab.add(flashFile.getVocab(skipVocab.remove(0)));
				loadSkipVocab();
			}
		} else if (skipBtn == actionButton) {
			if (!testFinished) {
				skipVocab.add(vocabOrder[currentVocabIndex]);
				loadNextVocab();
			} else if (hasSkipedVocabs) {
				skipVocab.add(skipVocab.get(0));
				skipVocab.remove(0);
				loadSkipVocab();
			}
		} else if (mainMenuBtn == actionButton) {
			int popUpMsg = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?\n You will lose all your progress.",
					"Exit?", JOptionPane.YES_NO_OPTION);
			if (popUpMsg == JOptionPane.YES_OPTION) {
				new MainMenu(flashFile, this.getContentPane());
				dispose();
			}
		} else if (retestBtn == actionButton) {
			new FillInTest(flashFile, this.getContentPane());
			dispose();
		}
		
		//Redraw the components to update changes
		revalidate();
		repaint();
	}



}
