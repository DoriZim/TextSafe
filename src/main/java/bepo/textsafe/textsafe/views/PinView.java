package bepo.textsafe.textsafe.views;

import bepo.textsafe.textsafe.controller.PinController;
import bepo.textsafe.textsafe.util.Alerts;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Handles the PinView. User input is processed to either enter the correct PIN to unlock the program
 * or to set a new PIN that will be used to do just that in the future (in setup mode)
 */
public class PinView implements Initializable {
    private PinController pinController;
    @FXML private HBox bigHBox;
    @FXML private Button minimizeButton, closeButton;
    @FXML private TextField pinTextField;
    @FXML private Label infoLabel;
    private String pin = "";
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
    }

    /*
     * When all controllers are set it is checked if a user is logged in or not. This determines the instruction text in the view.
     * Then the eventHandlers for registering text input are set
    */
    public void onControllersSet() {
        //Adds event handler for handling key presses in pinTextField
        pinTextField.addEventFilter(KeyEvent.KEY_TYPED, eventHandler -> {
            try {
                onPinCharacterTyped(eventHandler.getCharacter());
            } catch (Exception e) {
                System.err.println(e);
            }
            eventHandler.consume(); //Event handler gets consumed because user sets the text themselves
        });
        pinTextField.addEventFilter(KeyEvent.KEY_PRESSED, eventHandler -> {
            if (eventHandler.getCode().equals(KeyCode.DELETE) || eventHandler.getCode().equals(KeyCode.BACK_SPACE)) {
                try {
                    onPinCharacterTyped("Delete");
                } catch (Exception e) {
                    System.err.println(e);
                }
            }
            eventHandler.consume(); //Event handler gets consumed because user sets the text themselves
        });
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

    /*
     * This method is called when the PIN is entered through buttons rather than keyboard input.
     * It passes the value over to onPinCharacterTyped() where the input is processed
     */
    @FXML public void onPinEnter(ActionEvent actionEvent) throws Exception {
        String buttonText = ((Button) actionEvent.getTarget()).getText(); //Gets the pressed buttons value
        onPinCharacterTyped(buttonText); //Calls method for processing the input
    }

    /*
     * Gets called when the event handler registers a new typed character in the pinTextField.
     * Handles input validation and input masking.
     */
    private void onPinCharacterTyped(String character) throws Exception {
        if(character.equals("Delete")) {
            if (pin.length() <= 1) {
                pin = "";
                pinTextField.clear();
            } else {
                pin = pin.substring(0, pin.length() - 1);
                pinTextField.setText(maskInput(pin));
            }
            pinTextField.positionCaret(pin.length()); //Sets caret to the right
            return; //stops method
        }

        //Only allows numbers
        if (!character.matches("^\\d+$")) {
            return; //stops method
        }

        pin += character;

        pinTextField.setText(maskInput(pin));
        pinTextField.positionCaret(pin.length()); //Sets caret to the right

        //Stops the method if PIN has not been fully entered yet
        if (pin.length() != 4) {
            return;
        }

        //If Setup is not completed it sets a new PIN
        if (!pinController.getLoginState()) {
            if(!pinController.confirmPIN(pin)) {
                pinTextField.clear();
                pin = "";
                Alerts.infoAlert("PIN not confirmed", "Please set and confirm a PIN to continue.");
                return;
            }
        } else { //Checks whether the PIN is correct or not, if setup has already been completed...
            if (!pinController.checkPIN(pin)) {
                pinTextField.clear();

                if(pinController.getEdit()) {
                    pinController.confirmPIN(pin);

                } else {
                    pin = "";
                    Alerts.infoAlert("Wrong PIN", "You've entered a wrong PIN. If you continue entering wrong PINs you'll be logged out of the program.");
                    return;
                }
            }
        }

        pin = "";
        pinTextField.clear();

        Stage stage = (Stage) pinTextField.getScene().getWindow();
        stage.close();
    }

    //Masks recent inputs for PIN entering
    private String maskInput(String input) {
        String masked = "";

        for (int i = 0; i < input.length() - 1; i++) {
            masked += "*";
        }

        masked += input.charAt(input.length() - 1);

        return masked;
    }

    public void setPinController(PinController pinController) { this.pinController = pinController; }
}
