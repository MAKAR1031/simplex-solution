package ru.makar.simplexsolution.control;

import javafx.animation.Animation.Status;
import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import ru.makar.simplexsolution.elements.ConditionField;
import ru.makar.simplexsolution.util.SimplexSolution;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class WindowController implements Initializable {

    @FXML
    private AnchorPane rootPane;
    @FXML
    private TextField equationField;
    @FXML
    private Button solveButton;
    @FXML
    private TextArea answerField;
    @FXML
    private Button helpButton;

    private SimplexSolution simplexSolution;
    private ArrayList<ConditionField> conditionFields;
    private double currentY;
    private static final double GAP = 37;
    private Stage helpDialog;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createHelpDialog();
        adjustEquationField();
        adjustAnswerField();
        adjustSolveButton();
        adjustHelpButton();
        simplexSolution = new SimplexSolution();
        conditionFields = new ArrayList<>();
        currentY = 115;
        ConditionField field = newTextField(currentY);
        conditionFields.add(field);
        rootPane.getChildren().add(field);

    }

    private void createHelpDialog() {
        try {
            helpDialog = new Stage();
            helpDialog.setTitle("Помощь");
            AnchorPane pane = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/help-layout.fxml"));
            Scene scene = new Scene(pane);
            scene.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toExternalForm());
            helpDialog.setScene(scene);
            helpDialog.initModality(Modality.WINDOW_MODAL);
        } catch (Exception exception) {
            helpDialog = new Stage();
            helpDialog.setTitle("Ошикбка");
            HBox box = new HBox();
            box.setAlignment(Pos.CENTER);
            Label label = new Label("Не удалось построить окно справки...");
            box.setPrefSize(300, 80);
            label.setLayoutX(25);
            label.setLayoutY(20);
            box.getChildren().add(label);
            Scene scene = new Scene(box);
            helpDialog.setScene(scene);
            helpDialog.initModality(Modality.WINDOW_MODAL);
        }
    }

    private void adjustEquationField() {
        equationField.setContextMenu(new ContextMenu());
        equationField.setOnKeyPressed((e) -> {
            if (e.getCode() == KeyCode.ENTER) {
                conditionFields.get(0).requestFocus();
            }
            if (e.getCode() == KeyCode.V && e.isControlDown() && e.isShiftDown()) {
                try {
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    String parts[] = ((String) clipboard.getData(DataFlavor.stringFlavor)).split("\n");
                    if (conditionFields.size() < parts.length - 1) {
                        int foo = (parts.length - 1) - conditionFields.size();
                        for (int i = 0; i < foo; i++) {
                            ConditionField field = newTextField(currentY);
                            conditionFields.add(field);
                            rootPane.getChildren().add(field);
                        }
                    }
                    equationField.setText(parts[0]);
                    for (int i = 1; i < parts.length; i++) {
                        conditionFields.get(i - 1).setText(parts[i]);
                    }
                    equationField.positionCaret(parts[0].length());
                    e.consume();
                } catch (Exception ignored) { }
            }
        });
    }

    private void adjustAnswerField() {
        answerField.setContextMenu(new ContextMenu());
    }

    private void adjustSolveButton() {
        RotateTransition rotate1 = new RotateTransition(Duration.millis(50), solveButton);
        rotate1.setCycleCount(2);
        rotate1.setAutoReverse(true);
        rotate1.setFromAngle(0);
        rotate1.setToAngle(10);
        RotateTransition rotate2 = new RotateTransition(Duration.millis(50), solveButton);
        rotate2.setCycleCount(2);
        rotate2.setAutoReverse(true);
        rotate2.setFromAngle(0);
        rotate2.setToAngle(-10);
        SequentialTransition animation = new SequentialTransition(rotate1, rotate2);
        solveButton.setOnAction((e) -> {
            try {
                ArrayList<String> conditions = new ArrayList<>();
                for (ConditionField field : conditionFields) {
                    conditions.add(field.getText());
                }
                simplexSolution.newInstanse(equationField.getText(), conditions);
                answerField.setText(simplexSolution.getSolution());
            } catch (Exception exception) {
                answerField.setText(exception.getMessage());
                if (animation.getStatus() != Status.RUNNING) {
                    animation.play();
                }
                Toolkit.getDefaultToolkit().beep();
            }
        });
    }

    private void adjustHelpButton() {
        FadeTransition fade = new FadeTransition(Duration.millis(300), helpButton);
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.setAutoReverse(true);
        fade.setCycleCount(2);
        helpButton.setOnAction((e) -> {
            if (helpDialog.getOwner() == null) {
                helpDialog.initOwner(rootPane.getScene().getWindow());
            }
            fade.play();
            helpDialog.showAndWait();
        });
    }

    private ConditionField newTextField(double y) {
        ConditionField result = new ConditionField();
        result.setPrefSize(170, 15);
        result.setLayoutX(10);
        result.setLayoutY(y);
        result.setOnKeyPressed((e) -> {
            if (e.getCode() == KeyCode.ENTER) {
                int ind = conditionFields.indexOf(result);
                if (ind == conditionFields.size() - 1) {
                    ConditionField field = newTextField(currentY);
                    conditionFields.add(field);
                    rootPane.getChildren().add(field);
                    field.requestFocus();
                } else {
                    conditionFields.get(ind + 1).requestFocus();
                }
            }
            if (result.getCaretPosition() == 0 && e.getCode() == KeyCode.BACK_SPACE && result.getText().length() == 0) {
                int ind = conditionFields.indexOf(result);
                if (ind == conditionFields.size() - 1 && conditionFields.size() >= 2) {
                    conditionFields.get(ind).playExitAnimation();
                    conditionFields.get(ind - 1).requestFocus();
                    conditionFields.remove(ind);
                    currentY -= GAP;

                    Window window = rootPane.getScene().getWindow();
                    if (window.getHeight() > 300 + GAP) {
                        window.setHeight(window.getHeight() - GAP);
                    }
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        });
        currentY += GAP;
        result.playEntryAnimation();
        if (rootPane.getScene() != null && rootPane.getScene().getWindow().getHeight() < (currentY + 30)) {
            rootPane.getScene().getWindow().setHeight(currentY + 45);
        }
        return result;
    }
}