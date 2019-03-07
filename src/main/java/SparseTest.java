
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;

import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.markers.SeriesMarkers;

/**
 * Logarithmic Data
 *
 * <p>Demonstrates the following:
 *
 * <ul>
 * <li>Scatter chart
 * <li>Logarithmic X-Axis
 * <li>Place legend at Inside-NW position
 * <li>Formatting of number with large magnitude but small differences
 *
 * @author timmolter
 */
public class SparseTest {

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        System.setProperty("java.net.preferIPv6Addresses", "true");
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();


        fetch();

    }


    public static void fetch() {
        String connectionUrl =
                "jdbc:sqlserver://10.0.0.1:1433;"
                        + "DatabaseName=test;"
                        + "user=sa;"
                        + "password=pwd;"
                        + "encrypt=true;"
                        + "trustServerCertificate=true;"
                        + "loginTimeout=30;";

        ResultSet resultSet = null;

        List<Double> xData = new ArrayList<>(600_000);
        List<Double> yData = new ArrayList<>(600_000);
        long st = System.nanoTime();
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             Statement statement = connection.createStatement()) {

            // Create and execute a SELECT SQL statement.
//            String selectSql = "SELECT ID,AMOUNT,ITEMS_SOLD, REFUND_AMOUNT ,VOID_AMOUNT,EXTERNAL_END_UTC FROM BI_POS_TRANSACTION ";//WHERE VOID_AMOUNT IS NOT NULL
            String selectSql = "SELECT  PEOPLE_IN,PEOPLE_OUT FROM BI_ANALYTIC_PEOPLE_COUNT ";//WHERE VOID_AMOUNT IS NOT NULL

            resultSet = statement.executeQuery(selectSql);
            int count=0;
            // Print results from select statement
            while (resultSet.next()) {
          //      System.out.println(resultSet.getString(2) + " " + resultSet.getString(5));
                yData.add(resultSet.getDouble(1));
                xData.add(resultSet.getDouble(2));
                ++count;
            }

            System.out.println(">>"+count);
            System.out.println(">>"+(System.nanoTime()-st)+"ns");

            SparseTest exampleChart = new SparseTest();
            XYChart chart = exampleChart.getChart(xData,yData);
            new SwingWrapper<XYChart>(chart).displayChart();


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public XYChart getChart(List<Double> xData, List<Double> yData) {


        // Create Chart
        XYChart chart = new XYChartBuilder().width(800).height(600).build();

        // Customize Chart
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
        chart.getStyler().setChartTitleVisible(false);
        chart.getStyler().setLegendVisible(false);
      //  chart.getStyler().setXAxisLogarithmic(true);
        chart.getStyler().setMarkerSize(16);
        chart.getStyler().setYAxisGroupPosition(0, Styler.YAxisPosition.Right);

        XYSeries series = chart.addSeries("Gaussian Blob", xData, yData);
        series.setMarker(SeriesMarkers.CROSS);

        return chart;
    }
}