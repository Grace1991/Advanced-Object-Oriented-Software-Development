package ie.atu.sw;

import java.util.Objects;
import java.util.Scanner;


/**
 * A class that shows the menu and allows the user to select an option.
 * Allows the user to interact with the index generated from a given dictionary, stopwords and text file.
 */
public class Runner {
    /**
     * The name of the text file or URL to be indexed.
     */
    private static String sourceInput = "";


    /**
     * Boolean to check if the user wants to index a text file or a URL.
     */
    private static boolean isURL = false;
    /**
     * The name of the dictionary file containing the words and definitions.
     */
    private static String dictionaryFileName = "dictionary.csv";
    /**
     * The name of the file containing the common words to be excluded from the index.
     */
    private static String commonWordsFileName = "google-1000.txt";
    /**
     * The name of the output file where the indexed words and definitions will be stored.
     */
    private static String outputFileName = "index.txt";
    /**
     * A scanner object for reading user input.
     */
    private static Scanner scanner = new Scanner(System.in);
    /**
     * The IndexerAPI object for creating the index.
     */
    private static IndexerAPI indexer = null;
    /**
     * A flag for controlling the display of the menu. Used to exit the program.
     */
    private static boolean showMenu = true;

    /**
     * Reads an integer from the console within the specified range.
     *
     * @param inputMessage message to display to the user before reading input
     * @param min          minimum value allowed
     * @param max          maximum value allowed
     * @return the integer value read from the console
     */
    public static int readInt(String inputMessage, int min, int max) {
        int value = 0;
        boolean valid = false;
        while (!valid) {
            try {
                System.out.print(inputMessage);
                value = Integer.parseInt(scanner.nextLine());
                if (value >= min && value <= max) {
                    valid = true;
                } else {
                    System.out.print(ConsoleColour.RED_BOLD_BRIGHT);
                    System.out.println("Invalid value. Please enter a value between " + min + " and " + max);
                    System.out.print(ConsoleColour.RESET);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid value. Please enter a value between " + min + " and " + max);
            }
        }
        return value;
    }

    /**
     * Prints the main menu for the Virtual Threaded Text Indexer program and handles user input for menu options.
     * The menu will continue to be displayed until the user selects the Quit option.
     *
     * @throws Exception if an error occurs while handling the menu option
     */
    public static void main(String[] args) throws Exception {
        // You should put the following code into a menu or Menu class


        while (showMenu) {

            System.out.println(ConsoleColour.WHITE);
            System.out.println("************************************************************");
            System.out.println("*       ATU - Dept. Computer Science & Applied Physics     *");
            System.out.println("*                                                          *");
            System.out.println("*              Virtual Threaded Text Indexer               *");
            System.out.println("*                                                          *");
            System.out.println("************************************************************");
            System.out.println("(1) Specify Text File");
            System.out.println("(2) Configure Dictionary");
            System.out.println("(3) Configure Common Words");
            System.out.println("(4) Specify Output File");
            System.out.println("(5) Execute");
            System.out.println("(6) Apply Methods to Created Index");
            System.out.println("(7) Quit");

            // Output a menu of options and solicit text from the user


            // read only valid options
            int option = 0;
            boolean isValid = false;


            while (!isValid) {
                try {
                    System.out.print(ConsoleColour.BLACK_BOLD_BRIGHT);
                    System.out.print("Select Option [1-7]> ");
                    System.out.print(ConsoleColour.WHITE);
                    String input = scanner.nextLine();
                    option = Integer.parseInt(input);
                    if (option >= 1 && option <= 7) {
                        isValid = true;
                    } else {
                        System.out.print(ConsoleColour.RED_BOLD_BRIGHT);
                        System.out.println("Invalid option. Select option 1-7");
                        System.out.print(ConsoleColour.WHITE);
                    }

                } catch (Exception e) {
                    System.out.print(ConsoleColour.RED_BOLD_BRIGHT);
                    System.out.println("Invalid option. Select Option [1-7]");
                    System.out.print(ConsoleColour.WHITE);
                }
            }
            System.out.println();
            handleMenuOption(option);
        }

        System.out.println("Goodbye!");

    }

    /**
     * This method handles a selected menu option from the main menu of the program. It allows the user to specify a text file, configure the dictionary file,
     * configure the common words file, specify an output file, execute the indexing process, and apply methods to the created index. It also allows the user
     * to quit the program.
     *
     * @param option An integer representing the selected menu option.
     */
    private static void handleMenuOption(int option) {
        if (option == 1) {
//            System.out.print(ConsoleColour.BLACK_BOLD_BRIGHT);
//            System.out.print("Enter New Text File Name> ");
//            System.out.print(ConsoleColour.WHITE);

            boolean isValidSource = false;
            while (!isValidSource) {
                System.out.print("Is it a URL or text file? (U for URL, F for text file)> ");
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("U")) {
                    isURL = true;
                    isValidSource = true;
                } else if (input.equalsIgnoreCase("F")) {
                    isURL = false;
                    isValidSource = true;
                } else {
                    System.out.println("Invalid option. Please enter U or F");
                }
            }

            if (isURL) {
                System.out.print("Enter URL> ");
                String input = scanner.nextLine();
                sourceInput = input;
            } else {
                System.out.print("Enter New Text File Name> ");
                String input = scanner.nextLine();
                sourceInput = input;
            }

        } else if (option == 2) {
            System.out.print(ConsoleColour.BLACK_BOLD_BRIGHT);
            System.out.print("Enter New Dictionary File Name> ");
            System.out.print(ConsoleColour.WHITE);
            dictionaryFileName = scanner.nextLine();
        } else if (option == 3) {
            System.out.print(ConsoleColour.BLACK_BOLD_BRIGHT);
            System.out.print("Enter New Common Words File Name> ");
            System.out.print(ConsoleColour.WHITE);
            commonWordsFileName = scanner.nextLine();
        } else if (option == 4) {
            System.out.print(ConsoleColour.BLACK_BOLD_BRIGHT);
            System.out.print("Enter New Output File Name> ");
            System.out.print(ConsoleColour.WHITE);
            outputFileName = scanner.nextLine();
        } else if (option == 5) {
            System.out.println("Executing...");
            // check if dictionary file is set and exists
            if (Objects.equals(dictionaryFileName, "")) {
                System.out.print(ConsoleColour.YELLOW_BOLD_BRIGHT);
                System.out.println("Dictionary file not set. Using default dictionary.csv");
                System.out.print(ConsoleColour.WHITE);

                dictionaryFileName = "dictionary.csv";
            } else {
                System.out.println("Using dictionary file: " + dictionaryFileName);
            }

            // check if common words file is set and exists
            if (Objects.equals(commonWordsFileName, "")) {
                System.out.print(ConsoleColour.YELLOW_BOLD_BRIGHT);
                System.out.println("Common words file not set. Using default google-1000.txt");
                System.out.print(ConsoleColour.WHITE);

                commonWordsFileName = "google-1000.txt";
            } else {
                System.out.println("Using common words file: " + commonWordsFileName);
            }

            // check if text file is set and exists
            if (Objects.equals(sourceInput, "")) {
                System.out.print(ConsoleColour.RED_BOLD_BRIGHT);
                System.out.println("Text file or URL not set. Please specify a text file or URL.");
                System.out.print(ConsoleColour.WHITE);
                return;
            } else {
                if (isURL) {
                    System.out.println("Using URL: " + sourceInput);
                } else {
                    System.out.println("Using text file: " + sourceInput);
                }
            }

            // check if output file is set and exists
            if (Objects.equals(outputFileName, "")) {
                System.out.print(ConsoleColour.YELLOW_BOLD_BRIGHT);
                System.out.println("Output file not set. Using default index.txt");
                System.out.print(ConsoleColour.WHITE);

                outputFileName = "index.txt";
            } else {
                System.out.println("Using output file: " + outputFileName);
            }

            // polymorphism
            if (isURL) {
                indexer = new IndexerAPIForURL();
            } else {
                indexer = new IndexerAPIForFile();
            }

            try {
                indexer.loadStopWordsFile(commonWordsFileName);
                indexer.loadDictionaryFile(dictionaryFileName);
                System.out.print(ConsoleColour.YELLOW);
                indexer.createIndex(sourceInput);
                System.out.print(ConsoleColour.WHITE);
            } catch (Exception e) {
                System.out.print(ConsoleColour.RED_BOLD_BRIGHT);
                System.out.println(e.getMessage());
                System.out.print(ConsoleColour.WHITE);
                indexer = null;
                return;
            }

            System.out.println("\nIndexing complete. Writing to file...");

            try {
                indexer.outputIndexToFile(outputFileName);
            } catch (Exception e) {
                System.out.print(ConsoleColour.RED_BOLD_BRIGHT);
                System.out.println(e.getMessage());
                System.out.print(ConsoleColour.WHITE);
                return;
            }

            System.out.println("Index written to file: " + outputFileName);
//            indexer.writeIndexToFile(outputFileName);

        } else if (option == 6) {
            if (indexer == null) {
                System.out.print(ConsoleColour.RED_BOLD_BRIGHT);
                System.out.println("Index not created. Please execute first.");
                System.out.print(ConsoleColour.WHITE);
                return;
            }

            handleAPIMethods();


        } else {
            showMenu = false;
        }
    }

