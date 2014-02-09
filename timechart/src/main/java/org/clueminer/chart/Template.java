package org.clueminer.chart;

import java.util.ArrayList;
import org.clueminer.chart.api.Chart;
import org.clueminer.chart.api.ChartProperties;
import org.clueminer.chart.api.Overlay;
import org.clueminer.chart.base.ChartPropertiesImpl;

/**
 *
 * @author Tomas Barton
 */
public class Template {

    private final String name;
    private Chart chart;
    private ChartProperties chartProperties;
    private ArrayList<Overlay> overlays;

    public Template(String name) {
        this.name = name;
        this.chartProperties = new ChartPropertiesImpl();
        this.overlays = new ArrayList<Overlay>();
    }

    public String getName() {
        return name;
    }

    public void setChart(Chart chart) {
        if (chart == null) {
            return;
        }
        this.chart = chart;
    }

    public Chart getChart() {
        return chart;
    }

    public void setChartProperties(ChartProperties chartProperties) {
        if (chartProperties == null) {
            return;
        }
        this.chartProperties = chartProperties;
    }

    public ChartProperties getChartProperties() {
        return chartProperties;
    }

    public void addOverlay(Overlay overlay) {
        if (overlay == null) {
            return;
        }
        overlays.add(overlay);
    }

    public ArrayList<Overlay> getOverlays() {
        return overlays;
    }
}
