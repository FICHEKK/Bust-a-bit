package bustabit;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * The graphical user interface part of the game.
 * 
 * @author FICHEKK
 */
@SuppressWarnings("serial")
public class GUI extends JFrame {
	
	/** Displays the player's current bank-roll. */
	private JLabel bankrollLabel;
	
	/** Displays the game's current multiplier. */
	private JLabel multiplierLabel;
	
	/** Displays the current cash-out value for the player. */
	private JLabel cashOutValueLabel;
	
	/** Allows the user to place a bet. */
	private JTextField betTextField;
	
	/** Allows the user to set the auto cash-out multiplier value. */
	private JTextField autoCashOutTextField;
	
	/** Round control button. */
	private JButton roundControlButton;
	
	/** Reference to the game manager that performs the game logic. */
	private GameManager gameManager;
	
	/** A custom font used by the GUI. */
	private static final String FONT_NAME = "BebasNeue-Bold.ttf";
	private static Font font18;
	private static Font font30;
	private static Font font48;
	
	static {
		try {
			Font font = Font.createFont(Font.TRUETYPE_FONT, new File("resources/" + FONT_NAME));
			font18 = font.deriveFont(18f);
			font30 = font.deriveFont(30f);
			font48 = font.deriveFont(48f);
		} catch (Exception ignorable) {
		}
	}
	
	//-----------------------------------------------------------------------
	//							NUMBER FORMATTING
	//-----------------------------------------------------------------------
	
	private static final DecimalFormatSymbols symbols ;

	private static final DecimalFormat formatterLong;
	
	private static final DecimalFormat formatterDouble; 
	
	static {
		symbols = new DecimalFormatSymbols();
		symbols.setGroupingSeparator(' ');
		formatterLong = new DecimalFormat("###,###", symbols);
		formatterDouble = new DecimalFormat("###,###.000", symbols);
	}
	
	//-----------------------------------------------------------------------
	//							  CONSTRUCTOR
	//-----------------------------------------------------------------------
	
	/** Constructs a new GUI for the game. */
	public GUI() {
		gameManager = new GameManager(this);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 400);
		setTitle("Bust-a-bit");
		setResizable(false);

