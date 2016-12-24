import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

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

public class FlashFile
{
//Vocabs and Definitions
	private ArrayList<String> vocabWord = new ArrayList<String>();
	private ArrayList<String> definition = new ArrayList<String>();
	private int countOfVocabs = 0;
	private String fileName;
	private Character separator = ':';
	
//Known vocabs
	private ArrayList<String> knownVocabs = new ArrayList<String>();
	
//Priority vocabs
	private ArrayList<String> priorityVocabs = new ArrayList<String>();
	
//IO File
	private File file;
	private BufferedReader reader = null;
	private PrintWriter writer = null;
	private String extension = ".vocab";
	private boolean loaded = false;

	public FlashFile() {
		fileName = "Unknown" + extension;
	}
	
	//Call this if a file already exists.
	public FlashFile(String fileName) {
		this.fileName = fileName;
		file = new File(fileName);
		loadFromFile();
	}

//File functions
	public void loadFromFile() {
		vocabWord.clear();
		definition.clear();
		knownVocabs.clear();
		priorityVocabs.clear();
		countOfVocabs = 0;
		loaded = true;
		try 
		{
			file.createNewFile(); //Creates a new file if an existing old one isn't found.
			reader = new BufferedReader(new FileReader(file.getName()));
			String textLine;
			boolean isInVocabs = false, isInKnown = false, isInPriority = false;
			while ((textLine = reader.readLine()) != null)
			{
				//Check if it's in vocabs section.
				if (textLine.equals("<Vocabs>")) {
					isInVocabs = true;
					continue;
				} else if (textLine.equals("</Vocabs>")) {
					isInVocabs = false;
					continue;
				}
				//Check if it's in known section.
				if (textLine.equals("<Known>")) {
					isInKnown = true;
					continue;
				} else if (textLine.equals("</Known>")) {
					isInKnown = false;
					continue;
				}
				//Check if it's in priority section.
				if (textLine.equals("<Priority>")) {
					isInPriority = true;
					continue;
				} else if (textLine.equals("</Priority>")) {
					isInPriority = false;
					continue;
				}
				
				//Load vocabs.
				String[] splitLine = new String[2];
				if (isInVocabs) {
					splitLine = textLine.split(separator.toString(), 2);
					if (isASeparator(textLine))
						separator = textLine.charAt(0);
					else if (splitLine.length == 2)
					{
						vocabWord.add(splitLine[0].trim());
						definition.add(splitLine[1].trim());
						countOfVocabs++;
					}
				}
				
				//Load known.
				if (isInKnown) {
					knownVocabs.add(textLine);
				}
				
				//Load priority.
				if (isInPriority) {
					priorityVocabs.add(textLine);
				}
			}
			reader.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		catch (IndexOutOfBoundsException e)
		{
			e.printStackTrace();
		}
	}

	public void updateVocabs(String text) throws FileSaveException{
		vocabWord.clear();
		definition.clear();
		countOfVocabs = 0;
		String[] eachLine = text.split("[\\r\\n]+");	//Splits string by enter. 
		String[] eachWord = new String[2];
		for (int index = 0; index < eachLine.length; index++) {
			eachWord = eachLine[index].split(separator.toString(), 2);
			if (eachWord.length == 2) {
				vocabWord.add(eachWord[0].trim());
				definition.add(eachWord[1].trim());
				countOfVocabs++;
			} else {
				throw new FileSaveException(eachLine[index], index+1);
			}
		} 
		saveToFile();
	}
	
	public void saveToFile() {
		saveToFile(file);
	}
	public void saveToFile(File file) {
		try 
		{
			//Create a new empty file
			writer = new PrintWriter(file);
			
			//Load vocabs.
			writer.println("<Vocabs>");
			writer.println(separator);
			for (int index = 0; index < countOfVocabs; index++) {
				writer.println("   " + vocabWord.get(index) + " " + separator + " " + definition.get(index) );
			}
			writer.println("</Vocabs>");
			
			//Load known.
			writer.println("<Known>");
			for (int index = 0; index < knownVocabs.size(); index++) {
				writer.println("   " + knownVocabs.get(index));
			}
			writer.println("</Known>");
			
			//Load priority.
			writer.println("<Priority>");
			for (int index = 0; index < priorityVocabs.size(); index++) {
				writer.println("   " + priorityVocabs.get(index));
			}
			writer.println("</Priority>");
			
			writer.close();
		}
		catch (IOException e) {e.printStackTrace();}
	}

	public boolean setNewFile(String fileName) {
		boolean condition = false;
		this.fileName = fileName;
		file = new File(fileName + extension);
		//This function checks if the file exists before attempting to make the file
		condition = file.exists();
		loadFromFile(); //This function makes the file
		return condition;
	}
	
	
//Instance variable setters/getters for vocabs
	public String getVocab(int i)
	{
		if (countOfVocabs > 0 && (i < countOfVocabs && i >= 0) )
			return vocabWord.get(i);
		else
			return "out of bound index for definition String";
	}
	public String getDefinition(int i)
	{
		if (countOfVocabs > 0 && (i < countOfVocabs && i >= 0) )
			return definition.get(i);
		else
			return "out of bound index for definition String";
	}
	public int getIndexOfVocab(String vocab) 
	{
		return vocabWord.indexOf(vocab);
	}
	public String getDefOf(String vocab)
	{
		return definition.get(getIndexOfVocab(vocab));
	}
	//THIS SHOULDN'T BE USED.
	public int getIndexOfDefinition(String definition)
	{
		return definition.indexOf(definition);
	}
	//THIS SHOULDN'T BE USED.
	public String getVocabOf(String definition) 
	{
		return vocabWord.get(getIndexOfDefinition(definition));
	}
	public int getCount()
	{
		return countOfVocabs;
	}
	public void setSeparator(Character separator) 
	{
		this.separator = separator;
	}
	public Character getSeparator() {
		return separator;
	}
	public boolean isASeparator(String line) 
	{
		boolean condition;
		switch (line) {
		case ":":
			condition = true;
			break;
		case "-":
			condition = true;
			break;
		case "--":
			condition = true;
			break;
		case "=":
			condition = true;
			break;
		default:
			condition = false;
		}
		return condition;
	}
	public String getFileName() {
		return fileName;
	}
	
	public boolean getLoaded() {
		return loaded;
	}
//Functions for known and priority
	//Returns true if word is in the known.
	public boolean findKnownWord(String selectVocab){
		boolean found = false;
		for (int index = 0; index < knownVocabs.size(); index++)
		{
			if (knownVocabs.get(index).trim().equals(selectVocab)) {
				found = true;
			}
		}
		return found;
	}
	
	public boolean findPriorityWord(String selectVocab) {
		boolean found = false;
		for (int index = 0; index < priorityVocabs.size(); index++)
		{
			if (priorityVocabs.get(index).trim().equals(selectVocab))
				found = true;
		}
		return found;
	}
	public int getPriorityCount() {
		return priorityVocabs.size();
	}
	public String getPriorityVocab(int i) {
		return priorityVocabs.get(i);
	}
	public void addKnown(String name) {
		knownVocabs.add(name);
	}
	public void addPriority(String name) {
		priorityVocabs.add(name);
	}
	public void clearKnown() {
		knownVocabs.clear();
	}
	public void clearPriority() {
		priorityVocabs.clear();
	}
	public String getExtension() {
		return extension;
	}

}