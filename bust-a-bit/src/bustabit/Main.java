package bustabit;

import javax.swing.SwingUtilities;

/**
 * The starting point of the application. It simply creates a new
 * game window.
 *
 * @author Filip Nemec
 */
public class Main {

	/**
	 * The application starts from here.
	 *
	 * @param args none are used
	 * @throws InterruptedException
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new GUI().setVisible(true));
	}
}