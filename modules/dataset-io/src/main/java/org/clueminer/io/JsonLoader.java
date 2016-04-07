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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
    public boolean load(File file, Dataset<E> output) throws FileNotFoundException {
        JsonReader reader = new JsonReader(new FileReader(file));

        JsonElement json = new JsonParser().parse(reader);

        Gson gson = new Gson();
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
                parse(output, elem);

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
                System.out.println("entry: " + entry.getKey());
                JsonElement val = entry.getValue();
                if (val.isJsonPrimitive()) {
                    System.out.println(entry.getKey() + ": " + convertType(val));
                    builder.create(entry.getKey(), convertType(val), "META");
                } else if (val.isJsonArray()) {
                    System.out.println("data object " + val);
                } else if (val.isJsonNull()) {
                    //TODO: skip null?
                    System.out.println("null entry: " + entry.getKey());
                } else {
                    throw new RuntimeException("unexpected type: " + entry.getKey());
                }
            }
        } else {
            throw new RuntimeException("unexpected element: " + elem);
        }
    }

    private String convertType(JsonElement val) {
        Class<?> c = val.getClass();
        if (c.isPrimitive()) {
            switch (c.getTypeName()) {
                case "Double":
                    return "NUMERIC";
                default:
                    throw new RuntimeException("unknown type " + c.getTypeName());
            }
        }
        return "STRING";
    }

    private void parse(Dataset<E> output, JsonElement elem) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
