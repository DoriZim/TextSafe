package bepo.textsafe.textsafe.views;

import bepo.textsafe.textsafe.RootNodeFetcher;
import bepo.textsafe.textsafe.controller.MainController;
import bepo.textsafe.textsafe.controller.PinController;
import bepo.textsafe.textsafe.util.Alerts;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.ResourceBundle;

public class MainView implements Initializable {
    private MainController mainController;
    private PinController pinController;
    private RootNodeFetcher rootNodeFetcher;
    @FXML private MenuBar menuBar;
    @FXML private TextArea textArea;
    @FXML private HBox bigHBox;
    @FXML private Button minimizeButton, closeButton;
    @FXML private TabPane tabPane;
    @FXML private Button addTabButton;
    private double X, Y;
    private int lastOpenedTab = 0;

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
        closeButton.setOnAction((event) -> onCloseButtonClick());
        minimizeButton.setOnAction((event) -> this.onMinimizeButtonClick(event));

        //Adjusting the MenuBar
        Menu file = menuBar.getMenus().get(0);
        file.getItems().get(0).setText("Clear Text");
        file.getItems().get(0).setOnAction(actionEvent -> textArea.clear());
        file.getItems().add(new MenuItem("Save Changes"));
        file.getItems().get(1).setOnAction(actionEvent -> onSaveClick());
        file.getItems().add(new MenuItem("Edit PIN"));
        file.getItems().get(2).setOnAction(actionEvent -> onPinChangeClick());

        Menu about = menuBar.getMenus().get(1);
        about.getItems().get(0).setOnAction(actionEvent -> onAboutClick());

        textArea.setOnKeyTyped(keyEvent -> keyTyped());

        tabPane.setTabDragPolicy(TabPane.TabDragPolicy.FIXED);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB);

        tabPane.setOnMouseClicked(mouseEvent -> onTabClick());
    }

    public void loadData() {
        for (String name : mainController.getAllNames()) {
            Tab newTab = new Tab(name);
            newTab.setOnCloseRequest(event -> onTabClose(event));
            tabPane.getTabs().add(newTab);
        }

        if(tabPane.getTabs().size() > 0) {
            tabPane.getSelectionModel().selectFirst();
            textArea.setText(mainController.getData(0));
        }
    }

    private void keyTyped() {
        mainController.changeData(textArea.getText(), tabPane.getSelectionModel().getSelectedIndex());
        System.out.println("Key typed");
    }

    private void onTabClick() {
        if(tabPane.getSelectionModel().getSelectedIndex() != lastOpenedTab) {
            textArea.setText(mainController.getData(tabPane.getSelectionModel().getSelectedIndex()));
            textArea.requestFocus();

            lastOpenedTab = tabPane.getSelectionModel().getSelectedIndex();
            System.out.println("Switched tab to " + lastOpenedTab);
        }
    }

    public void onAddTabButtonClick() {
        //todo - open addTabView where the user can enter a tab name
        String name = "Tab " + (tabPane.getTabs().size());
        Tab newTab = new Tab(name);

        newTab.setOnCloseRequest(event -> onTabClose(event)); //Each tab needs its own listener set
        mainController.addData(name);

        tabPane.getTabs().add(newTab);
        tabPane.getSelectionModel().selectLast();

        onTabClick();
    }

    private void onTabClose(Event event) {
        if(Alerts.confirmationAlert("Are you sure you want to delete this tab?", "All information saved within this tab will be deleted!")) {
            mainController.deleteData(tabPane.getSelectionModel().getSelectedIndex());

            System.out.println("Deleting tab...");
        } else {
            event.consume(); //consuming the event means not letting the tab close
        }
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
        int choice = Alerts.choiceAlert("Quit the program?", "Are you sure you want to quit? \n Make sure all your changes are saved.", "Save and Quit", "Quit");

        if(choice == 0) {
            return;
        } else if (choice == 1) {
            onSaveClick();
        }

        Platform.exit();
        System.exit(0);
    }

    private void onSaveClick() {
        if (mainController.saveData(textArea.getText(), tabPane.getSelectionModel().getSelectedIndex())) {
            Alerts.infoAlert("Save completed", "Your data has been saved successfully.");
        } else {
            Alerts.infoAlert("Couldn't save data", "Your data couldn't be saved. Please try again.");
        }
    }

    private void onPinChangeClick() {
        if(Alerts.confirmationAlert("Are you sure?", "You are about to set a new PIN. You cannot access the program without it.")) {
            pinController.setEdit(true);

            Parent root = new AnchorPane(rootNodeFetcher.get(PinView.class));
            Scene pinScene = new Scene(root, 320, 400);

            Stage pinStage = new Stage();
            pinStage.setScene(pinScene);
            pinStage.setTitle("Enter PIN");
            pinStage.initStyle(StageStyle.UNDECORATED);
            pinStage.initModality(Modality.APPLICATION_MODAL);
            pinStage.showAndWait();

            pinController.setEdit(false);
        }
    }

    private void onAboutClick() {
        Alerts.infoAlert("Information about this program:", "This program has been developed by DoriZim. \n https://github.com/DoriZim");
    }

    public void setMainController(MainController mainController) { this.mainController = mainController; }
    public void setPinController(PinController pinController) { this.pinController = pinController; }
    public void setRootNodeFetcher(RootNodeFetcher rootNodeFetcher) { this.rootNodeFetcher = rootNodeFetcher; }
}