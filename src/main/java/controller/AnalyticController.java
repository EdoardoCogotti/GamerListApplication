package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import model.Game;
import model.Review;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AnalyticController implements Initializable {

    @FXML
    private Label userRankValue;
    @FXML
    private BarChart barChart;

    private int percentile;
    private List<Game> topKGames;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //TO_DO get Percentile of the average score of each user’s last year reviews
        String username = Session.getInstance().getLoggedUser().getUsername();

        percentile = Review.getRankingPosition(username);
        userRankValue.setText(percentile+"%");
        if(percentile<30)
            userRankValue.setStyle("-fx-text-fill: red");
        else if(percentile<70)
            userRankValue.setStyle("-fx-text-fill: white");
        else
            userRankValue.setStyle("-fx-text-fill: gold");

        //TO_DO get Top k games with positive reviews for a given genre in the last year
        // get in form <genre, gamename, numPosReview>
        List<String> genres = new ArrayList<>();
        genres.add("Action");
        genres.add("Puzzle");
        genres.add("Platform");
        genres.add("FPS");
        for(int i=0; i<10; i++)
            genres.add("DUMMY"+i);

        List<String> games = new ArrayList<>();
        games.add("Peggle");
        games.add("Pokemon");
        games.add("Layton");

        for(String game: games){
            XYChart.Series<?,?> series = new XYChart.Series<>();
            series.setName(game);
            for(String genre: genres){
                int r = (int)(Math.random()*1000);
                XYChart.Data data = new XYChart.Data(genre,r);
                series.getData().add(data);
            }
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
