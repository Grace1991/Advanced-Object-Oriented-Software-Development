package ie.atu.sw;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * This class is the base class for generating an index from a text file or URL.
 * It provides a dictionary of words and their definitions, and an index of each word and its page numbers.
 * It also provides functions for searching and printing the index.
 */
public abstract class IndexerAPI {

    /**
     * A map representing the dictionary containing words as keys and their corresponding definitions as values.
     * ConcurrentSkipListMap is used to store the dictionary to provide thread safety and efficient access to its elements.
     */
    protected Map<String, List<Definition>> dictionary = new ConcurrentSkipListMap<>();

    /**
     * A map representing the index containing words as keys and their corresponding WordDetail objects as values.
     * ConcurrentSkipListMap is used to store the index to provide thread safety and efficient access to its elements.
     */
    protected Map<String, WordDetail> index = new ConcurrentSkipListMap<>();
    /**
     * A set containing stop words. ConcurrentSkipListSet is used to store the stop words to provide thread safety and efficient access to its elements.
     */
    protected Set<String> stopWords = new ConcurrentSkipListSet<>();

    /**
     * Loads a dictionary file and processes each line to add the word and its definitions to the dictionary.
     * This function  has a complexity of O(n) because the complexity of the operation is directly related to the number of lines in the dictionary file.
     * In the worst case, the function would need to read and process every line in the file, which would take O(n) time, where n is the number of lines in the file.
     * O(n), where n is the number of lines in the dictionary file.
     *
     * @param dictionaryFileName The name of the dictionary file to be loaded.
     * @throws IOException If there is an error reading the file.
     */
    public void loadDictionaryFile(String dictionaryFileName) throws IOException {
        try {
            Files.lines(Path.of(dictionaryFileName)).forEach(text -> Thread.startVirtualThread(() -> processDictionaryFileLine(text)));
        } catch (IOException e) {
            throw new IOException("Error reading dictionary file: " + dictionaryFileName + ". Error message: " + e.getMessage());
        }
    }

    /**
     * Processes a line from the dictionary file to extract the word and its definitions and add them to the dictionary.
     * This function has a time complexity of O(n), where n is the number of characters in the input line. This is because
     * the function iterates through the line once to extract the word and its definitions.
     * O(n), where n is the number of definitions in the line
     *
     * @param line The line to be processed.
     */
    private void processDictionaryFileLine(String line) {
        String[] parts = line.split(",", 2);
        String word = parts[0];
        String definitions = parts[1];

        // split definitions into individual definition strings
        String[] defs = definitions.split("; ");

        dictionary.put(word, new ArrayList<>());
        // parse definition type, colon and space, and definition text

        // use streams and lambdas to do the same thing
        Arrays.stream(defs).forEach(def -> {
            String[] defParts = def.split(": ", 2);
            String type;
            String text;

            if (defParts.length == 1) {
                text = defParts[0];
                Definition previousDefinition = dictionary.get(word).get(dictionary.get(word).size() - 1);
                previousDefinition.setText(previousDefinition.getText() + "; " + text);
                return;
            } else {
                type = defParts[0];
                text = defParts[1];
            }

            Definition definition = new Definition(type, text);
            dictionary.get(word).add(definition);
        });
    }

    /**
     * Loads a file containing stop words and adds them to the stop words set.
     * we need to consider the operations being performed within the function. The function reads a file and adds the contents of the file to a Set.
     * <p>
     * Reading a file typically has a time complexity of O(n), where n is the number of bytes in the file. Adding elements to a Set has an average time complexity of O(1), since the Set data structure typically uses a hash table for efficient element insertion.
     * <p>
     * Therefore, the overall time complexity of the loadStopWordsFile function is O(n), since the time to read the file dominates the time to add the elements to the Set.
     * Same as loadDictionaryFile, this function has a time complexity of O(n), where n is the number of lines in the file.
     * This is because the function iterates through the file once to add each line to the set.
     *
     * @param stopWordsFileName The name of the stop words file to be loaded.
     * @throws IOException If there is an error reading the file.
     */
    public void loadStopWordsFile(String stopWordsFileName) throws IOException {
        try {

            Files.lines(Path.of(stopWordsFileName)).forEach(stopWords::add);
        } catch (IOException e) {
            throw new IOException("Error reading stop words file: " + stopWordsFileName + ". Error message: " + e.getMessage());
        }
    }

