package bustabit;

/**
 * Encapsulates a single game round data.
 * 
 * @author FICHEKK
 */
public class Round {
	
	/** Holds this round's player bet value. */
	private double bet;
	
	/** Holds the multiplier that player cashed out on. */
	private double cashOut;
	
	/** Holds this round's crash multiplier value. */
	private double crash;
	
	//-----------------------------------------------------------------------
	//							CONSTRUCTOR
	//-----------------------------------------------------------------------
	
	/**
	 * Constructs a new round with the specified player bet.
	 * 
	 * @param bet the player's bet
	 * @param cashOut the player's cash-out multiplier
	 * @param crash the round's crash multiplier
	 */
	public Round(double bet, double cashOut, double crash) {
		this.bet = bet;
		this.cashOut = cashOut;
		this.crash = crash;
	}
	
	//-----------------------------------------------------------------------
	//								GETTERS
	//-----------------------------------------------------------------------
	
	/**
	 * Returns the player's bet for this round.
	 * 
	 * @return the player's bet for this round
	 */
	public double getBet() {
		return bet;
	}
	
	/**
	 * Returns this round's crash value.
	 * 
	 * @return this round's crash value
	 */
	public double getCrash() {
		return crash;
	}
	
	/**
	 * Returns a flag that indicates if the player has
	 * cashed out this round (player won).
	 * 
	 * @return
	 */
	public boolean hasCashedOut() {
		return cashOut > 0;
	}
	
	/**
	 * Returns the player's profit for this round.
	 * 
	 * @return the player's profit for this round
	 */
	public double getProfit() {
		return (cashOut - 1) * bet;
	}
	
	@Override
	public String toString() {
		if(hasCashedOut()) {
			return String.format("BET: %.0f | CASH-OUT: %.3f | CRASH: %.3f | PROFIT: %.0f\r\n", bet, cashOut, crash, getProfit());
			
		} else {
			return String.format("BET: %.0f | CASH-OUT: - | CRASH: %.3f | PROFIT: %.0f\r\n", bet, crash, getProfit());
		}
	}
	
	/**
	 * Returns the compressed representation of a this round.
	 * This method should be used when storing large quantities
	 * of rounds to the persistent memory.
	 * 
	 * @return the compressed representation of a this round
	 */
	public String toStringCompressed() {
		if(hasCashedOut()) {
			return String.format("%.0f %.3f %.3f %.0f\r\n", bet, cashOut, crash, getProfit());
			
		} else {
			return String.format("%.0f - %.3f %.0f\r\n", bet, crash, getProfit());
		}
	}
}