    /**
     * Prompts the user to select a method from a list of available methods, and then executes the chosen method.
     * Available methods include:
     * 1. Get all words in the text ordered alphabetically
     * 2. Get all words in the text reverse ordered alphabetically
     * 3. Get unique words count
     * 4. Get total words count
     * 5. Get Top N most frequent words
     * 6. Get Top N least frequent words
     * 7. Execute all methods (Summary)
     */
    private static void handleAPIMethods() {
        System.out.println("************************************************************");
        // print "Available Methods" with corresponding spaces to the left and right
        System.out.println("*                                                          *");
        System.out.println("*                 Available Methods                        *");
        System.out.println("*                                                          *");
        System.out.println("************************************************************");

        System.out.println("1. Get all words in the text ordered alphabetically");
        System.out.println("2. Get all words in the text reverse ordered alphabetically");
        System.out.println("3. Get unique words count");
        System.out.println("4. Get total words count");
        System.out.println("5. Get Top N most frequent words");
        System.out.println("6. Get Top N least frequent words");
        System.out.println("7. Execute all methods (Summary)");

        System.out.println();

        boolean isValidMethod = false;
        int method = 0;

        while (!isValidMethod) {
            try {
                System.out.print(ConsoleColour.BLACK_BOLD_BRIGHT);
                System.out.print("Select Method [1-7]> ");
                System.out.print(ConsoleColour.WHITE);
                String input = scanner.nextLine();
                method = Integer.parseInt(input);
                if (method >= 1 && method <= 7) {
                    isValidMethod = true;
                } else {
                    System.out.print(ConsoleColour.RED_BOLD_BRIGHT);
                    System.out.println("Invalid method. Select method 1-7");
                    System.out.print(ConsoleColour.WHITE);
                }

            } catch (Exception e) {
                System.out.print(ConsoleColour.RED_BOLD_BRIGHT);
                System.out.println("Invalid method. Select Method [1-7]");
                System.out.print(ConsoleColour.WHITE);
            }
        }

        switch (method) {
            case 1:
                System.out.print("Enter Output File Name> ");
                String wordsSortedFilename = scanner.nextLine();

                try {
                    indexer.getAllWordsSorted(wordsSortedFilename);
                    System.out.println("Words sorted alphabetically written to " + wordsSortedFilename);
                } catch (Exception e) {
                    System.out.print(ConsoleColour.RED_BOLD_BRIGHT);
                    System.out.println(e.getMessage());
                    System.out.print(ConsoleColour.WHITE);
                }

                break;
            case 2:
                System.out.print("Enter Output File Name> ");
                String wordsReverseSortedFilename = scanner.nextLine();
                try {
                    indexer.getAllWordsReverseSorted(wordsReverseSortedFilename);
                    System.out.println("Words reverse sorted alphabetically written to " + wordsReverseSortedFilename);
                } catch (Exception e) {
                    System.out.print(ConsoleColour.RED_BOLD_BRIGHT);
                    System.out.println(e.getMessage());
                    System.out.print(ConsoleColour.WHITE);
                }
                break;
            case 3:
                System.out.println("Unique words count: " + indexer.getUniqueWordCount());
                break;
            case 4:
                System.out.println("Total words count: " + indexer.getWordCount());
                break;
            case 5:
                int n = readInt("Enter N> ", 1, Integer.MAX_VALUE);
                System.out.println("Top " + n + " most frequent words:");
                System.out.println(indexer.getTopNMostFrequent(n));
                break;
            case 6:
                int n2 = readInt("Enter N> ", 1, Integer.MAX_VALUE);
                System.out.println("Top " + n2 + " least frequent words:");
                System.out.println(indexer.getTopNLeastFrequent(n2));
                break;
            case 7:
                try {
                    System.out.println("********************\n");
                    System.out.println("1. All words in the text file ordered");
                    wordsSortedFilename = "words-sorted-alphabetically.txt";
                    indexer.getAllWordsSorted(wordsSortedFilename);
                    System.out.println("Words sorted alphabetically written to " + wordsSortedFilename);
                    System.out.println("\n********************\n");
                    System.out.println("2. All words in the text file reverse ordered");
                    wordsReverseSortedFilename = "words-sorted-reverse-alphabetically.txt";
                    indexer.getAllWordsReverseSorted(wordsReverseSortedFilename);
                    System.out.println("Words reverse sorted alphabetically written to " + wordsReverseSortedFilename);
                    System.out.println("\n********************\n");
                    System.out.println("3. Unique words count: " + indexer.getUniqueWordCount());
                    System.out.println("\n********************\n");
                    System.out.println("4. Total words count: " + indexer.getWordCount());
                    System.out.println("\n********************\n");
                    System.out.println("5. Top 10 most frequent words");
                    System.out.println(indexer.getTopNMostFrequent(10));
                    System.out.println("\n********************\n");
                    System.out.println("6. Top 10 least frequent words");
                    System.out.println(indexer.getTopNLeastFrequent(10));

                } catch (Exception e) {
                    System.out.print(ConsoleColour.RED_BOLD_BRIGHT);
                    System.out.println(e.getMessage());
                    System.out.print(ConsoleColour.WHITE);
                    return;
                }
                break;
        }
    }

