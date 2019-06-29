package bustabit;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A layer between the application and the persistent memory.
 * Used to perform the game data manipulation.
 * 
 * @author FICHEKK
 */
public class DataManager {
	
	/** The singleton instance. */
	private static final DataManager dm = new DataManager();
	
	/** Holds all of the properties. */
	private final Map<String, String> propertyMap = new HashMap<>();
	
	/** Path to the player data file. */
	private final Path playerDataPath = Paths.get("player.dat");
	
	/** Path to the rounds data file. */
	private final Path roundsDataPath = Paths.get("rounds.dat");
	
	/** Private constructor, we don't want any more instances. */
	private DataManager() {
		initializeProperties(playerDataPath);
	}
	
	/**
	 * Initializes the player data.
	 * 
	 * @param source the source of the property data
	 */
	private void initializeProperties(Path source) {
		try {
			for(String line : Files.readAllLines(source)) {
				if(line.isBlank()) {
					System.err.println("Line '" + line + "' is blank.");
					continue;
				}
				
				if(!line.contains("=")) {
					System.err.println("Expected separator '=' in line '" + line + "'.");
					continue;
				}
				
				String[] parts = line.split("=");
				
				if(parts.length != 2) {
					System.err.println("Multiple separators '=' in line '" + line + "'.");
					continue;
				}
				
				System.out.println("Loading property: " + parts[0] + " = " + parts[1]);
				propertyMap.put(parts[0], parts[1]);
			}
		} catch (IOException e) {
			System.err.println("Could not load the property data.");
		}
	}

	/**
	 * Returns the {@code PlayerDataManager} singleton instance.
	 * 
	 * @return the {@code DataManager} singleton instance
	 */
	public static final DataManager getInstance() {
		return dm;
	}
	
	/**
	 * Returns the specified property.
	 * @param property the property to be fetched
	 * @return the specified property, or {@code null} if it does not exist
	 */
	public String getProperty(String property) {
		return propertyMap.get(property);
	}
	
	/**
	 * Updates the value of the property and saves it to the
	 * persistent memory.
	 * 
	 * @param property the property to be updated
	 * @param newValue the new value of the property
	 */
	public void updateProperty(String property, String newValue) {
		String currentValue = propertyMap.get(property);
		
		if(currentValue == null) {
			System.err.println("Could not save property '" + property + "' as it does not exist.");
			return;
		}
		
		if(currentValue.equals(newValue)) {
			System.err.println("Property '" + property + "' was not saved, as it did not change.");
			return;
		}
		
		try {
			List<String> lines = Files.readAllLines(playerDataPath, StandardCharsets.UTF_8);
			
			for(int i = 0, len = lines.size(); i < len; i++) {
				if(lines.get(i).trim().startsWith(property)) {
					lines.set(i, property + "=" + newValue);
					break;
				}
			}
			
			Files.write(playerDataPath, lines, StandardCharsets.UTF_8);
			
		} catch (IOException e) {
			System.err.println("Property '" + property + "' was not saved. The save might be deleted or corrupted.");
			return;
		}
	}
	
	/**
	 * Saves the given round data to the persistent memory.
	 * 
	 * @param round the round to be saved
	 */
	public void saveRound(Round round) {
		try {
			if(!Files.exists(roundsDataPath)) {
				Files.write(roundsDataPath, "BET | CASH-OUT | CRASH | PROFIT\r\n".getBytes(), StandardOpenOption.CREATE);
			}
		    Files.write(roundsDataPath, round.toStringCompressed().getBytes(), StandardOpenOption.APPEND);
		    
		} catch (IOException e) {
		    System.err.println("Could not save the round. The save file might be deleted or corrupted.");
		    return;
		}
	}
}
