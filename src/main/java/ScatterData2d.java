import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.plot2d.primitives.ScatterSerie2d;
import org.jzy3d.plot3d.rendering.canvas.Quality;

import java.util.ArrayList;
import java.util.List;


public class ScatterData2d extends AbstractAnalysis{

    List<double[]> data;
    public ScatterData2d(List<double[]> data){
       this.data=data;
    }

    @Override
    public void init() throws Exception {
        List<Coord2d> points = new ArrayList<>(600_000);
        //List<Color> colors = new ArrayList<>(600_000);
        for(double[] d:data){
            points.add(new Coord2d(d[0],d[1]));
            //colors.add(new Color((float) d[0]/400,(float)d[1]/400,0,0.25f));
        }
        ScatterSerie2d scatter = new ScatterSerie2d("View");
        scatter.add(points);
        chart = AWTChartComponentFactory.chart(Quality.Advanced, "newt");
        //chart.getScene().add(scatter);
    }
}