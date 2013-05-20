package org.clueminer.evolution;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.collect.Table;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Barton
 */
public class ResultsCollector implements EvolutionListener {
    
    //reference to results table
    private Table<String, String, Double> table;

    public ResultsCollector(Table<String, String, Double> table) {        
        this.table = table;
    }

    @Override
    public void bestInGeneration(int generationNum, Individual best, double avgFitness) {
        //we care only about final results
    }

    @Override
    public void finalResult(Evolution evolution, int g, Individual best, Pair<Long, Long> time, Pair<Double, Double> bestFitness, Pair<Double, Double> avgFitness) {
        table.put(evolution.getEvaluator().getName(), evolution.getDataset().getName(), best.getFitness());
    }

    public void writeToCsv(String filename) {
        PrintWriter writer = null;
        try {

            writer = new PrintWriter(filename, "UTF-8");
            CSVWriter csv = new CSVWriter(writer, ',');

            String[] header = new String[table.columnKeySet().size() + 1];
            int i = 1;
            header[0] = "measure";
            for (String column : table.columnKeySet()) {
                header[i++] = column;
            }
            csv.writeNext(header);
            for (String key : table.rowKeySet()) {
                String[] line = new String[table.columnKeySet().size() + 1];
                line[0] = key;
                i = 1;
                for (String column : table.columnKeySet()) {
                    line[i++] = String.valueOf(table.get(key, column));
                }
                csv.writeNext(line);
            }
            writer.close();

        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            writer.close();
        }
    }
}
