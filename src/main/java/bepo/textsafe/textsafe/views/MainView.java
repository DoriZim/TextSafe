package bepo.textsafe.textsafe.views;

import bepo.textsafe.textsafe.controller.MainController;
import bepo.textsafe.textsafe.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import java.net.URL;
import java.util.ResourceBundle;

public class MainView implements Initializable {
    private MainController mainController;
    @FXML private MenuBar menuBar;
    @FXML private TextArea textArea;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        menuBar.getMenus().remove(1);

        Menu file = menuBar.getMenus().get(0);
        file.getItems().get(0).setText("Save");
        file.getItems().get(0).setOnAction(actionEvent -> onSaveClick());

        Menu about = menuBar.getMenus().get(1);
        about.getItems().get(0).setOnAction(actionEvent -> onAboutClick());
    }

    private void onSaveClick() {
        if (mainController.saveData(textArea.getText())) {
            Alerts.infoAlert("Save completed", "Your data has been saved successfully.");
        } else {
            Alerts.infoAlert("Couldn't save data", "Your data couldn't be saved. Please try again.");
        }
    }

    private void onAboutClick() {
        Alerts.infoAlert("Information about this program:", "This program has been developed by DoriZim.");
    }

    public void loadData() throws Exception {
        textArea.setText(mainController.getData());
    }

    public void setMainController(MainController mainController) { this.mainController = mainController; }
}