    /**
     * Terminal Progress Meter
     * -----------------------
     * You might find the progress meter below useful. The progress effect
     * works best if you call this method from inside a loop and do not call
     * System.out.println(....) until the progress meter is finished.
     * <p>
     * Please note the following carefully:
     * <p>
     * 1) The progress meter will NOT work in the Eclipse console, but will
     * work on Windows (DOS), Mac and Linux terminals.
     * <p>
     * 2) The meter works by using the line feed character "\r" to return to
     * the start of the current line and writes out the updated progress
     * over the existing information. If you output any text between
     * calling this method, i.e. System.out.println(....), then the next
     * call to the progress meter will output the status to the next line.
     * <p>
     * 3) If the variable size is greater than the terminal width, a new line
     * escape character "\n" will be automatically added and the meter won't
     * work properly.
     */
    public static void printProgress(int index, int total) {
        if (index > total) return; // Out of range
        int size = 50; // Must be less than console width
        char done = '█'; // Change to whatever you like.
        char todo = '░'; // Change to whatever you like.

        // Compute basic metrics for the meter
        int complete = (100 * index) / total;
        int completeLen = size * complete / 100;

        /*
         * A StringBuilder should be used for string concatenation inside a
         * loop. However, as the number of loop iterations is small, using
         * the "+" operator may be more efficient as the instructions can
         * be optimized by the compiler. Either way, the performance overhead
         * will be marginal.
         */
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < size; i++) {
            sb.append((i < completeLen) ? done : todo);
        }

        /*
         * The line feed escape character "\r" returns the cursor to the
         * start of the current line. Calling print(...) overwrites the
         * existing line and creates the illusion of an animation.
         */
        System.out.print("\r" + sb + "] " + complete + "%");

        // Once the meter reaches its max, move to a new line.
        if (done == total) System.out.println("\n");
    }
}