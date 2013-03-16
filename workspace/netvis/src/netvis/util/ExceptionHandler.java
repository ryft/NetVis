package netvis.util;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;

/**
 * A pretty global uncaught exception handler which allows bug reporting at a
 * custom URL.
 */
public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

	protected final Component parent;
	protected final String url = "https://docs.google.com/forms/d/1_B-LXFkWZClrs32S_fPNuUI_SyIbsVHPINQXltwFgnE/viewform";

	public ExceptionHandler(Component parent) {
		this.parent = parent;
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {

		// Get stack trace as a string
		StringBuilder stackTraceBuilder = new StringBuilder(e.toString());
		for (StackTraceElement ste : e.getStackTrace())
			stackTraceBuilder.append("\n" + ste);
		String stackTrace = stackTraceBuilder.toString();

		// Copy it to the clipboard
		Toolkit.getDefaultToolkit().getSystemClipboard()
				.setContents(new StringSelection(stackTrace), null);

		// Show a pretty box
		int dialogueChoice = JOptionPane
				.showOptionDialog(
						parent,
						"Error occurred and threw an uncaught exception in thread "
								+ t.getName()
								+ "\nThe stack trace has been copied to your clipboard for your conveneice."
								+ "\nPlease submit this for review, no personal details will be collected.",
						"Error caught", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE,
						null, new String[] { "Submit", "Close" }, "Submit");

		// Do the right thing in response
		if (dialogueChoice == 0) {
			try {
				Desktop.getDesktop().browse(new URI(url));
				JOptionPane.showMessageDialog(parent,
						"Please close this dialogue after submitting your report.\n"
								+ "Closing it will cause the JVM to clear your clipboard.");
			} catch (IOException | URISyntaxException e1) {
				JOptionPane.showMessageDialog(parent,
						"Couldn't open browser. Please visit the below address:\n" + url);
			}
		}

		// Quit with error code 1
		System.exit(1);
	}

}
