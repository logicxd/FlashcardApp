import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

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

public class LoadVocab extends JFrame implements KeyListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6004878791999358566L;
//FlashFile
	private FlashFile flashFile;
	
	
//Toolbar
	private JToolBar toolBar;
	private JButton newFileButton;
	private JButton openFileButton;
	private JButton saveButton;
	private JButton undoButton;
	private JButton redoButton;
	private JLabel selectedFileLabel;
	private final String selectedString = " Current File: "; //15 spaces from left
	private JLabel searchLabel;
	private JTextField findField;
	private JButton findBtn;
	private JButton invisFindBtn;
	private String newFileName;
	private JFileChooser fileChooser;
	
//Text area
	private JTextPane textPane;
	private StyledDocument doc;
	private SimpleAttributeSet attr = new SimpleAttributeSet();
	//Redo and Undo
	protected UndoManager undoManager = new UndoManager();
	private UndoAction undoAction = new UndoAction();
	private RedoAction redoAction = new RedoAction();
	//Find
	private AbstractAction findAction;
	Highlighter.HighlightPainter myHighlightPainter = new MyHighlightPainter(Color.LIGHT_GRAY);
	private int findCaretPosition = 0;

	
//South tool panel.	
	private JPanel southToolPanel;
	private JLabel comboBoxLabel;
	private JComboBox<Character> dropDownList;
	private Character[] separatorList = { ':', '=', '-'};
	private JButton loadButton;
	private JButton doneButton;
	
