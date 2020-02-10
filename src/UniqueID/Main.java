package UniqueID;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

import static javafx.scene.layout.GridPane.setHalignment;

public class Main extends Application {

    private File file = new File(System.getProperty("user.dir"));
    private ReadWriteCSV readWriteCSV = new ReadWriteCSV();
    private ReadWriteExcluded readWriteExcluded = new ReadWriteExcluded();
    private FileChooser fc = new FileChooser();

    private Button startSingle = new Button("Run single");
    private Button startBtn = new Button("Run");
    private Button clear = new Button("Clear");
    private Button open = new Button("Open File");
    private Button exitBtn = new Button("Exit");

    private CheckBox skipFirstRow = new CheckBox("Skip first row");

    private TextField name = new TextField();
    private TextField country = new TextField();
    private TextField code = new TextField();
    private TextField singleOutput = new TextField();

    private TextField nameCol = new TextField();
    private TextField countryCol = new TextField();
    private TextField codeCol = new TextField();

    private HBox singleInput = new HBox(name, country, code);
    private HBox columns = new HBox(nameCol, countryCol, codeCol);
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
        countryCol.setPromptText("Country Col");
        nameCol.setPromptText("Name Col");
        codeCol.setPromptText("Code Col");
        company.setPromptText("Company prefix");
        fileLabel.setMaxWidth(400);

        clear.setOnAction(event -> {
            company.setText("");
            name.setText("");
            country.setText("");
            code.setText("");
            singleOutput.setText("");
            nameCol.setText("");
            countryCol.setText("");
            codeCol.setText("");
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
            if (parseInput(nameCol) < 0) {
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
                        parseInput(countryCol), parseInput(nameCol), parseInput(codeCol),
                        Arrays.asList(excluded.getText().toLowerCase().split("\n")));
                Alert done = new Alert(Alert.AlertType.INFORMATION,
                        "ID's has been added");
                done.setTitle("Done");
                done.showAndWait();
            }
        });
        open.setOnAction(event -> {
            fc.setInitialDirectory(file.getParentFile());
            File csvFile = fc.showOpenDialog(primaryStage);
            if (csvFile != null && csvFile.length() != 0) {
                file = csvFile;
                fileLabel.setText(file.getAbsolutePath());
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

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.YES) {
                readWriteExcluded.write(excluded.getText());
                Platform.exit();
            }
        });

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(0, 10, 0, 10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        gridPane.add(company, 1, 0);
        gridPane.add(singleOutput, 1, 1);

        gridPane.add(startSingle, 0, 2);
        gridPane.add(singleInput, 1, 2);

        gridPane.add(skipFirstRow, 0, 3);
        gridPane.add(columns, 1, 3);

        gridPane.add(excludedLabel, 0, 4);
        gridPane.add(excluded, 1, 4);

        gridPane.add(buttons, 0, 5);
        gridPane.add(open, 1, 5);
        setHalignment(open, HPos.RIGHT);
        gridPane.add(fileLabel, 1, 5);

        Scene scene = new Scene(gridPane);

        skipFirstRow.requestFocus();

        primaryStage.setScene(scene);
        primaryStage.setTitle("Unique ID creator");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}