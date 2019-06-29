package bustabit;

/**
 * Game manager; performs the game logic and offers
 * basic interface for starting rounds, cashing out
 * and getting player/round information.
 * 
 * @author FICHEKK
 */
public class GameManager {
	
	/** The multiplier iteration factor. */
	private static final double MULTIPLIER_FACTOR = 1.001;
	
	/** The multiplier value at the start of the round. */
	private static final double STARTING_MULTIPLIER = 1.000;

	/** The multiplier iteration duration in milliseconds. */
	private static final long SLEEP_DURATION = 10;
	
	/** The player's bank-roll. */
	private long bankroll;

	/** Flag that indicated if the round is currently running. */
	private boolean roundRunning = false;
	
	/** Flag that indicated if the player has cashed out. */
	private boolean playerCashedOut = false;
	
	/** The multiplier that the player cashed-out on. */
	private double playerCashOutMultiplier = 0;
	
	/** Holds the player's current bet value. */
	private long bet;
	
	/** Holds the current multiplier value. */
	private double multiplier = STARTING_MULTIPLIER;
	
	/** Holds the current round crash value. */
	private double crash;
	
	/** A list of rounds that were played during the current session. */
//	private List<Round> sessionRounds = new LinkedList<>();
	
	/** The reference to the graphical user interface. */
	private GUI gui;
	
	//-----------------------------------------------------------------------
	//							  CONSTRUCTOR
	//-----------------------------------------------------------------------
	
	/**
	 * Constructs a new game manager that communicates with the given GUI.
	 * 
	 * @param gui reference to the application's GUI (graphical user interface)
	 */
	public GameManager(GUI gui) {
		this.gui = gui;
		this.bankroll = Long.parseLong(DataManager.getInstance().getProperty("bankroll"));
	}
	
	//-----------------------------------------------------------------------
	//							PUBLIC METHODS
	//-----------------------------------------------------------------------
	
	/**
	 * Starts a new round with the given player's bet.
	 * 
	 * @param bet the player's bet
	 * @param autoCashOut the player's auto cash-out multiplier
	 */
	public void startRound(long bet, double autoCashOut) {
		this.bet = bet;
		this.roundRunning = true;
		this.crash = generateCrashMultiplier();

		updateBankroll(bankroll - bet);
		gui.start();
		
		new Thread(() -> {
			while(multiplier < crash) {
				multiplier = Math.min(multiplier * MULTIPLIER_FACTOR, crash);
				gui.updateMultiplier();
				
				if(!playerCashedOut) {
					if(autoCashOut > 0 && multiplier >= autoCashOut) {
						multiplier = autoCashOut;
						cashOut();
					} else {
						gui.updateCashOutValue();
					}
				}
				
				try {
					Thread.sleep(SLEEP_DURATION);
				} catch (InterruptedException i) {
				}
			}
			
			crash();
		}).start();
	}

	//-----------------------------------------------------------------------
	//								GETTERS
	//-----------------------------------------------------------------------
	
	/** @return the player's bank-roll */
	public long getBankroll() {
		return bankroll; 
	}
	
	/** @return the current multiplier */
	public double getMultiplier() {
		return multiplier;
	}
	
	/** @return the flag indicating whether the round is running or not */
	public boolean isRoundRunning() {
		return roundRunning;
	}
	
	/** @return the player's bet */
	public long getBet() {
		return bet;
	}
	
	/** @return the player's cash-out multiplier */
	public double getPlayerCashOutMultiplier() {
		return playerCashOutMultiplier;
	}
	
	//-----------------------------------------------------------------------
	//							ROUND CONTROL
	//-----------------------------------------------------------------------
	
	/**
	 * Cashes out at the current multiplier.
	 */
	public void cashOut() {
		if(!roundRunning)   throw new IllegalStateException("The round is not running.");
		if(playerCashedOut) throw new IllegalStateException("The player already cashed out.");
		
		playerCashedOut = true;
		
		double multiplier3dec = roundToNDecimals(multiplier, 3);
		playerCashOutMultiplier = multiplier3dec;
		updateBankroll(bankroll + (long)(bet * multiplier3dec));
		
		gui.cashOut();
	}
	
