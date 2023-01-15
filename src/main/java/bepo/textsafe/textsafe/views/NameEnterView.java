package bepo.textsafe.textsafe.views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class NameEnterView implements Initializable {
    @FXML private TextField textField;
    @FXML private Label label;
    private String name = "";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    public String getName() {
        return name;
    }

    public void onConfirmButtonClick(ActionEvent actionEvent) {
        if(!textField.getText().isEmpty()) {
            name = textField.getText();
            textField.clear();

            Stage thisStage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            thisStage.close();
        } else {
            label.setText("Name can not be blank!");
        }
    }

    public void onCancelButtonClick(ActionEvent actionEvent) {
        name = "";
        textField.clear();
        label.setText("Choose a name for the tab");

        Stage thisStage = (Stage)((Button) actionEvent.getSource()).getScene().getWindow();
        thisStage.close();
    }
}
