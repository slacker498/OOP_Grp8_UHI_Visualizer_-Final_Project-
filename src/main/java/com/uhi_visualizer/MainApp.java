/*
 * Name of project: UHI Visualizer
 * Names of authors: Jachin Hugh Dzidumor Kpogli, Curtis Asizem, Adama Baba, Olivia Gyanwah Panford
 * Date created: November 15, 2025
 * Purpose: To display statistical information for a UHI (Urban Heat Island)
 */

package com.uhi_visualizer;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import com.uhi_visualizer.exceptions.*;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import javafx.geometry.Pos;
import javafx.scene.control.*;


public class MainApp extends Application {
    private static Stage stage;
    private static VBox vbox = new VBox(); // Root node
    private static ToolBar toolBar; // ToolBar


    @Override
    public void start(Stage primaryStage) throws IOException {
        /*
        * Top Section of Interface 
        */
        // Error pane
        VBox errorPane = new VBox();
        Text fileSelected = new Text("File Name: No file selected");
        Text errorMessage = new Text("Error Message: No error");
        errorPane.getChildren().addAll(fileSelected, errorMessage);
        
        // Top bar
        Button clearButton = new Button("Clear");
        Button helpButton = new Button("Help");
        Button chooseFile = new Button("Load Data File");
        ImageView uploadImageView = new ImageView(new Image(getClass().getResourceAsStream("/images/upload.png")));

        uploadImageView.setFitWidth(20);
        uploadImageView.setFitHeight(20);
        uploadImageView.setPreserveRatio(true);
        chooseFile.setGraphic(uploadImageView);

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        toolBar = new ToolBar();
        toolBar.getItems().addAll(errorPane, spacer, chooseFile, clearButton, helpButton);


        /*
        * File Control
        */
        // File Upload
        //Creating a File chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select the data file");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("CSV File", "*.csv*"),
                new ExtensionFilter("JSON File", "*.json*"));
        
