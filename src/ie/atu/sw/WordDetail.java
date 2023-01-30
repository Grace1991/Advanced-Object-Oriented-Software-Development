package ie.atu.sw;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Class representing a word and its details, including definition and list of pages on which it appears.
 * Uses thread-safe classes for definition and pages.
 */
public class WordDetail {
    /**
     * String containing all the definitions of the word.
     */
    StringBuffer definition = new StringBuffer(); // thread safe
    /**
     * Set of pages on which the word appears. Uses ConcurrentSkipListSet implementation to ensure thread safety.
     */
    Queue<Integer> pages; // thread safe, efficient

    /**
     * Constructor for WordDetail. Initializes an empty queue of pages.
     */
    public WordDetail() {
        this.pages = new ConcurrentLinkedQueue<>();
    }

    /**
     * Returns the definition of the word as a {@link StringBuffer}.
     *
     * @return definition of word as a StringBuffer
     */
    public StringBuffer getDefinition() {
        return definition;
    }

    /**
     * Sets the definition of the word.
     *
     * @param definition new definition of word
     */
    public void setDefinition(StringBuffer definition) {
        this.definition = definition;
    }

    /**
     * Returns the queue of pages on which the word appears.
     *
     * @return queue of pages on which word appears
     */
    public Queue<Integer> getPages() {
        return pages;

    }

    /**
     * Adds a page number to the queue of pages on which the word appears.
     * O(1) operation. Since the queue has a fixed size, the worst case is O(n).
     *
     * @param page page number to add to queue
     */
    public void addPage(int page) {
        pages.add(page);
    }


    /**
     * Sets the definition of the word.
     *
     * @param definition new definition of word
     */
    public void setDefinition(String definition) {
        this.definition.append(definition);
    }

    /**
     * Returns the definition of the word as a {@link String}.
     *
     * @return definition of word as a String
     */
    @Override
    public String toString() {
        return "WordDetail{" +
                "definition=" + definition +
                ", pages=" + pages +
                '}';
    }
}
