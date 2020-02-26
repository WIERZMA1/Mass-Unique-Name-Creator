package com.uniqueid.creator;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Arrays;
import java.util.prefs.Preferences;

import static javafx.scene.layout.GridPane.setHalignment;

public class Main extends Application {

    private static final String USER_DIR = "UserDir";
    private Preferences pref = Preferences.userNodeForPackage(getClass());
    private File dir = new File(pref.get(USER_DIR, System.getProperty("user.dir")));
    private File file;
    private ReadWriteCSV readWriteCSV = new ReadWriteCSV();
    private ReadWriteExcluded readWriteExcluded = new ReadWriteExcluded();
    private FileChooser fc = new FileChooser();

    private Button startSingle = new Button("Run single");
    private Button startBtn = new Button("Run");
    private Button clear = new Button("Clear");
    private Button open = new Button("Open File");
    private Button exitBtn = new Button("Exit");
    private Button singleMass = new Button("Single");

    private CheckBox skipFirstRow = new CheckBox("Skip first row");

    private TextField name = new TextField();
    private TextField country = new TextField();
    private TextField code = new TextField();
    private TextField singleOutput = new TextField();

    private HBox input = new HBox(name, country, code);
    private HBox buttons = new HBox(startBtn, clear, exitBtn);

    private Label excludedLabel = new Label("Excluded values:");
    private Label fileLabel = new Label();
    private TextField company = new TextField();
    private TextArea excluded = new TextArea();

    private int parseInput(TextField input) {
        return input.getText().equals("") ? -1 : Integer.parseInt(input.getText());
    }

    @Override
    public void start(Stage primaryStage) {
        for (String exc : readWriteExcluded.read()) {
            excluded.appendText(exc + "\n");
        }
        name.setPromptText("Name");
        country.setPromptText("Country");
        code.setPromptText("Code");
        singleOutput.setPromptText("Output");
        singleOutput.setEditable(false);
        skipFirstRow.setSelected(true);
        skipFirstRow.setVisible(false);
        company.setPromptText("Company prefix");
        fileLabel.setMaxWidth(400);

        clear.setOnAction(event -> {
            company.setText("");
            name.setText("");
            country.setText("");
            code.setText("");
            singleOutput.setText("");
        });
        startSingle.setOnAction(event -> {
            if (name.getText().length() <= 0) {
                Alert error = new Alert(Alert.AlertType.ERROR,
                        "Company name has to be provided");
                error.setTitle("Missing name");
                error.showAndWait();
            } else {
                singleOutput.setText(readWriteCSV.runSingle(name.getText(), company.getText().toLowerCase(), country.getText(),
                        code.getText(), Arrays.asList(excluded.getText().toLowerCase().split("\n"))));
            }
        });
        startBtn.setOnAction(event -> {
            if (singleMass.getText().equals("Single")) {
                if (name.getText().length() <= 0) {
                    Alert error = new Alert(Alert.AlertType.ERROR,
                            "Company name has to be provided");
                    error.setTitle("Missing name");
                    error.showAndWait();
                } else {
                    singleOutput.setText(readWriteCSV.runSingle(name.getText(), company.getText().toLowerCase(), country.getText(),
                            code.getText(), Arrays.asList(excluded.getText().toLowerCase().split("\n"))));
                }
            } else {
                if (parseInput(name) < 0) {
                    Alert error = new Alert(Alert.AlertType.ERROR,
                            "Company name column has to be provided");
                    error.setTitle("Missing name");
                    error.showAndWait();
                } else if (fileLabel.getText().equals("")) {
                    Alert error = new Alert(Alert.AlertType.ERROR,
                            "Open CSV file");
                    error.setTitle("CSV file missing");
                    error.showAndWait();
                } else {
                    readWriteCSV.run(file, company.getText(), skipFirstRow.isSelected(),
                            parseInput(country), parseInput(name), parseInput(code),
                            Arrays.asList(excluded.getText().toLowerCase().split("\n")));
                    Alert done = new Alert(Alert.AlertType.INFORMATION,
                            "ID's has been added");
                    done.setTitle("Done");
                    done.showAndWait();
                }
            }
        });
        open.setOnAction(event -> {
            fc.setInitialDirectory(dir);
            File csvFile = fc.showOpenDialog(primaryStage);
            if (csvFile != null && csvFile.length() != 0) {
                file = csvFile;
                dir = file.getParentFile();
                fileLabel.setText(file.getAbsolutePath());
                pref.put(USER_DIR, dir.getAbsolutePath());
            } else {
                Alert error = new Alert(Alert.AlertType.ERROR,
                        "Open CSV file");
                error.setTitle("CSV file is empty");
                error.showAndWait();
            }
        });
        exitBtn.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to quit?", ButtonType.YES, ButtonType.NO);
            alert.setTitle("Exit");
            alert.setHeaderText("Exit");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    readWriteExcluded.write(excluded.getText());
                    Platform.exit();
                }
            });
        });
        singleMass.setOnAction(e -> {
            if (singleMass.getText().equals("Single")) {
                singleMass.setText("Mass");
                name.setPromptText("Name Column");
                country.setPromptText("Country Column");
                code.setPromptText("Code Column");
                skipFirstRow.setVisible(true);
                singleOutput.setVisible(false);
            } else {
                singleMass.setText("Single");
                name.setPromptText("Name");
                country.setPromptText("Country");
                code.setPromptText("Code");
                skipFirstRow.setVisible(false);
                singleOutput.setVisible(true);
            }
        });

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(0, 10, 0, 10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        gridPane.add(singleMass, 0, 0);
        gridPane.add(singleOutput, 1, 0);

        gridPane.add(skipFirstRow, 0, 1);
        gridPane.add(company, 1, 1);

        gridPane.add(input, 1, 2);

        gridPane.add(excludedLabel, 0, 3);
        gridPane.add(excluded, 1, 3);

        gridPane.add(buttons, 0, 4);
        gridPane.add(open, 1, 4);
        setHalignment(open, HPos.RIGHT);
        gridPane.add(fileLabel, 1, 4);

        Scene scene = new Scene(gridPane);

        skipFirstRow.requestFocus();

        primaryStage.setScene(scene);
        primaryStage.setTitle("Unique ID creator");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/keyboard.png")));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}