//Save-Load message panel.
	private JFrame alertFrame;
	private JTextArea alertText;
	
	public LoadVocab(FlashFile fName, Container container) {
		super("Your Vocabs");
		setLayout(new BorderLayout(0,0));
		flashFile = fName;
		//Add theme
		addLookAndFeel();
		
///////////////////Tool bar/////////////////////////
		toolBar = new JToolBar();
		toolBar.setFloatable(false);
		fileChooser = new JFileChooser(System.getProperty("user.dir"));
		fileChooser.setFileFilter(new FileFilter() {
			public boolean accept(File arg0) {
				boolean condition = false;
				String name = arg0.getName();
				String extension = HelperClass.getFileExtension(name);
				if (extension != null && extension.equals("vocab"))
					condition = true;
				return condition;
			}
			public String getDescription() {
				return "Moe's Flashcard (*.vocab)";
			}
		});
		
		//New file tab
		newFileButton = new JButton("New");
		newFileButton.setFont(new Font("Arial", Font.PLAIN, 15));
		newFileButton.setMnemonic(KeyEvent.VK_N);
		newFileButton.setToolTipText("Alt + N");
		newFileButton.addActionListener( e -> {
			newFileName = JOptionPane.showInputDialog(new JFrame(), "Enter file name: ", 
					"New File", JOptionPane.QUESTION_MESSAGE );
			if (newFileName != null && !newFileName.isEmpty()) {
				if (flashFile.setNewFile(newFileName))
					HelperClass.showError("File already exists!");
				else {
					selectedFileLabel.setText(selectedString + flashFile.getFileName());
					textPane.setText(null);
//					setWarningMessage("new");
					textPane.setVisible(true);
					findField.setEnabled(true);
					redoButton.setEnabled(true);
					undoButton.setEnabled(true);
					saveButton.setEnabled(true);
					findBtn.setEnabled(true);
					loadButton.setEnabled(true);
				}
			}
		});
		toolBar.add(newFileButton);
		
		//Open file tab
		openFileButton = new JButton("Open");
		openFileButton.setFont(new Font("Arial", Font.PLAIN, 15));
		openFileButton.setMnemonic(KeyEvent.VK_O);
		openFileButton.setToolTipText("Alt + O");
		openFileButton.addActionListener( e -> {
			if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				newFileName = HelperClass.removeFileExtension(fileChooser.getSelectedFile().getName());
				flashFile.setNewFile(newFileName);
				selectedFileLabel.setText(selectedString + flashFile.getFileName());
				dropDownList.setSelectedItem(flashFile.getSeparator());
				loadVocabOnScreen();
				textPane.setCaretPosition(0);
//				setWarningMessage("load");
				textPane.setVisible(true);
				findField.setEnabled(true);
				redoButton.setEnabled(true);
				undoButton.setEnabled(true);
				saveButton.setEnabled(true);
				findBtn.setEnabled(true);
				loadButton.setEnabled(true);
			
			}
		});
		toolBar.add(openFileButton);
		
		//Separator to separte between file management and textpane handling
		toolBar.addSeparator();
		
		//Save button
		@SuppressWarnings({ "serial" })
		Action saveAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				try {
					//Updates the new flashFile with the one inside JTextArea.
					HelperClass.addBackUp(flashFile, textPane.getText());
					flashFile.setSeparator((Character)dropDownList.getSelectedItem());
					flashFile.updateVocabs(textPane.getText());
					int caretLocation = textPane.getCaretPosition();
					loadVocabOnScreen();
					if (caretLocation <= textPane.getDocument().getLength())
						textPane.setCaretPosition(caretLocation);
//					setWarningMessage("load");
					doneButton.setEnabled(true);
					selectedFileLabel.setText(selectedString + flashFile.getFileName());
				} catch (FileSaveException ex) {
					// This catches any error that happens while saving.
					// ex.getMessage() returns the line that threw the error. =
					
					//Alert frame
					alertFrame = new JFrame("Save Error");
					alertText = new JTextArea();
					alertText.setText("Incorrect format on the line: " + ex.getLineNumber() + "\nLine: " + ex.getMessage());
					alertFrame.add(new JScrollPane(alertText, 
							JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
					alertFrame.setSize(450, 300);
					alertFrame.setBackground(new Color(255,255,225));
					alertFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					alertFrame.setLocationRelativeTo(null);
					alertFrame.setVisible(true);
				}
				
			}
		};
		
		saveButton = new JButton("Save");
		saveButton.setFont(new Font("Arial", Font.PLAIN, 15));
		saveButton.setToolTipText("Ctrl + S");
		saveAction.putValue(Action.ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
		saveButton.getActionMap().put("saveAction", saveAction);
		saveButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				(KeyStroke) saveAction.getValue(Action.ACCELERATOR_KEY), "saveAction");
		saveButton.addActionListener(saveAction);
		saveButton.setEnabled(false);
		toolBar.add(saveButton);
		
		
		//Undo button
		undoButton = new JButton("Undo");
		undoButton.setFont(new Font("Arial", Font.PLAIN, 15));
		undoButton.setToolTipText("Ctrl + Z");
		undoButton.addActionListener(undoAction);
		undoButton.setEnabled(false);
		toolBar.add(undoButton);
		
		//Redo button
		redoButton = new JButton("Redo");
		redoButton.setFont(new Font("Arial", Font.PLAIN, 15));
		redoButton.setToolTipText("Ctrl + Y");
		redoButton.addActionListener(redoAction);
		redoButton.setEnabled(false);
		toolBar.add(redoButton);
		toolBar.addSeparator();
		
		//Filename label
		selectedFileLabel = new JLabel(selectedString + "File Not Loaded ");
		selectedFileLabel.setFont(new Font("Arial", Font.ITALIC, 15));
		selectedFileLabel.setPreferredSize(new Dimension(350,0));
		toolBar.add(selectedFileLabel);
		toolBar.addSeparator();
		
		//Search label
		searchLabel = new JLabel("Search: ");
		searchLabel.setFont(new Font("Arial", Font.ITALIC, 15));
		toolBar.add(searchLabel);
		
		//"Find" field
		findField = new JTextField("Enter To Find Next");
		findField.setFont(new Font("Arial", Font.ITALIC, 15));
		findField.setEnabled(false);
		findField.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				findWord("");
			}
			@Override
			public void mousePressed(MouseEvent e) {
				findField.setText("");
			}
		});
		findField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() != KeyEvent.VK_ENTER) {
					findCaretPosition = 0;
					findWord(findField.getText());
				} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					invisFindBtn.doClick();
				}		
			}
		});
		toolBar.add(findField);
		
		//Find action and button
		findAction = new AbstractAction() {
			private static final long serialVersionUID = 1905905700260418383L;
			@Override
			public void actionPerformed(ActionEvent e) {
				//Set caret position
				try {
					String target = findField.getText().toLowerCase();
					Document caretDoc = textPane.getDocument();
					String text = textPane.getText(0, caretDoc.getLength()).toLowerCase();
					
					//If can't find the word.
					if (text.indexOf(target) == -1)
						throw new Exception("Word not found");
					
					findCaretPosition = text.indexOf(target, findCaretPosition);
					if (findCaretPosition == -1) {
						findCaretPosition = text.indexOf(target, 0);
					}
					textPane.setCaretPosition(findCaretPosition);
					findCaretPosition += target.length();
				} catch (IllegalArgumentException illegalArgumentException) {
					findCaretPosition = 0;
					textPane.setCaretPosition(findCaretPosition);
					System.out.println("In illegalargumentexception error for find");
				} catch (BadLocationException badE) {
					badE.printStackTrace();
				} catch (Exception ec) {
					findCaretPosition = 0;
					System.out.println("In exception error for find");
				}
				
			}
		};
		//Enter key binding.
