package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import model.Game;
import model.Review;
import org.bson.Document;

import javax.print.Doc;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class AnalyticController implements Initializable {

    @FXML
    private Label userRankValue;
    @FXML
    private BarChart barChart;
    @FXML
    private ListView<String> genreListView;

    private String[] genres = {"Action", "Adventure", "Animation & Modeling", "Arcade", "Audio Production", "Building", "Casual", "Combat", "Comedy", "Design & Illustration", "Detective-mystery", "Early Access", "Economic", "Education", "Educational", "Espionage", "FPP", "Fantasy", "Fighting", "Free to Play", "Game Development", "Hidden Object", "Historical", "Horror", "Indie",  "Managerial", "Massively Multiplayer", "Metroidvania", "Modern", "Mystery", "Narrative", "Naval", "Off-road", "Open World", "Photo Editing", "Pinball", "Platformer", "Point-and-click",  "Puzzle", "RPG", "Racing", "Rally", "Real-time", "Roguelike", "Role-playing", "Sandbox", "Sci-fi",  "Shooter", "Simulation", "Soccer", "Software Training", "Sports", "Stealth", "Strategy", "Survival", "TPP", "Tactical", "Turn-based", "Utilities", "Video Production", "Virtual life", "Visual Novel", "Web Publishing"}; // "", "Chess", "Exploration", "Gore", "JRPG", "Programming", "Sexual Content", "Team sport", "Touring", "Violent"
    private ObservableList genre = FXCollections.observableArrayList();

    private int percentile;
    private ArrayList<Document> topKGames;
    private String currentGenre;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Session session = Session.getInstance();
        String username = session.getLoggedUser().getUsername();

        if(session.getPercentile()!=-1)
            percentile=session.getPercentile();
        else {
            percentile = Review.getRankingPosition(username);
            session.setPercentile(percentile);
        }
        userRankValue.setText(percentile+"%");
        if(percentile<30)
            userRankValue.setStyle("-fx-text-fill: red");
        else if(percentile<70)
            userRankValue.setStyle("-fx-text-fill: white");
        else
            userRankValue.setStyle("-fx-text-fill: gold");

        for(String g : genres)
            genre.add(g);

        genreListView.setItems(genre);

        genreListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                currentGenre = genreListView.getSelectionModel().getSelectedItem();
            }
        });

        genreListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2){
                    try {
                        topKGames = Game.getTopKByGenre(3, currentGenre);
                        updateMyChart(topKGames, currentGenre);
                    }
                    catch(Exception e){e.printStackTrace();}
                }
            }
        });

    }

    private void updateMyChart(ArrayList<Document> gamesDocument, String genre) throws InterruptedException {

        barChart.getData().clear();
        barChart.layout();

        TimeUnit.SECONDS.sleep(1);
        for(Document d: gamesDocument){
            XYChart.Series<?,?> series = new XYChart.Series<>();

            String game = d.get("_id", Document.class).getString("name");
            series.setName(game);

            int r = d.getInteger("totalPositiveReview");
            XYChart.Data data = new XYChart.Data(genre,r);
            series.getData().add(data);

            barChart.getData().add(series);
        }
        for (Object o : barChart.getData()) {
            for (final XYChart.Data<?, ?> data : ((XYChart.Series<?, ?>)o).getData()) {
                Tooltip tooltip = new Tooltip();
                tooltip.setText(((XYChart.Series<?, ?>) o).getName() +" "+
                        data.getYValue().toString());
                Tooltip.install(data.getNode(), tooltip);
            }
        }

    }
}
