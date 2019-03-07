import javafx.application.Application;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.stage.Stage;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartLauncher;
import org.jzy3d.maths.Rectangle;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MainView extends Application {


    @FXML
    private TreeView<String> treeview;
    @FXML
    private TitledPane panedb;
    @FXML
    private TitledPane performance;

    @FXML
    private TextArea query;
    @FXML
    private LineChart performanceChart;

    private Database db;
    private String server = "10.0.0.1";
    private String username = "sa";
    private String password = "pwd";
    private String database = "test";

    //defining performance series
    XYChart.Series performanceSeriesNum = new XYChart.Series();
    XYChart.Series performanceSeriesTime = new XYChart.Series();
    int queryCount=0;


    @FXML
    public void setServer(Event e) {
        TextField tx = (TextField) e.getTarget();
        this.server = tx.getText();
    }

    @FXML
    public void setDatabase(Event e) {
        TextField tx = (TextField) e.getTarget();
        this.database = tx.getText();
    }


    @FXML
    public void setUsername(Event e) {
        TextField tx = (TextField) e.getTarget();
        this.username = tx.getText();
    }

    @FXML
    public void setPassword(Event e) {
        TextField tx = (TextField) e.getTarget();
        this.password = tx.getText();
    }

    Set<Column> selected = new HashSet<>();
    protected static Rectangle DEFAULT_WINDOW = new Rectangle(0,0,600,600);


    @FXML
    public void chartSelection(Event e) throws Exception {
        long st = System.nanoTime();
        List<double[]> data = DBUtils.fetch(db, selected, query.getText());
        double elapsed = (System.nanoTime() - st)/10E3;
        performanceSeriesTime.getData().add(new XYChart.Data(queryCount, elapsed));
        performanceSeriesNum.getData().add(new XYChart.Data(queryCount, data.size()));
        queryCount++;
        System.out.println("Elapsed >>\t"+ elapsed +"µs aka "+String.format("%.5g",elapsed/10E6)+"s");
        System.out.println("----------------------");

        ScatterData demo = new ScatterData(data);
        demo.init();
        Column[] mmm = selected.toArray(new Column[0]);
        demo.setAxeLabels(mmm[0].columnName,mmm[1].columnName,mmm[2].columnName);
        Chart chart = demo.getChart();
        ChartLauncher.openChart(chart,DEFAULT_WINDOW, selected.stream().map( c-> c.columnName).collect(Collectors.joining(" / ")));

    }

    @FXML
    public void login(Event e) {
        selected = new LinkedHashSet<>();
        db = new Database(server, database, username, password);
        db = DBUtils.load(db);
        db.setIndependent(true);
        System.out.println(db.toString());
        // TreeView<String> treeview = (TreeView<String>) stage.getScene().lookup("#treeview");
        treeview.setRoot(db);
        treeview.setCellFactory((item) -> {
            return new CheckBoxTreeCell<String>() {

                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item != null) {
                        //this.disableProperty().unbind();
                        CheckBoxTreeItem<String> value = (CheckBoxTreeItem<String>) treeItemProperty().getValue();
                        if (value instanceof Column) {
                            ((Column) value).addEventHandler(CheckBoxTreeItem.checkBoxSelectionChangedEvent(), event -> {
                                if (((Column) event.getSource()).isSelected()) {
                                    selected.add((Column) value);
                                } else {
                                    selected.remove(value);
                                }
                                query.setText(DBUtils.getQuery(selected));
                            });

                        } else {
                            this.disableProperty().unbind();
                        }
                    }
                }
            };
        });
        //Enable the working panels
        panedb.setDisable(false);
        panedb.setExpanded(true);
        performance.setDisable(false);
        performanceChart.getData().add(performanceSeriesNum);
        performanceChart.getData().add(performanceSeriesTime);
        performanceSeriesTime.setName("query elapsed (µs)");
        performanceSeriesNum.setName("query results");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("mainview.fxml"));
        primaryStage.setTitle("Jerboa DB viewer");
        Scene scene = new Scene(root, 600, 405);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String args[]) {
        launch(args);


    }
}