//		InputMap inputMap = findField.getInputMap(JLabel.WHEN_FOCUSED);
//		ActionMap actionMap = findField.getActionMap();
//		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "find");
//		actionMap.put("find", findAction);
		
		
		//Find button
		findBtn = new JButton(" Find ");
		findBtn.setFont(new Font("Arial", Font.PLAIN, 15));
		findBtn.addActionListener(findAction);
		findBtn.setEnabled(false);
//		toolBar.add(findBtn);
		
		//Invis find button
		invisFindBtn = new JButton();
		invisFindBtn.addActionListener(findAction);
		
		add(toolBar, BorderLayout.NORTH);
/////////////////////text Area//////////////////////
		textPane = new JTextPane();
		textPane.setFont(new Font("Arial", Font.PLAIN, 19));
		textPane.setBackground(new Color(255,255,225));
		textPane.addKeyListener(this);
		textPane.setSelectionColor(Color.gray);
		textPane.setVisible(false);
		textPane.setOpaque(true);
		doc = textPane.getStyledDocument();
		loadVocabOnScreen();
		textPane.setCaretPosition(0);
		
		add(new JScrollPane(textPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
/////////////////////////////southToolPanel/////////////////////////////
		southToolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,7,0));
//		southToolPanel.setBackground(new Color (233,150,122));
		//Settings for label for drop down selection
		comboBoxLabel = new JLabel("Character To Separate Vocab and Definitions ");
		comboBoxLabel.setFont(new Font("Arial", Font.PLAIN, 17));
		southToolPanel.add(comboBoxLabel);
//////////////////Drop down selection/////////////////////
		dropDownList = new JComboBox<Character>(separatorList);
		Dimension dDropDown = dropDownList.getPreferredSize();
		dDropDown.width += 100;
		dropDownList.setPreferredSize(dDropDown);
		dropDownList.setFont(new Font("Arial", Font.PLAIN, 18));
		dropDownList.setFocusable(false);
		dropDownList.setForeground(Color.MAGENTA);
		dropDownList.setSelectedItem(flashFile.getSeparator());
		southToolPanel.add(dropDownList);
		
		//Settings for load button.
		loadButton = new JButton("Load");
		loadButton.setFont(new Font("Arial", Font.PLAIN, 17));
		loadButton.setEnabled(false);
		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Updates the new flashFile with the one inside JTextArea.
				try {
					HelperClass.addBackUp(flashFile, textPane.getText());
					flashFile.setSeparator((Character)dropDownList.getSelectedItem());
					flashFile.updateVocabs(textPane.getText());
					int caretLocation = textPane.getCaretPosition();
					loadVocabOnScreen();
					if (caretLocation <= textPane.getDocument().getLength())
						textPane.setCaretPosition(caretLocation);
					// setWarningMessage("load");
					doneButton.setEnabled(true);
					selectedFileLabel.setText(selectedString + flashFile.getFileName());
				} catch (FileSaveException ex) {
					// This catches any error that happens while saving.
					// ex.getMessage() returns the line that threw the error. =
					
					//Alert frame
					alertFrame = new JFrame("Save Error");
					alertText = new JTextArea();
					alertText.setText("Incorrect format on the line: " + ex.getLineNumber() + "\nLine: " + ex.getMessage());
					alertFrame.add(new JScrollPane(alertText, 
							JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
					alertFrame.setSize(450, 300);
					alertFrame.setBackground(new Color(255,255,225));
					alertFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					alertFrame.setLocationRelativeTo(null);
					alertFrame.setVisible(true);
				}
			}
		});
		southToolPanel.add(loadButton);
		
		//Settings for done button.
		doneButton = new JButton("Done");
		doneButton.setFont(new Font("Arial", Font.PLAIN, 17));
		doneButton.setToolTipText("Saving will enable this.");
		doneButton.addActionListener( e -> {
			new MainMenu(flashFile, this.getContentPane());
//			alertFrame.dispose();
			dispose();
		});
		southToolPanel.add(doneButton);
		add(southToolPanel, BorderLayout.SOUTH);
		
		if (flashFile.getLoaded()) {
			selectedFileLabel.setText(selectedString + flashFile.getFileName());
			textPane.setVisible(true);
			findField.setEnabled(true);
			redoButton.setEnabled(true);
			undoButton.setEnabled(true);
			saveButton.setEnabled(true);
			findBtn.setEnabled(true);
			loadButton.setEnabled(true);
		}
		
