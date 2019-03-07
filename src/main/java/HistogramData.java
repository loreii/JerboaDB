
import org.jzy3d.chart.Chart;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.HistogramBar;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;

import java.util.List;
import java.util.Optional;

public class HistogramData {

    private final Chart chart;
    private Optional<Float> min,max;

    List<double[]> data;

    public HistogramData(List<double[]> data){
        this.data=data;

        //max = data.stream().reduce(Float::max);
        //min = data.stream().reduce(Float::min);
        chart = new Chart(Quality.Advanced);
        Scene scene = chart.getScene();
        for(int i=0;i<data.size();++i)
        scene.add( addBar(data.get(i)[0],data.get(i)[1],data.get(i)[2]) );
    }

    public AbstractDrawable addBar(double x, double y, double height){
        Color color = Color.random();
        color.a = 0.55f;

        HistogramBar bar = new HistogramBar();
        bar.setData(new Coord3d(x, y, 0), (float)height, 10, color);
        bar.setWireframeDisplayed(false);
        bar.setWireframeColor(Color.BLACK);
        return bar;
    }

    public Chart getChart() {
        return chart;
    }
}
