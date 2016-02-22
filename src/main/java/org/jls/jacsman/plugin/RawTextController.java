/*#
 * The MIT License (MIT)
 * 
 * Copyright (c) 2016 LE SAUCE Julien
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 #*/

package org.jls.jacsman.plugin;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jls.jacsman.plugin.PluginInterface;
import org.jls.toolbox.util.TimeUtils;
import org.jls.toolbox.widget.FileChooser;

/**
 * Controller of the Raw Text plugin.
 * 
 * @author LE SAUCE Julien
 * @date Feb 16, 2016
 */
public class RawTextController {

	private final PluginInterface pluginInterface;
	private final RawTextView view;
	private final Logger logger;
	private File userDir;

	/**
	 * Instanciates the controller.
	 * 
	 * @param pluginInt
	 *            The plugin interface used to communicate with JaCSMan
	 *            application.
	 */
	public RawTextController (final PluginInterface pluginInt) {
		this.pluginInterface = pluginInt;
		this.view = new RawTextView(this);
		this.logger = LogManager.getLogger();
		this.userDir = new File(System.getProperty("user.dir"));
		// Creates the GUI
		this.view.createGui();
	}

	/**
	 * Prints the specified text to the input console.
	 * 
	 * @param text
	 *            Text to print in the console.
	 * @param color
	 *            Text color.
	 */
	public void print (final String text, final Color color) {
		this.view.print(TimeUtils.getConsoleTimestamp(), Color.blue, Font.BOLD);
		this.view.print(" > ", Color.blue, Font.BOLD);
		this.view.print(text + "\n", color, Font.PLAIN);
	}

	/**
	 * Sends the specified message to the Jacsman application.
	 * 
	 * @param msg
	 *            The message to send.
	 */
	public void send (final String msg) {
		try {
			this.logger.debug("Sending message");
			int ret = this.pluginInterface.send(msg.getBytes());
			if (ret > 0) { // If message was successfully sent
				print("Message sent", RawTextView.greenColor);
			} else {
				// Else we expect the server to throw an exception to the plugin
			}
		} catch (IOException e) {
			this.logger.error("An error occurred sending message", e);
			this.view.pop("Sending Error", "An error occurred sending the message :\n\n" + e.getMessage(),
					JOptionPane.ERROR_MESSAGE);
			print("ERROR : An error occurred sending message : " + e.getMessage(), RawTextView.redColor);
		}
	}

	/**
	 * Imports a file.
	 * 
	 * @return The content of the selected file.
	 * @throws IOException
	 *             If an error occurred reading the imported file.
	 */
	public String importFile () throws IOException {
		FileChooser chooser = new FileChooser(this.userDir);
		int ret = chooser.showOpenDialog(this.view);
		// If user selected a file
		if (ret == FileChooser.APPROVE_OPTION) {
			File selectedFile = chooser.getSelectedFile();
			this.userDir = selectedFile.getParentFile();

			// Read the file
			List<String> lines = Files.readAllLines(selectedFile.toPath(), Charset.defaultCharset());
			String content = "";
			for (String s : lines) {
				content += s;
			}
			print("File imported : " + selectedFile.getPath(), RawTextView.normalColor);
			return content;
		} else {
			return null;
		}
	}

	/**
	 * Returns the associated view.
	 * 
	 * @return The view.
	 */
	public RawTextView getView () {
		return this.view;
	}
}