//Alert frame
//		alertFrame = new JFrame("FlashCard Status Information");
//		alertText = new JTextArea();
//		alertFrame.add(new JScrollPane(alertText, 
//				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
//		alertFrame.setSize(720, 500);
//		alertFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//		alertFrame.setVisible(true);
		
		
		//Key bindings
		setUndoRedo();
		
		//Settings of Frame
		setBackground(new Color(255,255,225));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 600);
		setLocationRelativeTo(container);
		setVisible(true);
	}
	
	//Find the specified parameter inside textPane
	private void findWord(String target) {	
		target = target.toLowerCase();
		
		//Removes old highlights
		Highlighter hilite = textPane.getHighlighter();
		Highlighter.Highlight[] hilites = hilite.getHighlights();
		for (int index = 0; index < hilites.length; index++) {
			if (hilites[index].getPainter() instanceof MyHighlightPainter) {
				hilite.removeHighlight(hilites[index]);
			}
		}
		
		if (target.length() == 0)
			return;
		
		//Background of findField
		if (textPane.getText().toLowerCase().indexOf(target) != -1)
			findField.setBackground(Color.white);
		else
			findField.setBackground(Color.red);

		//Add highlights
		try {
			hilite = textPane.getHighlighter();
			Document doc = textPane.getDocument();
			String text = textPane.getText(0, doc.getLength());
			text = text.toLowerCase();
			
			int pos = 0;
			while((pos = text.indexOf(target, pos)) >= 0) {
				hilite.addHighlight(pos, pos + target.length(), new MyHighlightPainter(Color.LIGHT_GRAY));
				pos += target.length();
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		invisFindBtn.doClick();
	}
	
	
	private void addLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {e.printStackTrace();}
	}
	
	private void setUndoRedo() {
		doc.addUndoableEditListener(new UndoableEditListener() {
			public void undoableEditHappened(UndoableEditEvent e) {
				undoManager.addEdit(e.getEdit());
				undoAction.update();
				redoAction.update();
			}
		});
		
		KeyStroke undoKeystroke =  KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.CTRL_MASK);
		KeyStroke redoKeystroke = KeyStroke.getKeyStroke(KeyEvent.VK_Y, Event.CTRL_MASK);
		
		textPane.getInputMap().put(undoKeystroke, "undoKeystroke");
		textPane.getInputMap().put(redoKeystroke, "redoKeystroke");
		textPane.getActionMap().put("redoKeystroke", redoAction);
		textPane.getActionMap().put("undoKeystroke", undoAction);
	}
	private void loadVocabOnScreen() {
		textPane.setText(null);
		
		try {
			if (flashFile.getCount() > 0) {
				setTextColor("vocab");
				doc.insertString(doc.getLength(), flashFile.getVocab(0), attr);
				setTextColor("definition");	
				doc.insertString(doc.getLength(), " " + flashFile.getSeparator() + " " 
						+ flashFile.getDefinition(0), attr);
			}

			for (int index = 1; index < flashFile.getCount(); index++) {
				setTextColor("vocab");
				doc.insertString(doc.getLength(), "\n" + flashFile.getVocab(index), attr);
				setTextColor("definition");	
				doc.insertString(doc.getLength(), " " + flashFile.getSeparator() + " " 
						+ flashFile.getDefinition(index), attr);
			}
		} catch (BadLocationException e) {e.printStackTrace();}
	}
	private void setTextColor(String type) {
		if (type.equals("vocab")) {
			StyleConstants.setForeground(attr, new Color(153,0,0));
		}
		else if (type.equals("definition")) {
			StyleConstants.setForeground(attr, Color.black);
		}
		else if (type.equals("loadMsg")) {
			StyleConstants.setForeground(attr, Color.gray);
		}
		textPane.setCharacterAttributes(attr, false);
	}
