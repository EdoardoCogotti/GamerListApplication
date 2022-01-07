package controller;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.cert.PolicyNode;

public class UtilityMenu {

    private Parent menu;
    private static UtilityMenu utilityMenu;

    private UtilityMenu() {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MenubarScene.fxml"));
            menu = loader.load();
        }
        catch (IOException e){e.printStackTrace();}
    }

    public static UtilityMenu getInstance(){
        if(utilityMenu==null)
            utilityMenu = new UtilityMenu();
        return utilityMenu;
    }

    public Parent addMenuBox(Parent root) {
        BorderPane newRoot = new BorderPane();
        newRoot.setCenter(root);
        newRoot.setTop(menu);
        return newRoot;
    }

    public void bind(Parent newRoot){
        double width = ((BorderPane)newRoot).getWidth();
        //System.out.println(width);
        ((AnchorPane) menu).setPrefWidth(width);
                //prefWidthProperty().bind(stage.widthProperty());
    }

}
