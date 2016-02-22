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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jls.jacsman.plugin.Plugin;
import org.jls.jacsman.plugin.PluginEvent;
import org.jls.jacsman.plugin.PluginInterface;
import org.jls.toolbox.net.Interface;

/**
 * The Raw Text plugin main class.
 * 
 * @author LE SAUCE Julien
 * @date Aug 25, 2015
 */
public class RawText implements Plugin {

	public static final String pluginName = "Raw Text Plugin";
	public static final String pluginVersion = "1.0.0";

	private final RawTextController controller;
	private final Logger logger;

	/**
	 * Instanciates the RawText plugin.
	 * 
	 * @param pluginInterface
	 *            Interface used by the plugin to interact with the Jacsman
	 *            application.
	 */
	public RawText (final PluginInterface pluginInterface) {
		super();
		this.controller = new RawTextController(pluginInterface);
		this.logger = LogManager.getLogger();
	}

	@Override
	public String getName () {
		// Returns the plugin's name
		return pluginName;
	}

	@Override
	public void onException (PluginEvent event, Throwable t) {
		this.logger.error("An error occurred on interface {}", event.getInterface(), t);
		this.controller.print("ERROR : An error occurred on " + event.getInterface().getId() + " :\n" + t.getMessage(),
				RawTextView.redColor);
	}

	@Override
	public void onReceive (PluginEvent event) {
		Interface com = event.getInterface();
		this.logger.info("Message received from {} ({}:{})", com.getId(), com.getAddress(), com.getPort());
		// Decodes the message
		String msg = new String(event.getMessage());
		// Prints it to the console
		this.controller.print("Message received from " + com.getId() + " (" + com.getAddress() + ":" + com.getPort() + ")",
				RawTextView.greenColor);
		this.controller.print("Message :\n" + msg, RawTextView.dataColor);
	}

	@Override
	public void onTimeout (PluginEvent event) {
		this.logger.warn("** Timeout occurred on {}", event.getInterface().getId());
		this.controller.print("** Timeout occurred on " + event.getInterface().getId(), RawTextView.redColor);
	}

	@Override
	public RawTextView getView () {
		return this.controller.getView();
	}
}