//	private void setWarningMessage(String buttonClicked) {
//		setTextColor("loadMsg");
//		switch (buttonClicked) {
//		case "new":
//			textPane.setText("\n**************************************************************************\n" + 
//					"Start typing your vocabs and definition below here!\nThe format is [Vocab] (chosen separator) [Definition].\n" +
//					"Ex. Aung Moe : the author of Moe's Flash Card (with ':' chosen as the separator).\n" +
//					"**************************************************************************\n");
//			break;
//
//		case "load":
//			
//			try {
//				doc.insertString(doc.getLength(), "**************************************************************************\n" + 
//						"Vocabs above have been saved and loaded!\nIf some vocabs disappeared, don't worry!\nThere's a backup file saved, refer to help on the main menu.\n" +
//						"**************************************************************************\n", attr);
//			} catch (BadLocationException e1) {e1.printStackTrace();}
//			break;
//
//		case "changed":
//			try {
//				doc.insertString(doc.getLength(), "\n**************************************************************************\n" + 
//						"WARNING If you use the wrong selector your words will get deleted!\n" +
//						"**************************************************************************\n", attr);
//			} catch (BadLocationException e1) {e1.printStackTrace();}
//			break;
//
//		default:
//			break;
//		}
//		setTextColor("vocab");
//	}
	
	public void keyPressed(KeyEvent e) {
		doneButton.setEnabled(false);
		selectedFileLabel.setText(selectedString + flashFile.getFileName() + "*");
	}
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {
		if (e.getKeyChar() == (Character) dropDownList.getSelectedItem()) {
			setTextColor("definition");	
		}
		if (e.getKeyChar() == KeyEvent.VK_ENTER) {
			setTextColor("vocab");
		}
	}
	
	class UndoAction extends AbstractAction
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -2837080271615354139L;

		public UndoAction()
		{
			super("Undo");
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e)
		{
			try
			{
				undoManager.undo();
			}
			catch (CannotUndoException ex)
			{
				ex.printStackTrace();
			}
			update();
			redoAction.update();
		}

		protected void update()
		{
			if (undoManager.canUndo())
			{
				setEnabled(true);
				putValue(Action.NAME, undoManager.getUndoPresentationName());
			}
			else
			{
				setEnabled(false);
				putValue(Action.NAME, "Undo");
			}
		}
	}

	class RedoAction extends AbstractAction
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 8803260651868161033L;

		public RedoAction()
		{
			super("Redo");
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e)
		{
			try
			{
				undoManager.redo();
			}
			catch (CannotRedoException ex)
			{
				ex.printStackTrace();
			}
			update();
			undoAction.update();
		}

		protected void update()
		{
			if (undoManager.canRedo())
			{
				setEnabled(true);
				putValue(Action.NAME, undoManager.getRedoPresentationName());
			}
			else
			{
				setEnabled(false);
				putValue(Action.NAME, "Redo");
			}
		}
	}
	
	//Private sublcass for highlight painter
	class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {

		public MyHighlightPainter(Color arg0) {
			super(arg0);
		}
		
	}
}