package utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class UtilityMenu {

    private Parent menu;
    private static UtilityMenu utilityMenu;
    private static boolean usable;

    private UtilityMenu() {
        try{
            String menuBarPath;
            if(Session.getInstance().getLoggedUser().getAdmin())
                menuBarPath="/AdminMenubarScene.fxml";
            else
                menuBarPath="/MenubarScene.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(menuBarPath));
            menu = loader.load();
        }
        catch (IOException e){e.printStackTrace();}
    }

    public static UtilityMenu getInstance(){
        if(utilityMenu==null || !usable) {
            utilityMenu = new UtilityMenu();
            usable = true;
        }
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
        ((AnchorPane) menu).setPrefWidth(width);
    }

    public static void logout(){
        usable=false;
    }

}
