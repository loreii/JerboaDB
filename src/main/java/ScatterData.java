import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.axes.AxeBox;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.transform.space.SpaceTransformLog;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

import java.util.ArrayList;
import java.util.List;


public class ScatterData extends AbstractAnalysis{

    List<double[]> data;
    public ScatterData(List<double[]> data){
       this.data=data;
    }

    public void setAxeLabels(String x,String y,String z){
        chart.getAxeLayout().setXAxeLabel(x);
        chart.getAxeLayout().setYAxeLabel(y);
        chart.getAxeLayout().setZAxeLabel(z);
    }

    @Override
    public void init() throws Exception {
        List<Coord3d> points = new ArrayList<>(600_000);
        List<Color> colors = new ArrayList<>(600_000);
        double x=Double.MIN_VALUE,y=Double.MIN_VALUE,z=Double.MIN_VALUE;
        for(double[] d : data) {
            x=Math.max(x,d[0]);
            y=Math.max(y,d[1]);
            z=Math.max(z,d[2]);
        }
        for(double[] d:data){
            points.add(new Coord3d(d[0],d[1],d[2]));
            colors.add(new Color((float)(d[0]/x),(float)(d[1]/y),(float)(d[2]/z),0.5f));
        }
        Scatter scatter = new Scatter(points.toArray(new Coord3d[0]), colors.toArray(new Color[0]));

        chart = AWTChartComponentFactory.chart(Quality.Nicest, "newt");

        //addSurface(points);
        //setLog();
        chart.getScene().add(scatter);
    }

    private void setLog() {
        AxeBox axe = (AxeBox) chart.getView().getAxe();
        SpaceTransformer spaceTransformer = new SpaceTransformer(null, new SpaceTransformLog(), null);
        axe.setSpaceTransformer(spaceTransformer);
        chart.getView().setSpaceTransformer(spaceTransformer);
    }

    private void addSurface(List<Coord3d> points) {
        // Create a surface drawing that function
        Shape surface = Builder.buildDelaunay(points);
        surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBoundingBoxColor()));
        surface.setFaceDisplayed(true);
        surface.setWireframeDisplayed(false);
        surface.setWireframeColor(Color.BLACK);

        chart.add(surface);
    }
}