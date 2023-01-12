package bepo.textsafe.textsafe.views;

import bepo.textsafe.textsafe.controller.MainController;
import bepo.textsafe.textsafe.util.Alerts;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class MainView implements Initializable {
    private MainController mainController;
    @FXML private MenuBar menuBar;
    @FXML private TextArea textArea;
    @FXML private HBox bigHBox;
    @FXML private Button minimizeButton, closeButton;
    public double X, Y;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Creating the mouse event handler
        EventHandler<MouseEvent> eventHandler =
                e -> {
                    if(String.valueOf(e.getEventType()).equals("MOUSE_PRESSED")) {
                        mousePress(e.getScreenX(), e.getScreenY());
                    }
                    if(String.valueOf(e.getEventType()).equals("MOUSE_DRAGGED")) {
                        mouseDrag(e.getScreenX(), e.getScreenY());
                    }
                };

        //Adding the event handler to the hBoxes the titleBar consists of
        bigHBox.addEventHandler(MouseEvent.MOUSE_PRESSED, eventHandler);
        bigHBox.addEventHandler(MouseEvent.MOUSE_DRAGGED, eventHandler);

        //Adds eventHandler so that closeButton and minimizeButton clicks can be recognized
        closeButton.setOnAction((event) -> this.onCloseButtonClick());
        minimizeButton.setOnAction((event) -> this.onMinimizeButtonClick(event));

        //Adjusting the MenuBar
        Menu file = menuBar.getMenus().get(0);
        file.getItems().get(0).setText("Save");
        file.getItems().get(0).setOnAction(actionEvent -> onSaveClick());

        Menu about = menuBar.getMenus().get(1);
        about.getItems().get(0).setOnAction(actionEvent -> onAboutClick());
    }

    //Saves position where the mouse is pressed
    private void mousePress(double screenX, double screenY) {
        X = (bigHBox.getScene().getWindow().getX() - screenX);
        Y = (bigHBox.getScene().getWindow().getY() - screenY);
    }

    //Sets where window moves
    private void mouseDrag(double screenX, double screenY) {
        bigHBox.getScene().getWindow().setX(screenX + X);
        bigHBox.getScene().getWindow().setY(screenY + Y);
    }

    //Handles minimize-Button
    @FXML
    private void onMinimizeButtonClick(ActionEvent actionEvent) {
        ((Stage)((Button)actionEvent.getSource()).getScene().getWindow()).setIconified(true);
    }

    //Handles close-Button by closing the program
    @FXML
    private void onCloseButtonClick() {
        Platform.exit();
        System.exit(0);
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