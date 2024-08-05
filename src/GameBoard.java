import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import java.util.LinkedList;
import java.io.IOException;
import javafx.scene.Node;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.control.Tooltip;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.FileWriter;
import java.nio.file.Path;
import javafx.stage.Popup;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.control.Button;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javafx.scene.layout.HBox;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;


public class GameBoard {
    private Label[][] letterLabels = new Label[6][5];
    private GridPane gridPane;
    private int currRow;
    private int currCol;
    private Word answer;
    private int resetCount; 
    private int winCount; 
    private HBox keyboardRow1;
    private HBox keyboardRow2;
    private HBox keyboardRow3;
    private int guess1;
    private int guess2;
    private int guess3;
    private int guess4;
    private int guess5;
    private int guess6;
    

    public GameBoard(GridPane gridPane, HBox kr1, HBox kr2, HBox kr3) {
        this.gridPane = gridPane;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("stats.txt", true))) {
            String statsContent = Files.readString(Paths.get("stats.txt"));
            int timesPlayed = extractStat(statsContent, "Times Played:");
            int timesWon = extractStat(statsContent, "Times Won:");

            resetCount = timesPlayed;
            
            winCount = timesWon;

            int guess1_ = extractStat(statsContent, "Guess 1:");
            int guess2_ = extractStat(statsContent, "Guess 2:");
            int guess3_ = extractStat(statsContent, "Guess 3:");
            int guess4_ = extractStat(statsContent, "Guess 4:");
            int guess5_ = extractStat(statsContent, "Guess 5:");
            int guess6_ = extractStat(statsContent, "Guess 6:");

            guess1 = guess1_;
            guess2 = guess2_;
            guess3 = guess3_;
            guess4 = guess4_;
            guess5 = guess5_;
            guess6 = guess6_;
            

            
        } catch (IOException e) {
            System.err.println("Error writing stats to file: " + e.getMessage());
        }
        currRow = 0;
        
        keyboardRow1 = kr1;
        keyboardRow2 = kr2;
        keyboardRow3 = kr3;
        clearStatsFile("words.txt"); // Clear the stats file
        initializeLetterLabels();
        try {
            String ans = WordleWordGenerator.getRandomWord();
            System.out.println("The answer is " + ans);
            
            answer = new Word(ans);
        } catch (IOException e) {
            System.err.println("Error reading words from file: " + e.getMessage());
        }
    }
    public void setGuess1(int val){
        guess1 = val;
    }

    public void setGuess2(int val) {
        guess2 = val;
    }

    public void setGuess3(int val) {
        guess3 = val;
    }

    public void setGuess4(int val) {
        guess4 = val;
    }

    public void setGuess5(int val) {
        guess5 = val;
    }

    public void setGuess6(int val) {
        guess6 = val;
    }

    public void setAnswer(String ans){
        answer = new Word(ans);
        System.out.println("The answer is " + ans);
    }

    public void setWinCount(int val){
        winCount = val;
    }

    public void setWinReset(int val){
        resetCount = val;
    }
    private void initializeLetterLabels() {
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 5; col++) {
                Label label = new Label();
                label.getStyleClass().add("letter-label");
                gridPane.add(label, col, row);
                letterLabels[row][col] = label;
            }
        }
    }

    public void addWord(String str) {
        
        

        for (char letterC : str.toCharArray()) {
            String letter = String.valueOf(letterC); // Convert char to String
            addLetter(letter);
        }
    }

    public void addLetter(String letter){
    
        // Find the first empty label in the current row and fill it with the pressed letter
        for (int col = 0; col < 5; col++) {
            if (isEmpty(currRow, col)) { // Assume isEmpty method checks if the label is empty
                currCol++;
                updateLetterLabel(currRow, col, letter, "#FFFFFF"); // Assume updateLetter method updates the label at the specified row and column
                return;
            }
        }
    }

    public void resetKeyboardColors() {
        resetRowColors(keyboardRow1);
        resetRowColors(keyboardRow2);
        resetRowColors(keyboardRow3);
    }
    
    private void resetRowColors(HBox keyboardRow) {
        for (Node node : keyboardRow.getChildren()) {
            if (node instanceof Button) {
                Button button = (Button) node;
                button.setStyle("-fx-background-color: #D3D3D3;"); // Reset background color to default
            }
        }
    }

    private void clearStatsFile(String name) {
        try {
            Files.write(Paths.get(name), "".getBytes());
        } catch (IOException e) {
            System.err.println("Error clearing stats file: " + e.getMessage());
        }
    }

    public void removeLetter(){
    
        // Find the first empty label in the current row and fill it with the pressed letter
       
        if (currCol >= 1 && currCol < 6) {
            currCol--;
            // Update the label at the specified column in the current row with an empty string
            updateLetterLabel(currRow, currCol, "", "#FFFFFF"); // Assuming updateLetterLabel method updates the label at the specified row and column with the new letter
        }
    }

    public Map<Character, String> check(){
        Map<Character, String> letterColors = new HashMap<>();
        //take currRow and turn into string
        String currRowAsString = getRowAsString(currRow);
        if(currRowAsString.length() != 5){
            showPopup("Not Long enough", 1000);
            return null;
            //pop up saying yo its too short
        }
        else{
            //boolean validWord = false;
            boolean validWord = false;
            try{
                validWord = WordleWordGenerator.validateWord(currRowAsString);
            }catch (IOException e) {
                System.err.println("Error reading words from file: " + e.getMessage());
            }
            //comment out this is for testing
            
            if(validWord == true){
                Word currWord = new Word(currRowAsString);
                
                //compare against the anwer
                boolean winCheck = currWord.compareTo(answer);
                if(winCheck == true){
                    winCount++;
                    if(currRow == 0){
                        guess1++;
                    }
                    else if(currRow == 1){
                        guess2++;
                    }
                    else if(currRow == 2){
                        guess3++;
                    }
                    else if(currRow == 3){
                        guess4++;
                    }
                    else if(currRow == 4){
                        guess5++;
                    }
                    else if(currRow == 5){
                        guess6++;
                    }

                    System.out.println("You won");
                    showResultPopup(true);
                }
                //based on how it compares update the items
                updateLabelStyles(currWord);
                storeStats(currWord);
                saveStats();

                


                for (int i = 0; i < 5; i++) {
                    Letter letter = currWord.getword().get(i);
                    char character = letter.getValue().charAt(0);
                    String color = letter.getColor();
                    letterColors.put(character, color);
                }
                //move to next row
                currRow++;
                currCol = 0;
                if(currRow == 6){
                    System.out.println("You lost");
                    showResultPopup(false);
                }
                return letterColors;
            }else{
                //handle it not being a correct wordw
                showPopup("Word not in word list", 1000);
                return null;
            }
        }
        
        
    }

    private void storeStats(Word word) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("words.txt", true))) {
            writer.write(word.getActualWord() + ", ");
            //where i need to somehow get the letters
            
    
        } catch (IOException e) {
            System.err.println("Error writing stats to file: " + e.getMessage());
        }
    }

    public void reset() {
        // Clear all the labels
        resetCount++;
        clearStatsFile("words.txt");
        saveStats();
        resetKeyboardColors();
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 5; col++) {
                updateLetterLabel(row, col, "", "#FFFFFF"); // Set text to empty and background color to white
            }
        }
        
        try {
            String ans = WordleWordGenerator.getRandomWord();
            System.out.println("The answer is " + ans);
            
            answer = new Word(ans);
        } catch (IOException e) {
            System.err.println("Error reading words from file: " + e.getMessage());
        }
        // Reset current row and column
        currRow = 0;
        currCol = 0;
        
    }

    private void showPopup(String message, int durationMillis) {
        Tooltip popup = new Tooltip(message);
        popup.setAutoHide(true); // Close the popup when user clicks outside
        
        // Show the popup in the current window
        popup.show(gridPane.getScene().getWindow()); 
        
        // Schedule the popup to hide after the specified duration
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(durationMillis), e -> popup.hide()));
        timeline.play();
    }

    private void showResultPopup(boolean isWin) {
        Popup popup = new Popup();
        VBox content = new VBox(10);
        content.getStyleClass().add("popup-content");
        String lose = "Sorry! You lost! The answer was " + answer.getActualWord();
        String resultMessage = isWin ? "Congratulations! You won!" : lose;
        Label label = new Label(resultMessage);
        label.setFont(Font.font(18)); // Increase font size if needed
        content.getChildren().add(label);
        
        // Add game statistics
        int timesPlayed = resetCount + 1;
        int timesWon = winCount;
        Label statsLabel = new Label("Times Played: " + timesPlayed + "\nTimes Won: " + timesWon);
        content.getChildren().add(statsLabel);


        // Create the bar chart
    CategoryAxis xAxis = new CategoryAxis();
    xAxis.setLabel("Guess Number");

    NumberAxis yAxis = new NumberAxis();
    yAxis.setLabel("Times Won");

    BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);

    XYChart.Series<String, Number> series = new XYChart.Series<>();
    series.setName("Wins");

    ArrayList<Integer> guesses = new ArrayList<>(Arrays.asList(guess1, guess2, guess3, guess4, guess5, guess6));

    // Add data points for guess1 to guess6
    for (int i = 1; i <= 6; i++) {
        series.getData().add(new XYChart.Data<>(String.valueOf(i), guesses.get(i - 1))); // Replace getGuessWins with your method to get wins for each guess
    }

    barChart.getData().add(series);

    content.getChildren().add(barChart);



        Button resetButton = new Button("Reset");
        resetButton.setOnAction(e -> {
            reset();
            popup.hide();
        });
        content.getChildren().add(resetButton);
    
        popup.getContent().add(content);
        popup.setAutoHide(true);
        popup.show(gridPane.getScene().getWindow());
    }

    private void updateLabelStyles(Word currWord) {
        for (int col = 0; col < 5; col++) {
            Letter letter = currWord.getword().get(col);
            Label label = letterLabels[currRow][col];
            String color = letter.getColor();
            label.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white;");
        }
    }

    

    private String getRowAsString(int row) {
        String rowAsString = "";
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getRowIndex(node) == row && node instanceof Label) {
                Label label = (Label) node;
                rowAsString += label.getText();
            }
        }
        return rowAsString;
    }

    public int getCurrRow(){
        return currRow;
    }
    
    private static int extractStat(String content, String label) {
        int startIndex = content.indexOf(label);
        if (startIndex != -1) {
            int endIndex = content.indexOf("\n", startIndex + label.length());
            if (endIndex != -1) {
                String statStr = content.substring(startIndex + label.length(), endIndex);
                
                return Integer.parseInt(statStr.trim());
            }
        }
        return 0; // Default value if not found
    }

    public void saveStats(){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("stats.txt", true))) {
        
            int timesPlayed = resetCount;

           String guesses = "\nGuess 1: " + guess1 + "\nGuess 2: " + guess2 + "\nGuess 3: " + guess3 + "\nGuess 4: " + guess4 + "\nGuess 5: " + guess5 + "\nGuess 6: " + guess6 ;

           
            
           
            

            clearStatsFile("stats.txt");
            String additionalStats = "\nTimes Played: " + timesPlayed + "\nTimes Won: " + winCount + "\n" + guesses;
            writer.write(additionalStats);
    
        } catch (IOException e) {
            System.err.println("Error writing stats to file: " + e.getMessage());
        }

    }
    public void save() {
        Path statsFilePath = Paths.get("words.txt");
        if (Files.exists(statsFilePath)) {
            // Get the current date and time
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String timestamp = now.format(formatter);
    
            // Construct the new file name
            String newFileName = "game_" + timestamp + ".txt";
            Path newStatsFilePath = Paths.get(newFileName);
            saveStats();
            // Read the existing content of the stats file
            try {
                String statsContent = Files.readString(statsFilePath);
                String currRowAsString = getRowAsString(currRow);
                if(currRowAsString.length() < 5){
                    statsContent += currRowAsString;
                }



                String keyStr = "\nKey: " + answer.getActualWord();
                statsContent += keyStr;
                // Write the updated content to the new file
                Files.writeString(newStatsFilePath, statsContent);
    
                System.out.println("Stats saved to: " + newFileName);
            } catch (IOException e) {
                System.err.println("Error reading or writing stats file: " + e.getMessage());
            }
        } else {
            System.out.println("Stats file does not exist.");
        }
    }

    public void updateLetterLabel(int row, int col, String value, String color) {
        if (row >= 0 && row < 6 && col >= 0 && col < 5) {
            letterLabels[row][col].setText(value);
            letterLabels[row][col].setStyle("-fx-background-color: " + color + ";");
        }

        
    }

    public boolean isEmpty(int row, int col) {
        if (row >= 0 && row < 6 && col >= 0 && col < 5) {
            return letterLabels[row][col].getText().isEmpty();
        } else {
            return false; // Return false for out-of-bounds indices
        }
    }


    


}
