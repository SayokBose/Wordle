import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.Node;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;



public class App extends Application {

    private GameBoard gameBoard;
    private HBox keyboardRow1;
    private HBox keyboardRow2;
    private HBox keyboardRow3;

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10));

        
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(50)); // Adjust the padding to make the GridPane larger
        gridPane.setHgap(10);
        gridPane.setVgap(10);

         // Initialize the game board

        

        keyboardRow1 = createKeyboardRow(new String[]{"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"});
        keyboardRow2 = createKeyboardRow(new String[]{"A", "S", "D", "F", "G", "H", "J", "K", "L", "\u2190"});
        keyboardRow3 = createKeyboardRow(new String[]{"Z", "X", "C", "V", "B", "N", "M", "\u23CE"});
        
        gameBoard = new GameBoard(gridPane, keyboardRow1, keyboardRow2, keyboardRow3);
        
        HBox buttonsRow = new HBox(10);
        buttonsRow.setAlignment(Pos.CENTER);
        buttonsRow.setPadding(new Insets(20));

        Button resetButton = new Button("Reset");
        resetButton.setOnAction(e -> {
            handleResetButton();
            resetKeyboardColors();
        });
        buttonsRow.getChildren().add(resetButton);


        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> handleSaveButton());
        buttonsRow.getChildren().add(saveButton);

        Scene scene = new Scene(root, 700, 900);

        Button uploadButton = new Button("Upload");
        uploadButton.setOnAction(e -> handleUploadButton(primaryStage, keyboardRow1, keyboardRow2, keyboardRow3));
        buttonsRow.getChildren().add(uploadButton);

        root.getChildren().addAll(gridPane, keyboardRow1, keyboardRow2, keyboardRow3, buttonsRow);


        // Adjust the width and height of the scene
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleKeyboardButton("\u23CE", keyboardRow1, keyboardRow2, keyboardRow3);
                event.consume();
            } else if (event.getCode() == KeyCode.BACK_SPACE) {
                
                handleKeyboardButton("\u2190", keyboardRow1, keyboardRow2, keyboardRow3);
                event.consume(); // Consume the event to prevent further processing
            } 
            else {
                String typedCharacter = event.getText();
                if (!typedCharacter.isEmpty()) {
                    handleKeyboardButton(typedCharacter.toUpperCase(), keyboardRow1, keyboardRow2, keyboardRow3);
                     // Convert to uppercase if needed
                }
            }
        });

        primaryStage.setScene(scene);
        primaryStage.setTitle("Wordle Game Frontend");
        primaryStage.show();
    }

    private void handleUploadButton(Stage primaryStage, HBox... keyboardRows) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload Text File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null) {
            // Handle the selected file
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            // Here you can add code to read and process the selected file
            extractGameDataFromFile(selectedFile.getAbsolutePath(), keyboardRows);

        }
    }
    private HBox createKeyboardRow(String[] letters) {
        HBox keyboardRow = new HBox(10);
        keyboardRow.setAlignment(Pos.CENTER);
        keyboardRow.setPadding(new Insets(20));

        for (String letter : letters) {
            Button button = new Button(letter);
            button.getStyleClass().add("keyboard-button");
            button.setOnAction(e -> handleKeyboardButton(letter)); // Handle button click
            keyboardRow.getChildren().add(button);
        }

        return keyboardRow;
    }

    private void handleKeyboardButton(String letter, HBox... keyboardRows) {
        
        if (isValidLetter(letter)) {
            gameBoard.addLetter(letter);
        }
        else if(letter == "\u23CE"){
            Map<Character, String> letterColors;
            letterColors = gameBoard.check();
            if(letterColors != null){
                for (Character key : letterColors.keySet()) {
                    // Find the corresponding button in each keyboard row
                    for (HBox keyboardRow : keyboardRows) {
                        Button button = findButtonByKey(key, keyboardRow);
                        if (button != null) {
                            // Update the button style based on the color from letterColors
                            String color = letterColors.get(key);
                            button.setStyle("-fx-background-color: " + color + ";");
                        }
                    }
                }
            }
        }
        else if(letter == "\u2190"){
            gameBoard.removeLetter();
        }
    }
    
    private Button findButtonByKey(Character key, HBox keyboardRow) {
        String buttonText = key.toString();
        for (Node child : keyboardRow.getChildren()) {
            if (child instanceof Button) {
                Button button = (Button) child;
                if (button.getText().equals(buttonText)) {
                    return button;
                }
            }
        }
        return null;
    }

    private boolean isValidLetter(String letter) {
        // Check if the input is a single letter (a-z or A-Z)
        return letter.length() == 1 && Character.isLetter(letter.charAt(0));
    }
    
    private void handleResetButton() {
        gameBoard.reset();
    }
    
    public void resetKeyboardColors() {
        for (Node node : keyboardRow1.getChildren()) {
            if (node instanceof Button) {
                Button button = (Button) node;
                button.setStyle("-fx-background-color: #D3D3D3;"); // Reset background color to default
            }
        }
    
        for (Node node : keyboardRow2.getChildren()) {
            if (node instanceof Button) {
                Button button = (Button) node;
                button.setStyle("-fx-background-color: #D3D3D3;"); // Reset background color to default
            }
        }
    
        for (Node node : keyboardRow3.getChildren()) {
            if (node instanceof Button) {
                Button button = (Button) node;
                button.setStyle("-fx-background-color: #D3D3D3;"); // Reset background color to default
            }
        }
    }
    
    private void handleSaveButton() {
        gameBoard.save();
    }
    
    private static int extractStat(String content, String label) {
        int startIndex = content.indexOf(label);
        if (startIndex != -1) {
            System.out.println("a");
            int endIndex = content.indexOf("\n", startIndex + label.length());
            if (endIndex != -1) {
                System.out.println("b");
                String statStr = content.substring(startIndex + label.length(), endIndex);
                System.out.println(statStr);
                return Integer.parseInt(statStr.trim());
            }
        }
        return 0; // Default value if not found
    }

    private static String extractKey(String content) {
        if (content.length() >= 5) {
            return content.substring(content.length() - 5);
        }
        return ""; // Default value if not found
    }

    private void extractGameDataFromFile(String filePath, HBox... keyboardRows) {
        try {
            String content = Files.readString(Paths.get(filePath));
            String firstLine = content.split("\n")[0]; 
            String[] wordsArray = firstLine.split(",\\s*");
            
            System.out.println("");
            
            

            
            String statsContent = Files.readString(Paths.get("stats.txt"));

            int timesPlayed = extractStat(statsContent, "Times Played:");
            int timesWon = extractStat(statsContent, "Times Won:");
            

            int guess1_ = extractStat(statsContent, "Guess 1:");
            int guess2_ = extractStat(statsContent, "Guess 2:");
            int guess3_ = extractStat(statsContent, "Guess 3:");
            int guess4_ = extractStat(statsContent, "Guess 4:");
            int guess5_ = extractStat(statsContent, "Guess 5:");
            int guess6_ = extractStat(statsContent, "Guess 6:");

            gameBoard.setGuess1(guess1_);
            gameBoard.setGuess2(guess2_);
            gameBoard.setGuess3(guess3_);
            gameBoard.setGuess4(guess4_);
            gameBoard.setGuess5(guess5_);
            gameBoard.setGuess6(guess6_);
            // Find and extract "Key"
            String key = extractKey(content);


            // Print extracted data (you can modify this to use the extracted data as needed)
            // System.out.println("Word: " + word);
            // System.out.println("Colors: " + colorsMap);
            System.out.println("Times Played: " + timesPlayed);
            System.out.println("Times Won: " + timesWon);
            System.out.println("Key: " + key);

            gameBoard.reset();
            gameBoard.setAnswer(key);
            gameBoard.setWinCount(timesWon);
            gameBoard.setWinReset(timesPlayed);

            for (String word : wordsArray) {
                gameBoard.addWord(word);
                if(word.length() == 5){
                    handleKeyboardButton("\u23CE", keyboardRows);
                }
                

                
                
            }


            //fromat the output to have the words inputted as a list

            //for each word, use gameboard.addword()

            //after each call to addword, act as if the enter button has been pressed.

            // Update UI or perform other actions with the extracted data
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
