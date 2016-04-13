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
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.AttributeBuilder;
import org.clueminer.dataset.api.DataType;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.InstanceBuilder;
import org.clueminer.exception.ParserError;
import org.clueminer.utils.DatasetLoader;

/**
 * JSON format parse. Currently the top element must be an Array which consists
 * of "data lines" (JSON objects).
 *
 * @author deric
 * @param <E>
 */
public class JsonLoader<E extends Instance> implements DatasetLoader<E> {

    @Override
    public boolean load(Reader reader, Dataset<E> output) throws FileNotFoundException, ParserError, ParseException {
        JsonElement json;
        try {
            json = new JsonParser().parse(reader);

            int i = 0;
            if (json.isJsonArray()) {
                JsonArray ja = json.getAsJsonArray();
                Iterator<JsonElement> iter = ja.iterator();

                while (iter.hasNext()) {
                    JsonElement elem = iter.next();
                    if (i == 0) {
                        createStructure(output, elem);
                    }
                    parse(i, output, elem);

                    i++;
                }
            } else {
                throw new UnsupportedOperationException("not supported yet");
            }
        } catch (JsonSyntaxException ex) {
            throw new ParserError(ex);
        }

        return true;
    }

    @Override
    public boolean load(File file, Dataset<E> output) throws FileNotFoundException, ParserError, ParseException {
        FileReader reader = new FileReader(file);
        return load(reader, output);
    }

    /**
     * Based on first element structure define dataset
     *
     * @param output
     * @param elem
     */
    private void createStructure(Dataset<E> output, JsonElement elem) throws ParserError {
        AttributeBuilder builder = output.attributeBuilder();
        if (elem.isJsonObject()) {
            JsonObject obj = elem.getAsJsonObject();
            Set<Entry<String, JsonElement>> set = obj.entrySet();
            for (Entry<String, JsonElement> entry : set) {
                JsonElement val = entry.getValue();
                if (val.isJsonPrimitive()) {
                    builder.create(entry.getKey(), convertType(val), "META");
                } else if (val.isJsonArray()) {
                    //the actual data
                    builder.create(entry.getKey(), "MD_DATA", "INPUT");
                    //TODO: only numeric data is expected here
                    JsonArray ary = val.getAsJsonArray();
                    JsonArray e = ary.getAsJsonArray();
                    JsonElement j = e.iterator().next();
                    if (j.isJsonArray()) {
                        JsonArray je = j.getAsJsonArray();
                        if (je.size() == 2) {
                            //required 2 attributes
                            output.setDataType(DataType.XY_CONTINUOUS);
                        }
                    }

                } else if (val.isJsonNull()) {
                    //TODO: skip null?
                    builder.create(entry.getKey(), "STRING", "META");
                } else {
                    throw new ParserError("unexpected type: " + entry.getKey());
                }
            }
        } else {
            throw new ParserError("unexpected element: " + elem);
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

    private void parse(int line, Dataset<E> output, JsonElement elem) throws ParserError, ParseException {
        E inst;
        InstanceBuilder<E> builder = output.builder();
        Attribute attr;
        if (elem.isJsonObject()) {
            JsonObject obj = elem.getAsJsonObject();
            Set<Entry<String, JsonElement>> set = obj.entrySet();
            inst = builder.create(output.attributeCount());
            inst.setIndex(line);
            for (Entry<String, JsonElement> entry : set) {
                attr = output.getAttribute(entry.getKey());
                JsonElement val = entry.getValue();
                //-> delegate parsing to lower level modules (storage)
                builder.set(val, attr, inst);
            }
        } else {
            throw new ParserError("unexpected element: " + elem);
        }
    }

}
