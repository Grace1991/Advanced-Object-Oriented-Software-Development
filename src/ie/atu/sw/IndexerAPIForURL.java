package ie.atu.sw;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
// import java.lang.

/**
 * Extends the {@link IndexerAPI} class and provides implementation for creating an index from a URL.
 * Utilizes Project Loom's virtual threads to process each sentence in the resource concurrently.
 */
public class IndexerAPIForURL extends IndexerAPI {

    /**
     * Creates an index from a source URL.
     * The createIndex function reads a URL line by line using a Stream, which has O(n) complexity,
     * where n is the number of lines in the URL. For each line, it creates a virtual thread to process the line,
     * which has O(1) complexity. The virtual threads are added to a CopyOnWriteArrayList, which has O(1) complexity for adding an element.
     * Finally, the function waits for all the virtual threads to complete, which has O(n) complexity, where n is the number of virtual threads.
     * Overall, the complexity of the createIndex function is O(n).
     *
     * @param source The URL of the source to be indexed.
     * @throws IOException If there is an error reading the source URL.
     */
    public void createIndex(String source) throws IOException {
        // assume a page is 40 lines
        AtomicInteger lineCount = new AtomicInteger();
        AtomicInteger finishedSentences = new AtomicInteger();

        CopyOnWriteArrayList<Thread> threads = new CopyOnWriteArrayList<>();

        // since source is a URL, we need to to use a BufferedReader
        // then process it line by line using the Stream
        try {
            URL url = new URL(source);

            BufferedReader read = new BufferedReader(new java.io.InputStreamReader(url.openStream()));

            // read the URL line by line using lambda and stream
            read.lines().forEach(text -> {
                Thread thread = Thread.startVirtualThread(() -> processTextFileLine(text, lineCount.getAndIncrement(), lineCount, finishedSentences));
                threads.add(thread);
            });


        } catch (Exception e) {
            throw new IOException("Error reading URL: " + source + ". Error message: " + e.getMessage());
        }

        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}

