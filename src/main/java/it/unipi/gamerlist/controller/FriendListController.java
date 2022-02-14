package it.unipi.gamerlist.controller;

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
import it.unipi.gamerlist.model.User;
import it.unipi.gamerlist.utils.Session;
import it.unipi.gamerlist.utils.UtilityMenu;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FriendListController implements Initializable {

    @FXML
    private TableView<User> tbFollowerData, tbFollowingData;
    @FXML
    public TableColumn<User, String> userName, actionCol;
    @FXML
    public TableColumn<User, String> userNameFollower;

    private ObservableList<User> userList = FXCollections.observableArrayList();

    private ObservableList<User> followingList = FXCollections.observableArrayList();
    private ObservableList<User> followerList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //find follower and following users from db
        for(String s : User.getFollowedList(Session.getInstance().getLoggedUser().getUsername())){
            User u = User.getUserByName(s);
            followerList.add(u);
        }
        for(String s : User.getFollowingList(Session.getInstance().getLoggedUser().getUsername())){
            User u = User.getUserByName(s);
            followingList.add(u);
        }

        userNameFollower.setCellValueFactory(new PropertyValueFactory<>("Username"));

        userName.setCellValueFactory(new PropertyValueFactory<>("Username"));
        actionCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

        Callback<TableColumn<User, String>, TableCell<User, String>> cellFactory
                =
                new Callback<>() {
                    @Override
                    public TableCell call(final TableColumn<User, String> param) {
                        final TableCell<User, String> cell = new TableCell<>() {

                            Button btn = new Button("UNFOLLOW");
                            boolean followed=true;

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    btn.setOnAction(event -> {
                                        User u = getTableView().getItems().get(getIndex());
                                        if(followed){
                                            System.out.println("UNFOLLOWED");
                                            followed=false;
                                            btn.setText("FOLLOWED");
                                            User logged = Session.getInstance().getLoggedUser();
                                            // delete follower relationship
                                            User.unfollowUser(logged.getUsername(),u.getUsername());
                                        }
                                        else{
                                            System.out.println("FOLLOWED");
                                            followed=true;
                                            btn.setText("UNFOLLOW");
                                            User logged = Session.getInstance().getLoggedUser();
                                            // create follower relationship
                                            User.addFollow(logged.getUsername(),u.getUsername());

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

        userName.setCellFactory(tc -> {
            TableCell<User, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty) ;
                    setText(empty ? null : item);
                }
            };
            cell.setOnMouseClicked(e -> {
                if(e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {
                    String username = cell.getItem();
                    try {
                        switchToUser(e, username);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            return cell ;
        });

        userNameFollower.setCellFactory(tc -> {
            TableCell<User, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty) ;
                    setText(empty ? null : item);
                }
            };
            cell.setOnMouseClicked(e -> {
                if(e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {
                    String username = cell.getItem();
                    try {
                        switchToUser(e, username);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            return cell ;
        });

        tbFollowerData.setItems(followerList);
        tbFollowingData.setItems(followingList);
    }

    private void switchToUser(MouseEvent e, String username) throws IOException {
        Stage stage = (Stage) (((Node) e.getSource()).getScene().getWindow());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserProfileScene.fxml"));
        Parent root = loader.load();
        Parent newRoot = UtilityMenu.getInstance().addMenuBox(root);
        UserProfileController userProfileController = loader.getController();
        userProfileController.displayInfo(username, false);

        Scene scene = new Scene(newRoot);
        String css = this.getClass().getResource("/css/userProfileScene.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }
}