		initGUI();
	}
	
	//-----------------------------------------------------------------------
	//								GUI
	//-----------------------------------------------------------------------

	private void initGUI() {
		Container pane = getContentPane();
		
		//---------------------------------------------------
		// 				  Bank-roll display
		//---------------------------------------------------
		bankrollLabel = new JLabel("", SwingConstants.CENTER);
			bankrollLabel.setFont(font18);
			bankrollLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		updateBankroll();
		pane.add(bankrollLabel, BorderLayout.NORTH);
		
		//---------------------------------------------------
		// 				  Multiplier display
		//---------------------------------------------------
		JPanel gamePanel = new JPanel(new GridLayout(2, 1));
		gamePanel.setBackground(new Color(20, 144, 245));
		
		multiplierLabel = new JLabel("x1,000", JLabel.CENTER);
			multiplierLabel.setFont(font48);
			gamePanel.add(multiplierLabel);
		cashOutValueLabel = new JLabel("Cash-out value: -", JLabel.CENTER);
			cashOutValueLabel.setFont(font30);
			gamePanel.add(cashOutValueLabel);
		pane.add(gamePanel, BorderLayout.CENTER);
		
		//---------------------------------------------------
		// 					Bottom panel
		//---------------------------------------------------
		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
			JPanel betPanel = new JPanel(new GridLayout(1, 4));
			betPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			JLabel betLabel = new JLabel("Bet:", JLabel.CENTER);
				betLabel.setFont(font18);
				betPanel.add(betLabel);
			betPanel.add(betTextField = new JTextField());
				betTextField.setHorizontalAlignment(JTextField.CENTER);
				betTextField.setText(DataManager.getInstance().getProperty("bet"));
			JLabel autoCashOutLabel = new JLabel("Auto cash-out at:", JLabel.CENTER);
				autoCashOutLabel.setFont(font18);
				betPanel.add(autoCashOutLabel);
			betPanel.add(autoCashOutTextField = new JTextField());
				autoCashOutTextField.setHorizontalAlignment(JTextField.CENTER);
				autoCashOutTextField.setText(DataManager.getInstance().getProperty("autoCashOut"));
			bottomPanel.add(betPanel, BorderLayout.NORTH);
			
			roundControlButton = new JButton(roundController);
				roundControlButton.setText("Start !");
				roundControlButton.setBackground(Color.LIGHT_GRAY);
				bottomPanel.add(roundControlButton, BorderLayout.SOUTH);
		
		pane.add(bottomPanel, BorderLayout.SOUTH);
	}
	
	//-----------------------------------------------------------------------
	//							GUI INTERFACE
	//-----------------------------------------------------------------------
	
	/** Performs all of the GUI updates upon the round start. */
	public void start() {
		SwingUtilities.invokeLater(() -> {
			roundControlButton.setText("Cash-out !");
			roundControlButton.setBackground(Color.ORANGE);
			multiplierLabel.setForeground(Color.ORANGE);
			cashOutValueLabel.setForeground(Color.ORANGE);
		});
	}
	
	/** Performs all of the GUI updates upon the round crash. */
	public void crash() {
		SwingUtilities.invokeLater(() -> {
			multiplierLabel.setForeground(Color.RED);
			cashOutValueLabel.setForeground(Color.RED);
			cashOutValueLabel.setText("Busted | " + 
					  formatterLong.format(gameManager.getBet() * gameManager.getMultiplier()));
			updateBankroll();
		});
	}
	
	/** Performs all of the GUI updates upon the round reset. */
	public void reset() {
		SwingUtilities.invokeLater(() -> {
			roundControlButton.setText("Start !");
			roundControlButton.setBackground(Color.LIGHT_GRAY);
			roundControlButton.setEnabled(true);
		});
	}
	
	/** Performs all of the GUI updates upon the player's cash-out. */
	public void cashOut() {
		SwingUtilities.invokeLater(() -> {
			roundControlButton.setEnabled(false);
			roundControlButton.setBackground(Color.GREEN);
			multiplierLabel.setForeground(Color.GREEN);
			cashOutValueLabel.setText("Cashed out | " + 
									  formatterLong.format(gameManager.getBet() * gameManager.getPlayerCashOutMultiplier()) + " | " +
									  "x" + formatterDouble.format(gameManager.getPlayerCashOutMultiplier()));
			cashOutValueLabel.setForeground(Color.GREEN);
		});
	}
	
	/**
	 * Updates the bank-roll text.
	 */
	public void updateBankroll() {
		SwingUtilities.invokeLater(() -> {
			bankrollLabel.setText("Bankroll: " + formatterLong.format(gameManager.getBankroll()));
		});
	}
	
	/**
	 * Updates the multiplier text.
	 */
	public void updateMultiplier() {
		SwingUtilities.invokeLater(() -> {
			multiplierLabel.setText("x" + formatterDouble.format(gameManager.getMultiplier()));
		});
	}
	
	/**
	 * Updates the cash-out value text.
	 */
	public void updateCashOutValue() {
		SwingUtilities.invokeLater(() -> {
			double cashOutValue = gameManager.getBet() * gameManager.getMultiplier();
			cashOutValueLabel.setText("Cash-out value: " + formatterLong.format(cashOutValue));
		});
	}
	
	/** @return the player's bet from the "bet" text-field */
	public String getBet() {
		return betTextField.getText();
	}
	
	/** @return the player's auto cash-out multiplier from the "auto cash-out" text-field */
	public String getAutoCashOut() {
		return autoCashOutTextField.getText();
	}
	
	//-----------------------------------------------------------------------
	//							BUTTON LOGIC
	//-----------------------------------------------------------------------
	
	private Action roundController = new AbstractAction() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if(gameManager.isRoundRunning()) {
				gameManager.cashOut();
				
			} else {
				try {
					long bet = Long.parseLong(getBet());
					double autoCashOut = Double.parseDouble(getAutoCashOut());
					
					if(bet > gameManager.getBankroll()) {
						betTextField.setText(String.valueOf(gameManager.getBankroll()));
						showMessage("You don't have enough money for this bet!");
						
					} else if(bet <= 0) {
						showMessage("Your bet should be higher than 0!");
						
					} else if(autoCashOut <= 1) {
						showMessage("Auto cash-out value must be higher than x1.000");
						
					} else {
						DataManager.getInstance().updateProperty("bet", String.valueOf(bet));
						DataManager.getInstance().updateProperty("autoCashOut", String.valueOf(autoCashOut));
						gameManager.startRound(bet, autoCashOut);
					}
					
				} catch (NumberFormatException nfe) {
					showMessage("Invalid bet or auto cash-out value!\r\n" +
								"Your bet should be an integer, while the auto cash-out should be a decimal number.");
				}
			}
		}
	};
	
	private void showMessage(String message) {
		JOptionPane.showMessageDialog(GUI.this, message);
	}
}