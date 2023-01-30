package ie.atu.sw;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
// import java.lang.


/**
 * Extends the {@link IndexerAPI} class and provides implementation for creating an index from a given file name.
 * Utilizes Project Loom's virtual threads to process each sentence in the resource concurrently.
 */
public class IndexerAPIForFile extends IndexerAPI {

    /**
     * Creates an index of a text file with the given name.
     * O(n). It reads the file line by line using the Files.lines method, which returns a Stream of lines in the file.
     * The forEach method is called on the Stream, which iterates through each line in the file and processes it with the processTextFileLine method.
     * The time complexity of the forEach method is O(n) because it processes n lines in the file.
     *
     * @param textFileName The name of the text file to create an index for.
     * @throws IOException If there is an error reading the file.
     */
    public void createIndex(String textFileName) throws IOException {
        // assume a page is 40 lines
        AtomicInteger lineCount = new AtomicInteger();
        AtomicInteger finishedSentences = new AtomicInteger();

        CopyOnWriteArrayList<Thread> threads = new CopyOnWriteArrayList<>();
        // load text file
        try {
            Files.lines(Path.of(textFileName)).forEach(text -> {

                Thread thread = Thread.startVirtualThread(() -> processTextFileLine(text, lineCount.getAndIncrement(), lineCount, finishedSentences));
                threads.add(thread);
            });

            threads.forEach(thread -> {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            throw new IOException("Error reading file: " + textFileName + ". Error message: " + e.getMessage());
        }
    }
}

