package KeskoName;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

    private Button startBtn = new Button("Run");
    private Button open = new Button("Open File");
    private Button exitBtn = new Button("Exit");

    private CheckBox skipFirstRow = new CheckBox("Skip first row");

    private TextField countryCol = new TextField();
    private TextField nameCol = new TextField();
    private TextField codeCol = new TextField();

    private HBox columns = new HBox(countryCol, nameCol, codeCol);

    private Label excludedLabel = new Label("Excluded values:");
    private Label fileLabel = new Label();
    private TextField company = new TextField();
    private TextArea excluded = new TextArea();

    @Override
    public void start(Stage primaryStage) throws Exception {
        for (String exc : readWriteExcluded.read()) {
            excluded.appendText(exc + "\n");
        }
        skipFirstRow.setSelected(true);
        countryCol.setPromptText("Country Col");
        nameCol.setPromptText("Name Col");
        codeCol.setPromptText("Code Col");
        company.setPromptText("Company prefix");
        fileLabel.setMaxWidth(400);

        startBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (company.getText().equals("")) {
                    Alert error = new Alert(Alert.AlertType.ERROR,
                            "Company prefix has to be provided");
                    error.setTitle("Missing prefix");
                    error.showAndWait();
                } else if (fileLabel.getText().equals("")) {
                    Alert error = new Alert(Alert.AlertType.ERROR,
                            "Open CSV file");
                    error.setTitle("CSV file missing");
                    error.showAndWait();
                } else {
                    readWriteCSV.run(file, company.getText(), skipFirstRow.isSelected(),
                            Integer.parseInt(countryCol.getText()), Integer.parseInt(nameCol.getText()), Integer.parseInt(codeCol.getText()),
                            Arrays.asList(excluded.getText().toLowerCase().split("\n")));
                    Alert done = new Alert(Alert.AlertType.INFORMATION,
                            "Tenants has been added");
                    done.setTitle("Done");
                    done.showAndWait();
                }
            }
        });
        open.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                fc.setInitialDirectory(file.getParentFile());
                File csvFile = fc.showOpenDialog(primaryStage);
                if (csvFile != null) {
                    file = csvFile;
                    fileLabel.setText(file.getAbsolutePath());
                }
            }
        });
        exitBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                        "Are you sure you want to quit?", ButtonType.YES, ButtonType.NO);
                alert.setTitle("Exit");
                alert.setHeaderText("Exit");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.YES) {
                    readWriteExcluded.write(excluded.getText());
                    Platform.exit();
                }
            }
        });

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(0, 10, 0, 10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        gridPane.add(skipFirstRow, 0, 0);
        gridPane.add(columns, 1, 0);

        gridPane.add(company, 1, 1);

        gridPane.add(excludedLabel, 0, 2);
        gridPane.add(excluded, 1, 2);

        gridPane.add(startBtn, 0, 3);
        gridPane.add(exitBtn, 0, 3);
        setHalignment(exitBtn, HPos.RIGHT);
        gridPane.add(open, 1, 3);
        setHalignment(open, HPos.RIGHT);
        gridPane.add(fileLabel, 1, 3);

        Scene scene = new Scene(gridPane);

        primaryStage.setScene(scene);
        primaryStage.setTitle("TenantID creator");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}