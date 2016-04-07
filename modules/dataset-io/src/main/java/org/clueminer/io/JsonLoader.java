/*
 * Copyright (C) 2011-2016 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.io;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import org.clueminer.dataset.api.AttributeBuilder;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.InstanceBuilder;
import org.clueminer.utils.DatasetLoader;

/**
 * JSON format parse. Currently the top element must be an Array which consists
 * of "data lines" (JSON objects).
 *
 * @author deric
 * @param <E>
 */
public class JsonLoader<E extends Instance> implements DatasetLoader<E> {

    private int numValues = 0;
    private int numMeta = 0;

    @Override
    public boolean load(File file, Dataset<E> output) throws FileNotFoundException {
        JsonReader reader = new JsonReader(new FileReader(file));

        JsonElement json = new JsonParser().parse(reader);

        InstanceBuilder builder = output.builder();
        int i = 0;
        /* Type collectionType = new TypeToken<Collection<Integer>>() {
         * }.getType();
         * gson.fromJson(json, collectionType); */
        if (json.isJsonArray()) {
            JsonArray ja = json.getAsJsonArray();
            Iterator<JsonElement> iter = ja.iterator();

            while (iter.hasNext()) {
                JsonElement elem = iter.next();
                if (i == 0) {
                    createStructure(output, elem);
                }
                parse(i, builder, elem);

                i++;
            }
        } else {
            throw new UnsupportedOperationException("not supported yet");
        }

        return true;
    }

    /**
     * Based on first element structure define dataset
     *
     * @param output
     * @param elem
     */
    private void createStructure(Dataset<E> output, JsonElement elem) {
        AttributeBuilder builder = output.attributeBuilder();
        if (elem.isJsonObject()) {
            JsonObject obj = elem.getAsJsonObject();
            Set<Entry<String, JsonElement>> set = obj.entrySet();
            for (Entry<String, JsonElement> entry : set) {
                JsonElement val = entry.getValue();
                if (val.isJsonPrimitive()) {
                    builder.create(entry.getKey(), convertType(val), "META");
                    numMeta++;
                } else if (val.isJsonArray()) {
                    //the actual data
                    JsonArray ary = val.getAsJsonArray();
                    numValues = ary.size();
                } else if (val.isJsonNull()) {
                    //TODO: skip null?
                    builder.create(entry.getKey(), "STRING", "META");
                    numMeta++;
                } else {
                    throw new RuntimeException("unexpected type: " + entry.getKey());
                }
            }
        } else {
            throw new RuntimeException("unexpected element: " + elem);
        }
    }

    private String convertType(JsonElement val) {
        if (val.isJsonPrimitive()) {
            JsonPrimitive jp = val.getAsJsonPrimitive();
            if (jp.isBoolean()) {
                return "BOOLEAN";
            } else if (jp.isNumber()) {
                return "NUMERIC";
            } else {
                return "STRING";
            }
        }
        return "STRING";
    }

    private void parse(int line, InstanceBuilder builder, JsonElement elem) {
        Instance inst;
        double[] values = new double[numValues];
        if (elem.isJsonObject()) {
            JsonObject obj = elem.getAsJsonObject();
            Set<Entry<String, JsonElement>> set = obj.entrySet();
            for (Entry<String, JsonElement> entry : set) {
                JsonElement val = entry.getValue();
                if (val.isJsonPrimitive()) {

                } else if (val.isJsonArray()) {
                    JsonArray ary = val.getAsJsonArray();
                    if (ary.size() != numValues) {
                        throw new RuntimeException("unexpected data row size " + ary.size() + ". expected length: " + numValues);
                    }
                    //array of arrays -> probably timeseries
                    if (ary.isJsonArray()) {
                        JsonArray e = ary.getAsJsonArray();
                        for (JsonElement j : e) {
                            if (j.isJsonArray()) {
                                JsonArray je = j.getAsJsonArray();
                                if (je.size() == 2) {
                                    System.out.println("data: " + je.toString());
                                } else {
                                    throw new RuntimeException("parsing error [" + line + "]: expected array with 2 elements, got " + e.size());
                                }
                            } else {
                                throw new RuntimeException("parsing error [" + line + "]: expected array got " + j);
                            }
                        }
                    } else {
                        //expect array of doubles
                        int i = 0;
                        for (JsonElement e : ary) {
                            values[i++] = e.getAsDouble();
                        }
                    }
                } else if (val.isJsonNull()) {

                } else {
                    throw new RuntimeException("unexpected type: " + entry.getKey());
                }
            }
        } else {
            throw new RuntimeException("unexpected element: " + elem);
        }
    }

}
