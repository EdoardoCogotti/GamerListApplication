package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Game;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GamerListController implements Initializable {

    @FXML
    private TableView<Game> tbGameData;
    @FXML
    public TableColumn<Game, String> gameName;
    @FXML
    public TableColumn<Game, String> developerName;
    @FXML
    public TableColumn<Game, String> publisherName;
    @FXML
    public TableColumn<Game, String> storeName;
    @FXML
    public TableColumn<Game, String> actionCol;

    private ObservableList<Game> gameList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //TO_DO find gamelist from db

        for(int i=0; i<50; i++){
            Game g = new Game();
            g.setName("Peggle 3");
            g.setDeveloper("Bandai");
            g.setPublisher("Bandai");
            g.setStore("Steam");
            gameList.add(g);
        }

        gameName.setCellValueFactory(new PropertyValueFactory<>("Name"));
        developerName.setCellValueFactory(new PropertyValueFactory<>("Developer"));
        publisherName.setCellValueFactory(new PropertyValueFactory<>("Publisher"));
        storeName.setCellValueFactory(new PropertyValueFactory<>("Store"));

        tbGameData.setItems(gameList);

        gameName.setCellFactory(tc -> {
            TableCell<Game, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty) ;
                    setText(empty ? null : item);
                }
            };
            cell.setOnMouseClicked(e -> {
                if(e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {
                    String gamename = cell.getItem();
                    try {
                        switchToGameInfo(e, gamename);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            return cell ;
        });

        Callback<TableColumn<Game, String>, TableCell<Game, String>> cellFactory
                =
                new Callback<>() {
                    @Override
                    public TableCell call(final TableColumn<Game, String> param) {
                        final TableCell<Game, String> cell = new TableCell<>() {

                            Button btn = new Button("REMOVE");
                            boolean added=true;

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    btn.setOnAction(event -> {
                                        Game g = getTableView().getItems().get(getIndex());
                                        if(added){
                                            System.out.println("REMOVED");
                                            added=false;
                                            btn.setText("ADD");
                                            // TO_DO remove in db
                                        }
                                        else{
                                            System.out.println("ADDED");
                                            added=true;
                                            btn.setText("REMOVE");
                                            // TO_DO add in gamelist
                                        }
                                    });
                                    setGraphic(btn);
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                };
        actionCol.setCellFactory(cellFactory);
    }

    private void switchToGameInfo(MouseEvent e, String gamename) throws IOException {
        Stage stage = (Stage) (((Node) e.getSource()).getScene().getWindow());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/GameInfoScene.fxml"));
        Parent root = loader.load();
        Parent newRoot = UtilityMenu.getInstance().addMenuBox(root);

        Scene scene = new Scene(newRoot);
        String css = this.getClass().getResource("/css/gameInfoScene.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }
}
