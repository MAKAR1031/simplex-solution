package ru.makar.simplexsolution.control;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;

public class HelpController implements Initializable {

    @FXML
    private TextArea helpArea;
    @FXML
    private TextArea authorArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        helpArea.setText(readFile("txt/help.txt"));
        authorArea.setText(readFile("txt/aboutAuthor.txt"));
    }

    private String readFile(String url) {
        try {
            InputStream stream = getClass().getClassLoader().getResourceAsStream(url);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder result = new StringBuilder();
            String tmp;
            while ((tmp = reader.readLine()) != null) {
                result.append(tmp).append("\n\r");
            }
            reader.close();
            return result.toString();
        } catch (IOException e) {
            return "Ошибка при чтении файла";
        }
    }
}
