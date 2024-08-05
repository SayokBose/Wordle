import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WordleWordGenerator {
    private static final String FILE_PATH = "src/valid-wordle-words.txt";

    public static String getRandomWord() throws IOException {
        List<String> words = readWordsFromFile(FILE_PATH);
        Random random = new Random();
        int randomIndex = random.nextInt(words.size());
        return words.get(randomIndex);
    }

    public static boolean validateWord(String word) throws IOException {
        List<String> words = readWordsFromFile(FILE_PATH);
        return words.contains(word.toLowerCase()); // Case-insensitive comparison
    }

    private static List<String> readWordsFromFile(String filePath) throws IOException {
        List<String> words = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line.trim());
            }
        }
        return words;
    }

    public static void main(String[] args) {
        try {
            String randomWord = getRandomWord();
            System.out.println("Random word: " + randomWord);

            String testWord = "Test"; // Replace with the word you want to validate
            boolean isValid = validateWord(testWord);
            System.out.println("Validation result for \"" + testWord + "\": " + isValid);
        } catch (IOException e) {
            System.err.println("Error reading words from file: " + e.getMessage());
        }
    }
}
