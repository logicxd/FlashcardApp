import javax.swing.JFrame;
import javax.swing.SwingUtilities;

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

public class FlashCardApp {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					new LoadVocab(new FlashFile(), new JFrame());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}