    /**
     * Creates the index of words and their occurrences in the text file.
     *
     * @param source The name of the text file to be indexed. It could be a local file or URL.
     * @throws IOException If there is an error reading the file.
     */
    public abstract void createIndex(String source) throws IOException;


    /**
     * Processes a line from the text file to extract words and their page numbers and add them to the index.
     * The time complexity for processTextFileLine is O(n), where n is the length of the input string.
     * This is because the time taken to process the line is directly proportional to the length of the input string.
     * The algorithm iterates through each character in the input string and performs constant time operations on it,
     * so the time taken is directly proportional to the size of the input.
     *
     * @param text              The line of text to be processed.
     * @param lineNumber        The current line number of the text file.
     * @param lineCount         The total number of lines in the text file.
     * @param finishedSentences The number of sentences that have been processed.
     */
    protected void processTextFileLine(String text, int lineNumber, AtomicInteger lineCount, AtomicInteger finishedSentences) {
        // clean line from punctuation and lowercase
        text = text.replaceAll("[^a-zA-Z ]", "").toLowerCase();

        // split line into words
        String[] words = text.split(" ");


        Arrays.stream(words).forEach(word -> {

            // look for word in dictionary ignoring case
            List<Definition> definition = dictionary.entrySet().stream().filter(entry -> entry.getKey().equalsIgnoreCase(word)).findFirst().map(Map.Entry::getValue).orElse(null);

            // if word is not in dictionary, ignore it
            if (definition == null) {
                return;
            }

            // if word is in stop words, ignore it
            if (stopWords.contains(word)) {
                return;
            }
            float pageFloat = (float) Math.ceil((float) lineNumber / 40f);
            int page = (int) pageFloat;
            // if word is in index, just add page number to list, since definition is already there
            if (index.containsKey(word)) {
                index.get(word).addPage(page);
            } else {
                // if word is not in index, add word to index with page number
                WordDetail wordDetail = new WordDetail();
                wordDetail.addPage(page);

                // definitionString should have the following information for each definition:
                // "<word>","<type>","<definition>"
                // note that word, type and definition should be enclosed in double quotes
                String definitionsString = definition.stream().map(def -> "\"" + word + "\",\"" + def.getType() + "\",\"" + def.getText() + "\"").collect(Collectors.joining("\n"));
                wordDetail.setDefinition(definitionsString);
                index.put(word, wordDetail);
            }
        });

        Runner.printProgress(finishedSentences.incrementAndGet(), lineCount.get());
    }

    /**
     * Outputs the index to a file.
     *
     * @param fileName The name of the file to output the index to.
     * @throws IOException If there is an error writing to the file.
     */
    public void outputIndexToFile(String fileName) throws IOException {
        // create file
        File file = new File(fileName);
        file.createNewFile();

        // write index to file
        Files.writeString(Path.of(fileName), this.toString());
    }


    /**
     * Outputs all words in the index sorted alphabetically to a file.
     * O(n), where n is the number of elements in the index map.
     * This is because the function performs a single pass over the index map to sort the keys
     * and then another pass to iterate over the sorted keys to create the output string.
     *
     * @param outputFileName The name of the file to output the sorted words to.
     * @throws IOException If there is an error writing to the file.
     */
    public void getAllWordsSorted(String outputFileName) throws IOException {
        // 5 words per line
        AtomicInteger counter = new AtomicInteger(0);
        String outputStr = index.keySet().stream().sorted().map(word -> {
            if (counter.get() == 5) {
                counter.set(0);
                return word + "\n";

            } else {
                counter.getAndIncrement();
                return word + ", ";
            }
        }).collect(Collectors.joining());


        try {
            Files.writeString(Path.of(outputFileName), outputStr);
        } catch (IOException e) {
            throw new IOException("Error writing to file: " + outputFileName + ". Error message: " + e.getMessage());
        }
    }

