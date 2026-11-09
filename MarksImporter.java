import java.io.*;
import java.util.*;
import java.util.logging.*;

public class MarksImporter {
    private static final Logger LOGGER =
        Logger.getLogger(MarksImporter.class.getName());

    // Custom unchecked exception
    static class DuplicateStudentException extends RuntimeException {
        DuplicateStudentException(String id) {
            super("Duplicate student number: " + id);
        }
    }

    static void importMarks(String filename) throws IOException {
        Set<String> seen = new HashSet<>();
        int lineNo = 0;

        // try-with-resources: the BufferedReader is closed automatically
        // when the block ends, whether it finishes normally or an
        // exception is thrown.
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                lineNo++;
                try {
                    String[] parts = line.split(",");
                    // TODO 1: exactly 2 fields
                    if (parts.length != 2) {
                        throw new IllegalArgumentException(
                            "Expected 2 fields but found " + parts.length);
                    }
                    String id = parts[0].trim();
                    // TODO 2: parse and validate range 0..100
                    int mark = Integer.parseInt(parts[1].trim());
                    if (mark < 0 || mark > 100) {
                        throw new IllegalArgumentException(
                            "Mark out of range 0..100: " + mark);
                    }
                    // TODO 3: detect repeated student ID
                    if (!seen.add(id)) {
                        throw new DuplicateStudentException(id);
                    }
                    // TODO 4: display accepted record
                    System.out.println("Accepted: " + id + " -> " + mark);

                } catch (NumberFormatException e) {
                    LOGGER.log(Level.WARNING, "Line " + lineNo + ": "
                        + e.getClass().getName() + " - " + e.getMessage());
                    System.out.println("Line " + lineNo
                        + ": mark is not a number; record skipped.");
                } catch (DuplicateStudentException e) {
                    LOGGER.log(Level.WARNING, "Line " + lineNo + ": "
                        + e.getClass().getName() + " - " + e.getMessage());
                    System.out.println("Line " + lineNo
                        + ": student number already imported; record skipped.");
                } catch (IllegalArgumentException e) {
                    LOGGER.log(Level.WARNING, "Line " + lineNo + ": "
                        + e.getClass().getName() + " - " + e.getMessage());
                    System.out.println("Line " + lineNo
                        + ": invalid record (" + e.getMessage() + "); skipped.");
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            importMarks("marks.txt");
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, e.getClass().getName() + " - " + e.getMessage());
            System.out.println("Marks file not found; check the file name.");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getClass().getName() + " - " + e.getMessage());
            System.out.println("Could not read the marks file. Please try again.");
        } catch (DuplicateStudentException | IllegalArgumentException e) {
            LOGGER.log(Level.SEVERE, e.getClass().getName() + " - " + e.getMessage());
            System.out.println("Import stopped because of invalid data.");
        } finally {
            System.out.println("Import attempt finished");
        }
    }
}