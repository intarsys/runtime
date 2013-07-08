/*
 * Copyright (c) 2007, intarsys consulting GmbH
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of intarsys nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package de.intarsys.tools.reporter;

/**
 * An interface to report different types of information to the user.
 * <p>
 * This may be implemented for example as pure logging in a headless environment
 * or with message dialogs in a window system.
 * 
 */
public interface IReporter {

	public static final int STYLE_NONE = 0;

	public static final int STYLE_STANDALONE = 1;

	public static final int STYLE_BEEP = 2;

	/**
	 * Indicate the end of an activity.
	 */
	public void reportActivityEnd();

	/**
	 * Indicate the beginning of an activity, possibly blocking system
	 * interaction.
	 * 
	 * @param message
	 *            A label for reporting the activity.
	 * @param style
	 */
	public void reportActivityStart(String message, int style);

	/**
	 * Indicate an error condition. This method will return normally after a
	 * possible user interaction, any error handling is still up to the caller.
	 * 
	 * @param title
	 * @param message
	 * @param t
	 * @param style
	 */
	public void reportError(final String title, final String message,
			final Throwable t, int style);

	/**
	 * Report a message to the user. The message should be displayed in a
	 * blocking way in an interactive system.
	 * 
	 * @param title
	 * @param message
	 * @param style
	 */
	public void reportMessage(final String title, final String message,
			int style);

	/**
	 * Indicate the progress of an ongoing activity. The activity will probably
	 * run concurrently.
	 * 
	 * @param message
	 *            The message to be displayed.
	 * @param percent
	 *            The amount of work already done in percent. Valid values are 0
	 *            <= percent <= 100 for progress indication, percent < 0 for the
	 *            end of progress indication.
	 * @param style
	 */
	public void reportProgress(String message, int percent, int style);

	/**
	 * Report a simple state information. This should not block the system in
	 * any way.
	 * 
	 * @param message
	 * @param style
	 */
	public void reportStatus(String message, int style);
}
