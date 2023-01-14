package bepo.textsafe.textsafe;

import bepo.textsafe.textsafe.controller.MainController;
import bepo.textsafe.textsafe.controller.PinController;
import bepo.textsafe.textsafe.views.MainView;
import bepo.textsafe.textsafe.views.PinView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.File;
import java.util.HashMap;

public class Main extends Application {
    private HashMap<Class<?extends Initializable>, Node> rootNodes = new HashMap<>();
    private PinController pinController = new PinController();
    private MainController mainController = new MainController();
    private PinView pinView = new PinView();
    private MainView mainView;

    @Override
    public void start(Stage stage) throws Exception {
        //Creates all necessary files
        (new File("application-files")).mkdirs();
        (new File("application-files/Data.ser")).createNewFile();
        (new File("application-files/Info.ser")).createNewFile();
        (new File("application-files/Auth.ser")).createNewFile();

        File tempFile = new File("application-files/Temp.ser");
        tempFile.createNewFile();
        tempFile.deleteOnExit();

        //Sets rootNodeFetcher
        RootNodeFetcher rootNodeFetcher = (clazz) -> this.rootNodes.get(clazz);

        //Sets Main View
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/bepo/textsafe/textsafe/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 450);
        this.mainView = fxmlLoader.getController();

        fxmlLoader.setControllerFactory((type) -> this.mainView);
        this.rootNodes.put(MainView.class, null);

        //Sets pinView
        FXMLLoader fxmlLoaderPin = new FXMLLoader(Main.class.getResource("/bepo/textsafe/textsafe/pin-view.fxml"));
        fxmlLoaderPin.setControllerFactory((type) -> this.pinView);
        this.rootNodes.put(PinView.class, fxmlLoaderPin.load());

        //Sets Controllers in Views
        this.pinView.setPinController(this.pinController);
        this.mainView.setMainController(this.mainController);
        this.mainView.setPinController(this.pinController);
        this.mainView.setRootNodeFetcher(rootNodeFetcher);

        stage.setScene(scene);
        stage.setTitle("TextSafe");
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();

        pinView.onControllersSet();

        //Setting pinStage
        Stage pinStage = new Stage();
        Scene pinScene = new Scene((Parent) rootNodeFetcher.get(PinView.class), 320, 400);
        pinStage.setScene(pinScene);
        pinStage.setTitle("Enter PIN");
        pinStage.setResizable(false);
        pinStage.initStyle(StageStyle.UNDECORATED);
        pinStage.initModality(Modality.APPLICATION_MODAL);
        pinStage.setOnCloseRequest(windowEvent -> {
            Platform.exit();
            System.exit(1);
        });

        pinStage.showAndWait();

        //Once all controllers are set and the program is unlocked all data is loaded
        if (this.pinController.getLoginState()) {
            System.out.println("Login succeeded");
            mainController.loadContent();
            mainView.loadData();
            }
        }

    public static void main(String[] args) {
        launch();
    }
}