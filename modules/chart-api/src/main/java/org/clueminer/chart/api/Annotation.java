package org.clueminer.chart.api;

/**
 *
 * @author Tomas Barton
 */
public interface Annotation {

    public String getName();

    public boolean isActive();

    public void setActive(boolean b);

    public boolean isSelected();

    public void setSelected(boolean b);

    public void setChartConfig(ChartConfig frame);

}