	/**
	 * Crashes and resets the round.
	 */
	private void crash() {
		if(!playerCashedOut) {
			gui.crash();
		}

		DataManager.getInstance().saveRound(new Round(bet, playerCashOutMultiplier, crash));
		
		resetRound();
	}
	
	/**
	 * Resets the round to the default settings.
	 */
	private void resetRound() {
		roundRunning = false;
		playerCashedOut = false;
		playerCashOutMultiplier = 0;
		multiplier = STARTING_MULTIPLIER;
		
		gui.reset();
	}
	
	//-----------------------------------------------------------------------
	//							Player data
	//-----------------------------------------------------------------------
	
	private void updateBankroll(long newBankroll) {
		bankroll = newBankroll;
		DataManager.getInstance().updateProperty("bankroll", String.valueOf(bankroll));
		gui.updateBankroll();
	}
	
	//-----------------------------------------------------------------------
	//							"THE" ALGORITHM
	//-----------------------------------------------------------------------
	
	private static double generateCrashMultiplier() {
		double uniform = 1 / (1.0 - Math.random());
		return roundToNDecimals(uniform, 3);
	}
	
	//-----------------------------------------------------------------------
	//							HELPER METHODS
	//-----------------------------------------------------------------------
	
	/**
	 * Helper method for rounding a decimal number to the <i>n</i> decimal places.
	 * For example, for {@code value = 2.468} and {@code n = 2}, the result
	 * will be {@code 2.47}. Please note that the number is being rounded.
	 * 
	 * @param value the value to be rounded
	 * @param nDecimals the number of decimal places
	 * @return the same value as the passed {@code value}, but rounded to <i>n</i>
	 * 		   decimal places
	 */
	private static double roundToNDecimals(double value, int nDecimals) {
		long tenPowerN = 1;
		for(int i = 0; i < nDecimals; i++) {
			tenPowerN *= 10;
		}
		
		value *= tenPowerN;
		value = Math.round(value);
		value /= tenPowerN;
		
		return value;
	}
	
//	@SuppressWarnings("unused")
//	private static double getBestMultiplierOutOfNRounds(int n) {
//		double best = 1.0;
//		
//		for(int i = 0; i < n; i++) {
//			double multiplier = generateCrashMultiplier();
//			
//			if(multiplier > best) {
//				best = multiplier;
//			}
//		}
//		
//		return best;
//	}
//	
//	@SuppressWarnings("unused")
//	private static double getAverageMultiplierOutOfNRounds(int n) {
//		double sum = 0.0;
//		
//		for(int i = 0; i < n; i++) {
//			sum += generateCrashMultiplier();
//		}
//		
//		return sum / n;
//	}
	
	//-----------------------------------------------------------------------
	//						ALGORITHM TESTING AREA
	//-----------------------------------------------------------------------
	
//	@SuppressWarnings("unused")
//	private static final void multiplierAlgorithmTest() {
//		final int SAMPLE_COUNT = 100_000;
//		Set<Double> sorted = new TreeSet<>();
//		
//		for(int i = 0; i < SAMPLE_COUNT; i++) {
//			sorted.add(generateMultiplier());
//		}
//		
//		int counter = 0;
//		double multiplier = 2.0; 
//		
//		sorted.forEach(num -> System.out.println(String.format("%8.2f", num)));
//
//		for(double num : sorted) {
//			if(num < multiplier) {
//				counter++;
//			} else {
//				String fromTo = String.format("%4d - %4d: ", (int)multiplier/2, (int)multiplier);
//				String percentage = String.format("%5.4f", (double)counter / SAMPLE_COUNT * 100);
//				System.out.println(fromTo + percentage + "%");
//				multiplier *= 2;
//				
//				counter = 0;
//			}
//		}
//	}
}