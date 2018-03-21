/*
 * Copyright (C) 2011-2018 clueminer.org
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
package org.clueminer.bio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.InstanceBuilder;
import org.clueminer.exception.ParserError;
import org.clueminer.utils.DatasetLoader;

/**
 * FASTA format parser
 *
 * @see https://en.wikipedia.org/wiki/FASTA_format
 * @author deric
 */
public class FastaParser<E extends Instance> implements DatasetLoader<E> {

    public FastaParser() {

    }

    public BufferedReader open(File file) throws FileNotFoundException {
        return new BufferedReader(new FileReader(file));
    }

    @Override
    public boolean load(File file, Dataset<E> output) throws ParserError, ParseException, IOException {
        BufferedReader br = open(file);
        return load(br, output);
    }

    public boolean load(BufferedReader br, Dataset<E> output) throws ParserError, ParseException, IOException {
        StringBuilder sequence = new StringBuilder();
        int i = -1;
        String protein;
        String line;

        while ((line = br.readLine()) != null) {
            if (line.startsWith(">")) {
                String tokens[] = line.split(" ");
                i++;
                protein = tokens[0].substring(1);
                if (sequence.length() > 0) {
                    encodeProtein(i, protein, sequence, output.builder());
                    //reset sequence
                    sequence.setLength(0);
                }
            } else {
                sequence.append(line);
            }
        }

        return i > -1;
    }

    @Override
    public boolean load(Reader reader, Dataset<E> output) throws ParserError, ParseException, IOException {
        BufferedReader br = new BufferedReader(reader);
        return load(br, output);
    }

    private E encodeProtein(int i, String protein, StringBuilder sequence, InstanceBuilder<E> builder) {
        E inst = builder.create(sequence.length());
        inst.setId(protein);
        inst.setIndex(i);
        inst.setName(protein);
        for (int j = 0; j < sequence.length(); j++) {
            inst.setObject(j, sequence.charAt(j));
        }
        int pos = protein.indexOf("_");
        if (pos > -1) {
            inst.setClassValue(protein.substring(0, pos));
        }

        return inst;
    }

}