        //Adding action on the button
        chooseFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            //Opening a dialog box
            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile == null) return;
            fileSelected.setText("File Selected: " + selectedFile.getPath());

            FileParsable parser;
            if (selectedFile.getPath().endsWith(".json")) parser = new JSONFileParser();
            else parser = new CSVFileParser();
                // Fill scene with panes from data loading
                ScrollPane lPane;
                BarChart chart;
                try {
                    Island island = parser.readFromFile(selectedFile.getPath()); // Create Island
                    lPane = getStatsPanes(island.getName(),
                            StatisticsCalculator.getMeanTemperature(island),
                            StatisticsCalculator.getMedianTemperature(island),
                            StatisticsCalculator.getModeTemperature(island),
                            StatisticsCalculator.getSTDEVTemperature(island),
                            StatisticsCalculator.getIslandMaxTemperature(island),
                            StatisticsCalculator.getIslandMinTemperature(island),
                            StatisticsCalculator.getOptimalTempZones(island.getCityZones())
                            );

                    if (StatisticsCalculator.getAllDataPointsInIsland(island) <= 15) chart = getPlotBarGraph("CityZones-DataPoints", StatisticsCalculator.getMegaIslandTempDifferentialMap(island));
                    else chart = getPlotBarGraph("CityZones", StatisticsCalculator.getNanoIslandTempDifferentialMap(island));

                    showNothing("No error");
                    vbox.getChildren().clear();
                    vbox.getChildren().addAll(toolBar, new HBox(new SplitPane(lPane,
                                    new VBox(getFlaggedZones(StatisticsCalculator.getLowTempFlaggedZones(island.getCityZones()),
                                            StatisticsCalculator.getHighTempFlaggedZones(island.getCityZones())),
                                            chart))));

                } catch (FileNotCSVException e) {
                    showNothing("File not of type '.csv'");
                } catch (FileNotJSONException e) {
                    showNothing("File not of type '.json'");
                } catch (DuplicateDataPointException e) {
                    showNothing("A city zone has the same datapoint repeated");
                }catch (InvalidColumnException e) {
                    showNothing("Data File does not contain the required columns");
                    System.out.println(e.getMessage());
                } catch (InvalidValueTypeException e) {
                    showNothing("Temperature value is not a valid integer/ floating-point.");
                    System.out.println(e.getMessage());
                } catch (IOException e) {
                    showNothing("General File Reading error has occurred");
                }
            }});

        // Action on clearButton
        clearButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showNothing("No error");
            }}

        );

        // Action on helpButton
        helpButton.setOnAction(e -> {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/slacker498/OOP_Grp8_UHI_Visualizer_-Final_Project-.git"));
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }
        });



        /*
        *  Stage Settings
        */

        // Root Node for file upload scene
        ImageView nothingImageView = new ImageView(new Image(getClass().getResourceAsStream("/images/nodata.png")));
        Label noDataLabel = new Label("No data selected to visualize");
        nothingImageView.setX(170);
        nothingImageView.setY(10);
        nothingImageView.setFitWidth(270);
        nothingImageView.setPreserveRatio(true);

        VBox noData = new VBox(nothingImageView, noDataLabel);
        noData.setAlignment(Pos.CENTER);
        noData.setSpacing(20);
        vbox.getChildren().addAll(toolBar, noData);
        vbox.setAlignment(Pos.BASELINE_CENTER);

        //Creating a Scene by passing the group object, height and width
        Scene fileUploadScene = new Scene(vbox, 600, 300);
        

        //Setting the title to Stage. 
        primaryStage.setTitle("Urban Heat Island Visualizer"); 

        //Setting the logo to Stage.
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logo.png")));

        //Adding the scene to Stage 
        primaryStage.setScene(fileUploadScene); 

        //Displaying the contents of the stage
        primaryStage.show();
        
    }

    private static ScrollPane getStatsPanes(String name,
                                            double meanTemperature,
                                            double medianTemperature,
                                            double modeTemperature,
                                            double stDevTemperature,
                                            Object[] maxTempList,
                                            Object[] minTempList,
                                            HashMap<String, Double> optimalTempCityZones
                                            ) {
        /*
         * Top pane
         */
        Label islandName = new Label("Island Name: " + name);
        islandName.setFont(new Font("Arial", 20));
        Separator separator1 = new Separator(Orientation.HORIZONTAL);
        Label meanTemp = new Label(String.format("Mean temperature: %.2f°C", meanTemperature));
        Label medianTemp = new Label(String.format("Median temperature: %.2f°C", medianTemperature));
        Label modeTemp = new Label(String.format("Mode temperature: %.2f°C", modeTemperature));
        Label stDevTemp = new Label(String.format("Standard Deviation in temperature: %.2f°C", stDevTemperature));
        Label varianceInTemp = new Label(String.format("Variance in temperature: %.2f°C", Math.pow(stDevTemperature, 2)));

        // Table of Minimum and Max temp values, cityzones and Datapoints
        Separator separator2 = new Separator(Orientation.HORIZONTAL);
        TableView tableView = new TableView(); // Reference to edit later: https://jenkov.com/tutorials/javafx/tableview.html

        TableColumn<Map, String> headerColumn = new TableColumn<>("Header");
        headerColumn.setCellValueFactory(new MapValueFactory<>("header"));

        TableColumn<Map, String> minTempColumn = new TableColumn<>("Minimum Temperature");
        minTempColumn.setCellValueFactory(new MapValueFactory<>("min_value"));

        TableColumn<Map, String> maxTempColumn = new TableColumn<>("Maximum Temperature");
        maxTempColumn.setCellValueFactory(new MapValueFactory<>("max_value"));

        tableView.getColumns().addAll(headerColumn, minTempColumn, maxTempColumn);

        ObservableList<Map<String, Object>> items = FXCollections.<Map<String, Object>>observableArrayList();

        Map<String, Object> temps = new HashMap<>();
        temps.put("header", "Temp value");
        temps.put("min_value", minTempList[2] + "°C");
        temps.put("max_value", maxTempList[2] + "°C");
        items.add(temps);

        Map<String, Object> cityZones = new HashMap<>();
        cityZones.put("header" , "City Zone");
        cityZones.put("min_value" , minTempList[0]);
        cityZones.put("max_value" , maxTempList[0]);
        items.add(cityZones);

        Map<String, Object> dataPoints = new HashMap<>();
        dataPoints.put("header" , "Data Point");
        dataPoints.put("min_value" , minTempList[1]);
        dataPoints.put("max_value" , maxTempList[1]);
        items.add(dataPoints);

        tableView.getItems().addAll(items);
        
        VBox descriptiveStats = new VBox(islandName, separator1, meanTemp, medianTemp, modeTemp, stDevTemp, varianceInTemp, separator2, tableView);
        descriptiveStats.setMaxHeight(300); // liable to change


        /*
         * Bottom Pane 
         */
        Label infName = new Label("Inferential Statistics");
        infName.setFont(new Font("Arial", 20));
        Separator separator3 = new Separator(Orientation.HORIZONTAL);
        Separator separator4 = new Separator(Orientation.HORIZONTAL);
        Separator separator5 = new Separator(Orientation.HORIZONTAL);
        Label notesTitle = new Label("Things to Note: ");
        Text notes = new Text("\tHigh temperature threshold: 35°C\n" +
                                "\tLow temperature threshold: 20°C\n"
        );

        // Optimal Temp Flagged Zones Table
        Label opTableLabel = new Label("CityZones With Optimal Temperatures");
        opTableLabel.setFont(new Font("Arial", 12));

        TableView infTableOptimal = new TableView(); // Reference to edit later: https://jenkov.com/tutorials/javafx/tableview.html

        TableColumn<Map, String> opCZNamesColumn = new TableColumn<>("City Zone");
        opCZNamesColumn.setCellValueFactory(new MapValueFactory<>("name"));
        TableColumn<Map, String> opCZTempColumn = new TableColumn<>("Average Temperature");
        opCZTempColumn.setCellValueFactory(new MapValueFactory<>("temp"));
        infTableOptimal.getColumns().addAll(opCZNamesColumn, opCZTempColumn);

        ObservableList<Map<String, Object>> items3 = FXCollections.<Map<String, Object>>observableArrayList();
        for (String czName: optimalTempCityZones.keySet()){
            Map<String, Object> row = new HashMap<>();
            row.put("name" , czName);
            row.put("temp" , optimalTempCityZones.get(czName) + "°C");
            items3.add(row);
        }
        infTableOptimal.getItems().addAll(items3);


        VBox suggestiveStats = new VBox(infName,
                separator3,
                separator4,
                notesTitle,
                notes,
                new Separator(Orientation.HORIZONTAL),
                opTableLabel,
                infTableOptimal
                );
        VBox.setMargin(opTableLabel, new Insets(10,0,0,0));
        suggestiveStats.setMaxHeight(500);
        

        /*
         * Main Pane 
         */
        VBox main = new VBox(descriptiveStats, suggestiveStats);
        descriptiveStats.setPadding(new Insets(10, 0,0,0));
        suggestiveStats.setPadding(new Insets(10, 0,0,0));
        main.setPadding(new Insets(0, 10,0,10));
        main.setMinWidth(250);
        VBox.setVgrow(main, Priority.ALWAYS);
        ScrollPane scrollPane = new ScrollPane(main);
        scrollPane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);


        return scrollPane;
    }

    private static BarChart getPlotBarGraph(String xAxisLabel, HashMap<String, Double> tempDiffMap) {
        var xAxis = new CategoryAxis();
        xAxis.setLabel(xAxisLabel.strip() + " °C");

        var yAxis = new NumberAxis();
        yAxis.setLabel("Temperature Differential (°C)");

        var barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Plot of Temperature Differential Across Zones");

        var data = new XYChart.Series<String, Number>();

        for (String xName: tempDiffMap.keySet()) {
            data.getData().add(new XYChart.Data<>(xName, tempDiffMap.get(xName)));
        }

        barChart.getData().add(data);
        barChart.setLegendVisible(false);

        return barChart;

    }

    private static HBox getFlaggedZones(HashMap<String, Double> lowTempCityZones,
                                        HashMap<String, Double> highTempCityZones) {
        // High Temp Flagged Zones
        var xAxish = new CategoryAxis();
        xAxish.setLabel("CityZones");
        var yAxish = new NumberAxis();
        yAxish.setLabel("Average Temperature (°C)");

        var highFlagBarChart = new BarChart<>(xAxish, yAxish);
        highFlagBarChart.setTitle("Plot of CityZones with High Temperatures");

        var data = new XYChart.Series<String, Number>();

        for (String xName: highTempCityZones.keySet()) {
            data.getData().add(new XYChart.Data<>(xName, highTempCityZones.get(xName)));
        }
        highFlagBarChart.getData().add(data);
        highFlagBarChart.setLegendVisible(false);



        // Low Temp Flagged Zones
        var xAxisl = new CategoryAxis();
        xAxisl.setLabel("CityZones");
        var yAxisl = new NumberAxis();
        yAxisl.setLabel("Average Temperature (°C)");
        var lowFlagBarChart = new BarChart<>(xAxisl, yAxisl);
        lowFlagBarChart.setTitle("Plot of CityZones with Low Temperatures");

        var datal = new XYChart.Series<String, Number>();

        for (String xName: lowTempCityZones.keySet()) {
            datal.getData().add(new XYChart.Data<>(xName, lowTempCityZones.get(xName)));
        }
        lowFlagBarChart.getData().add(datal);
        lowFlagBarChart.setLegendVisible(false);

        HBox hbox = new HBox(highFlagBarChart, lowFlagBarChart);
        return hbox;
    }

    private static void showNothing(String errorMessage) {
        ImageView nothingImageView = new ImageView(new Image(MainApp.class.getResourceAsStream("/images/nodata.png")));
        Label noData = new Label("No data selected to visualize");
        nothingImageView.setX(170);
        nothingImageView.setY(10);
        nothingImageView.setFitWidth(270);
        nothingImageView.setPreserveRatio(true);

        vbox.getChildren().clear();
        VBox nothingBox = new VBox(nothingImageView, noData);
        nothingBox.setSpacing(20);
        nothingBox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(toolBar, nothingBox);
        ((Text) ((VBox) toolBar.getItems().get(0)).getChildren().get(1)).setText("Error Message: " + errorMessage);
        vbox.setAlignment(Pos.BASELINE_CENTER);
    }
    static void setRoot(String fxml) throws IOException {
        setRoot(fxml,stage.getTitle());
    }

    static void setRoot(String fxml, String title) throws IOException {
        Scene scene = new Scene(loadFXML(fxml));
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/fxml/"+fxml + ".fxml"));
        return fxmlLoader.load();
    }


    public static void main(String[] args) {
        launch(args);
    }

}
