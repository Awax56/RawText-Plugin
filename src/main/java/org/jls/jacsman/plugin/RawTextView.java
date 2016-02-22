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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jls.toolbox.widget.Console;

import net.miginfocom.swing.MigLayout;

/**
 * The Raw Text plugin view.
 * 
 * @author LE SAUCE Julien
 * @date Aug 25, 2015
 */
public class RawTextView extends JPanel implements ActionListener {

	private static final long serialVersionUID = -3541483516302094242L;

	public static final Color normalColor = Color.black;
	public static final Color greenColor = Color.green.darker();
	public static final Color redColor = Color.red;
	public static final Color dataColor = new Color(230, 115, 0);

	private final RawTextController controller;
	private final Logger logger;

	private Console consoleIn;
	private JTextArea consoleOut;
	private JButton btnSend;
	private JButton btnImport;

	/**
	 * Instanciates the plugin view.
	 * 
	 * @param controller
	 *            Plugin interface controller.
	 */
	public RawTextView (final RawTextController controller) {
		super();
		this.controller = controller;
		this.logger = LogManager.getLogger();
	}

	/**
	 * Creates the graphical user interface.
	 * 
	 * @return Returns the view.
	 */
	public RawTextView createGui () {
		createComponents();
		setStyle();
		addListeners();
		print("## " + RawText.pluginName + " - Version " + RawText.pluginVersion + " ##\n\n", normalColor, Font.PLAIN);
		return this;
	}

	/**
	 * Shows a pop-up with the specified message.
	 * 
	 * @param title
	 *            Title of the pop-up.
	 * @param msg
	 *            The pop-up message.
	 * @param option
	 *            Type of pop-up (see {@link JOptionPane#setMessageType(int)}).
	 */
	public final void pop (final String title, final String msg, final int option) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run () {
				JOptionPane.showMessageDialog(RawTextView.this, msg, title, option);
			}
		});
	}

	/**
	 * Prints the specified text into the console.
	 * 
	 * @param text
	 *            Text to print.
	 * @param textColor
	 *            Text color.
	 * @param fontStyle
	 *            Text style ({@link Font#BOLD}, {@link Font#ITALIC} ou
	 *            {@link Font#PLAIN}).
	 */
	public void print (final String text, final Color textColor, final int fontStyle) {
		this.consoleIn.print(text, textColor, fontStyle);
	}

	/**
	 * Instanciates the elements composing the user graphical interface.
	 */
	private void createComponents () {
		this.consoleIn = new Console();
		this.consoleIn.setBorder(BorderFactory.createTitledBorder("Console :"));
		this.consoleOut = new JTextArea();
		this.consoleOut.setBorder(BorderFactory.createTitledBorder("Output :"));
		this.btnSend = new JButton("Send");
		this.btnImport = new JButton("Import");
	}

	/**
	 * Adds the components and creates the user graphical interface.
	 */
	private void setStyle () {
		JScrollPane consoleInScroll = new JScrollPane(this.consoleIn);
		JScrollPane consoleOutScroll = new JScrollPane(this.consoleOut);

		setLayout(new MigLayout("fill"));
		add(consoleInScroll, "wmin 600lp, hmin 300lp, spanx, push, grow, wrap");
		add(consoleOutScroll, "hmin 150lp, push, grow");
		add(this.btnSend, "split, flowy, growx");
		add(this.btnImport, "growx");
	}

	/**
	 * Adds the listeners to the GUI components.
	 */
	private void addListeners () {
		this.btnSend.addActionListener(this);
		this.btnImport.addActionListener(this);
	}

	@Override
	public void actionPerformed (ActionEvent e) {
		/*
		 * JButton
		 */
		if (e.getSource() instanceof JButton) {
			JButton btn = (JButton) e.getSource();

			// Send
			if (this.btnSend.equals(btn)) {
				String msg = this.consoleOut.getText();
				this.controller.send(msg);
			}
			// Import
			else if (this.btnImport.equals(btn)) {
				try {
					String content = this.controller.importFile();
					// Update the output text area
					if (content != null) {
						this.consoleOut.setText(content);
					}
				} catch (IOException e1) {
					this.logger.error("An error occurred importing file", e1);
					pop("Import Error", "An error occurred importing file :\n\n" + e1.getMessage(),
							JOptionPane.ERROR_MESSAGE);
					this.controller.print("ERROR : An error occurred importing file : " + e1.getMessage(), redColor);
				}
			}
		}
	}
}