    /**
     * Outputs all words in the index reverse sorted alphabetically to a file.
     * O(n). This is because the method iterates through the index map and performs a single action on each entry,
     * resulting in a time complexity of O(n), where n is the number of entries in the map.
     *
     * @param outputFileName The name of the file to output the reverse sorted words to.
     * @throws IOException If there is an error writing to the file.
     */
    public void getAllWordsReverseSorted(String outputFileName) throws IOException {
        // 5 words per line
        AtomicInteger counter = new AtomicInteger(0);
        String outputStr = index.keySet().stream().sorted(Comparator.reverseOrder()).map(word -> {
            if (counter.get() == 5) {
                counter.set(0);
                return word + "\n";

            } else {
                counter.getAndIncrement();
                return word + ", ";
            }
        }).collect(Collectors.joining());

        try {
            Files.writeString(Path.of(outputFileName), outputStr);
        } catch (IOException e) {
            throw new IOException("Error writing to file: " + outputFileName + ". Error message: " + e.getMessage());
        }
    }

    /**
     * Returns the number of unique words in the index.
     * O(1) time complexity because the size of the index is stored in a variable.
     *
     * @return The number of unique words in the index.
     */
    public int getUniqueWordCount() {
        return index.size();
    }

    /**
     * Returns the total word count in the index.
     * O(n) time complexity because the total word count is calculated by iterating through the index.
     *
     * @return An integer representing the total word count in the index.
     */
    public int getWordCount() {
        return index.values().stream().mapToInt(wordDetail -> wordDetail.getPages().size()).sum();
    }

    /**
     * Returns the top n most frequent words in the index.
     * O(nlogn) due to the use of the sorted method, which uses a comparison-based sorting algorithm.
     *
     * @param n The number of most frequent words to return.
     * @return A {@link String} containing the top n most frequent words and their frequency.
     */
    public String getTopNMostFrequent(int n) {
        // order by frequency

        Map<String, Integer> wordFrequency = new HashMap<>();
        index.forEach((word, wordDetail) -> wordFrequency.put(word, wordDetail.getPages().size()));

        String[] top = wordFrequency.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(n).map(Map.Entry::getKey).toArray(String[]::new);
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < top.length; i++) {
            result.append("#").append(i + 1).append(": ").append(top[i]).append(" - ").append(wordFrequency.get(top[i])).append(" ocurrences\n");
        }

        return result.toString();
    }


    /**
     * Returns the top n least frequent words in the index.
     * O(nlogn) due to the use of the sorted method, which uses a comparison-based sorting algorithm.
     *
     * @param n The number of least frequent words to return.
     * @return A {@link String} containing the top n least frequent words and their frequency.
     */
    public String getTopNLeastFrequent(int n) {
        Map<String, Integer> wordFrequency = new HashMap<>();
        index.forEach((word, wordDetail) -> wordFrequency.put(word, wordDetail.getPages().size()));

        // top n least frequent words
        String[] top = wordFrequency.entrySet().stream().sorted(Map.Entry.comparingByValue()).limit(n).map(Map.Entry::getKey).toArray(String[]::new);
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < top.length; i++) {
            result.append("#").append(i + 1).append(": ").append(top[i]).append(" - ").append(wordFrequency.get(top[i])).append(" ocurrence(s)\n");
        }

        return result.toString();

    }

    /**
     * Returns the index as a {@link String}.
     * Every word in the index has its corresponding page numbers and definitions.
     * O(n) because it iterates through each element in the index map once.
     *
     * @return A {@link String} representation of the index.
     */
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        // format index to string like this
        /*
        word      |    details
        -----------------------
        orchard   |   Definitions:
                  |   "Orchard", "n.", "A garden."
                  |   "Orchard", "n.", "A place where fruit trees are grown."
                  |
                  |   Pages:
                  |   {1, 5}
         */

        // output header
        output.append(String.format("%-20s | %s%n", "word", "details"));
        output.append(String.format("%-20s | %s%n", "--------------------", "--------------------"));


        for (String word : index.keySet()) {
            // output definitions
            output.append(String.format("%-20s | %s%n", word, "Definitions:"));

            // there could be multiple definitions for a word divided by a new line
            // first split the definitions string by new line
            // then print each definition on a new line
            String[] definitions = index.get(word).getDefinition().toString().split("\n");
            for (String definition : definitions) {
                output.append(String.format("%-20s | %s%n", " ", definition));
            }

            output.append(String.format("%-20s | %s%n", "", ""));
            output.append(String.format("%-20s | %s%n", " ", "Pages:"));

            // print unique page numbers
            output.append(String.format("%-20s | %s%n", " ", new TreeSet(index.get(word).getPages()) {
            }));
            output.append(String.format("%-20s---%s%n", "--------------------", "--------------------"));
        }
        return output.toString();
    }
}