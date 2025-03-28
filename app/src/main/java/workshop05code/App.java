package workshop05code;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author sqlitetutorial.net
 */
public class App {
    // Start code for logging exercise
    static {
        // must set before the Logger
        // loads logging.properties from the classpath
        try {// resources\logging.properties
            LogManager.getLogManager().readConfiguration(new FileInputStream("resources/logging.properties"));
        } catch (SecurityException | IOException e1) {
            System.err.println("Failed to configure logger. Default settings will be used.");
        }
    }

    private static final Logger logger = Logger.getLogger(App.class.getName());
    // End code for logging exercise
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SQLiteConnectionManager wordleDatabaseConnection = new SQLiteConnectionManager("words.db");

        wordleDatabaseConnection.createNewDatabase("words.db");
        if (wordleDatabaseConnection.checkIfConnectionDefined()) {
            System.out.println("Wordle created and connected.");
        } else {
            System.out.println("Not able to connect. Sorry!");
            return;
        }
        if (wordleDatabaseConnection.createWordleTables()) {
            System.out.println("Wordle structures in place.");
        } else {
            System.out.println("Not able to launch. Sorry!");
            return;
        }

        // let's add some words to valid 4 letter words from the data.txt file

        try (BufferedReader br = new BufferedReader(new FileReader("resources/data.txt"))) {
            String line;
            int i = 1;
            while ((line = br.readLine()) != null) {
                if(isValidWordFormat(line)) {
                    wordleDatabaseConnection.addValidWord(i, line);
                    logger.log(Level.INFO, "Valid word added: {0}", line);
                    i++;
                } else {
                    logger.log(Level.SEVERE, "Invalid word in data.txt: {0}", line);
                }
            }

        } catch (IOException e) {
            logger.log(Level.WARNING, "Error reading words from data.txt", e);
            return;
        }

        // let's get them to enter a word

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("Enter a 4-letter word for a guess or 'q' to quit: ");
                String guess = scanner.nextLine();

                if (guess.equals("q")) break;

                if (!isValidWordFormat(guess)) {
                    System.out.println("Invalid input! Enter exactly 4 lowercase letters (a-z).\n");
                    logger.log(Level.WARNING, "Invalid guess: {0}", guess);
                    continue;
                }

                System.out.println("You've guessed '" + guess + "'.");

                if (wordleDatabaseConnection.isValidWord(guess)) {
                    System.out.println("Success! It is in the list.\n");
                } else {
                    System.out.println("Sorry. This word is NOT in the list.\n");
                }
            }
        } catch (NoSuchElementException | IllegalStateException e) {
            logger.log(Level.WARNING, "Scanner error in user input loop", e);
        }

    }
    private static boolean isValidWordFormat(String word) {
        return word.matches("[a-z]{4}");
    }
}