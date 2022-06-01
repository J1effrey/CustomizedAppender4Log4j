package uw.edu.css553.report;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.chart.*;
import javafx.scene.Group;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class LogAnalysisApplication extends Application {

    private static final int ALL = 0;
    private static final int DEBUG = 1;
    private static final int INFO = 2;
    private static final int WARN = 3;
    private static final int ERROR = 4;
    private static final int FATAL = 5;
    private static final int OFF = 6;
    private static final int TRACE = 7;
    public static HashMap<String, Integer> LevelCount = new HashMap<String, Integer>();
    private static BufferedReader reader;
    public static int TotalCount = 0;


    public static void InitializeHashMap()
    {
        LevelCount.put("ALL", 0);
        LevelCount.put("DEBUG", 0);
        LevelCount.put("INFO", 0);
        LevelCount.put("WARN", 0);
        LevelCount.put("ERROR", 0);
        LevelCount.put("FATAL", 0);
        LevelCount.put("OFF", 0);
        LevelCount.put("TRACE", 0);
    }

    public static int LineContains(String line)
    {
        if(line.matches(".*ALL.*"))
            return ALL;
        else if(line.matches(".*DEBUG.*"))
            return DEBUG;
        else if(line.matches(".*INFO.*"))
            return INFO;
        else if(line.matches(".*WARN.*"))
            return WARN;
        else if(line.matches(".*ERROR.*"))
            return ERROR;
        else if(line.matches(".*FATAL.*"))
            return FATAL;
        else if(line.matches(".*OFF.*"))
            return OFF;
        return TRACE;
    }

    public static void Switch(String line)
    {
        switch(LineContains(line))
        {
            case 0: LevelCount.put("ALL", LevelCount.get("ALL")+1);
                break;
            case 1: LevelCount.put("DEBUG", LevelCount.get("DEBUG")+1);
                break;
            case 2: LevelCount.put("INFO", LevelCount.get("INFO")+1);
                break;
            case 3: LevelCount.put("WARN", LevelCount.get("WARN")+1);
                break;
            case 4: LevelCount.put("ERROR", LevelCount.get("ERROR")+1);
                break;
            case 5: LevelCount.put("FATAL", LevelCount.get("FATAL")+1);
                break;
            case 6: LevelCount.put("OFF", LevelCount.get("OFF")+1);
                break;
            case 7: LevelCount.put("TRACE", LevelCount.get("TRACE")+1);
                break;
            default: break;
        }
    }

    public static void ReadFile()
    {
        try {
            reader = new BufferedReader(new FileReader("../datamerge-logs/app.log"));
//            reader = new BufferedReader(new FileReader("src/main/resources/sample logs.txt"));
            String line = reader.readLine();
            while (line != null) {
                Switch(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override public void start(Stage stage) {
        Scene scene = new Scene(new Group());
        stage.setTitle("Statistical Logger");
        stage.setWidth(500);
        stage.setHeight(500);
        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                        new PieChart.Data("ALL", LevelCount.get("ALL")),
                        new PieChart.Data("DEBUG", LevelCount.get("DEBUG")),
                        new PieChart.Data("INFO", LevelCount.get("INFO")),
                        new PieChart.Data("WARN", LevelCount.get("WARN")),
                        new PieChart.Data("ERROR", LevelCount.get("ERROR")),
                        new PieChart.Data("FATAL", LevelCount.get("FATAL")),
                        new PieChart.Data("OFF", LevelCount.get("OFF")),
                        new PieChart.Data("TRACE", LevelCount.get("TRACE")));
        final PieChart chart = new PieChart(pieChartData);
        chart.setLegendSide(Side.BOTTOM);
        chart.setTitle("Log Level Statistics");
        final Label caption = new Label("");
        caption.setTextFill(Color.DARKORANGE);
        caption.setStyle("-fx-font: 24 arial;");
        for (final PieChart.Data data : chart.getData()) {
            data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
                caption.setTranslateX(e.getSceneX()-caption.getLayoutX());
                caption.setTranslateY(e.getSceneY()-caption.getLayoutY());
                caption.setText(((data.getPieValue()/TotalCount) * 100)  + "%");
                System.out.println(((data.getPieValue()/TotalCount) * 100) + "%");
            });
        }
        ((Group) scene.getRoot()).getChildren().add(chart);
        stage.setScene(scene);
        stage.show();
    }

    public static void PrintAndCompute()
    {
        for (String key: LevelCount.keySet()){
            System.out.println(key +" = "+LevelCount.get(key) + " occurrences.");
            TotalCount+=LevelCount.get(key);
        }
    }

    public static void main(String[] args) {
        InitializeHashMap();
        ReadFile();
        PrintAndCompute();
        launch(args);
    }
}