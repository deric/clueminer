package com.panayotis.gnuplot.plot;

/**
 *
 * @author Tomas Barton
 */
public class ExternalDataSetPlot extends AbstractPlot{ 
    private String filename;
    
    public ExternalDataSetPlot(String file){
        this.filename = file;
        setDefinition("\""+file+"\"");
    }

    @Override
    public void retrieveData(StringBuffer buffer) {
        buffer.append("\n");
    }
    
    public void setFilename(String filename){
        this.filename = filename;
    }
    
    public String getFilename(){
        return this.filename;
    }

}
