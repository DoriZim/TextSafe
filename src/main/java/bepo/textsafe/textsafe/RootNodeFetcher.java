package bepo.textsafe.textsafe;

import javafx.fxml.Initializable;
import javafx.scene.Node;

public interface RootNodeFetcher {
    Node get(Class<?extends Initializable> clazz);
}
