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
package org.clueminer.importer.impl;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import org.clueminer.graph.api.EdgeType;
import org.clueminer.importer.Issue;
import org.clueminer.io.importer.api.Container;
import org.clueminer.io.importer.api.EdgeDraft;
import org.clueminer.io.importer.api.InstanceDraft;
import org.clueminer.io.importer.api.NodeDraft;
import org.clueminer.io.importer.api.Report;
import org.clueminer.longtask.spi.LongTask;
import org.clueminer.spi.FileImporter;
import org.clueminer.types.FileType;
import org.clueminer.utils.progress.ProgressTicket;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Graph Modeling Language (GML) is a hierarchical ASCII-based file format for
 * describing graphs. It has been also named Graph Meta Language.
 *
 * @author deric
 */
public class GmlImporter<E extends InstanceDraft> extends AbstractLineImporter<E> implements FileImporter<E>, LongTask {

    private static String NAME = "GML";
    private static Logger LOG = LoggerFactory.getLogger(GmlImporter.class);
    private Reader reader;
    private GraphDraft gcont;
    private Report report;
    private ProgressTicket progressTicket;
    private boolean cancel = false;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean execute(Container<E> container, LineNumberReader reader) throws IOException {
        this.container = container;
        this.gcont = (GraphDraft) container;
        this.report = new Report();
        LineNumberReader lineReader = ImportUtils.getTextReader(reader);
        try {
            importData(lineReader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                lineReader.close();
            } catch (IOException ex) {
            }
        }
        return !cancel;
    }

    @Override
    public boolean isAccepting(Collection mimeTypes) {
        String mime = mimeTypes.toString();
        //this will match pretty much anything
        return mime.contains("text") || mime.contains("octet-stream");
    }

    @Override
    public FileType[] getFileTypes() {
        FileType ft = new FileType(".csv", NbBundle.getMessage(getClass(), "fileType_GML_Name"));
        return new FileType[]{ft};
    }

    @Override
    public boolean isMatchingImporter(FileObject fileObject) {
        String ext = fileObject.getExt();
        return ext.equalsIgnoreCase("gml");
    }

    private void importData(LineNumberReader reader) throws Exception {
        ArrayList<Object> list;
        list = parseList(reader);

        boolean ret = false;
        for (int i = 0; i < list.size(); i++) {
            if ("graph".equals(list.get(i)) && list.size() >= i + 2 && list.get(i + 1) instanceof ArrayList) {
                ret = parseGraph((ArrayList) list.get(i + 1));
            }
        }
        if (!ret) {
            LOG.warn("failed to parse GML");
        }
    }

    private ArrayList<Object> parseList(LineNumberReader reader) throws IOException {

        ArrayList<Object> list = new ArrayList<>();
        char t;
        boolean readString = false;
        String stringBuffer = new String();

        while (reader.ready()) {
            t = (char) reader.read();
            if (readString) {
                if (t == '"') {
                    list.add(stringBuffer);
                    stringBuffer = new String();
                    readString = false;
                } else {
                    stringBuffer += t;
                }
            } else {
                switch (t) {
                    case '[':
                        list.add(parseList(reader));
                        break;
                    case ']':
                        return list;
                    case '"':
                        readString = true;
                        break;
                    case ' ':
                    case '\t':
                    case '\n':
                        if (!stringBuffer.isEmpty()) {
                            //First try to parse as long, if not possible, try double.
                            try {
                                Long longValue = Long.valueOf(stringBuffer);
                                list.add(longValue);
                            } catch (NumberFormatException e1) {
                                try {
                                    Double doubleValue = Double.valueOf(stringBuffer);
                                    list.add(doubleValue);
                                } catch (NumberFormatException e2) {
                                    list.add(stringBuffer);
                                }
                            }
                            stringBuffer = new String();
                        }
                        break;
                    default:
                        stringBuffer += t;
                        break;
                }
            }
        }
        return list;
    }

    private boolean parseGraph(ArrayList list) {
        if ((list.size() & 1) != 0) {
            return false;
        }

        boolean ret = true;
        for (int i = 0; i < list.size(); i += 2) {
            Object key = list.get(i);
            Object value = list.get(i + 1);
            if ("node".equals(key)) {
                ret = parseNode((ArrayList) value);
            } else if ("edge".equals(key)) {
                ret = parseEdge((ArrayList) value);
            } else if ("directed".equals(key)) {
                if (value instanceof Number) {
                    EdgeType edgeDefault = ((Number) value).intValue() == 1 ? EdgeType.FORWARD : EdgeType.NONE;
                    //container.setEdgeDefault(edgeDefault);
                } else {
                    report.logIssue(new Issue("error parsing graph", Issue.Level.WARNING));
                }
            } else {
            }
            if (!ret) {
                break;
            }
        }
        return ret;
    }

    private boolean parseNode(ArrayList list) {
        String id = null;
        String label = null;
        for (int i = 0; i < list.size(); i += 2) {
            String key = (String) list.get(i);
            Object value = list.get(i + 1);
            if ("id".equalsIgnoreCase(key)) {
                id = value.toString();
            } else if ("label".equalsIgnoreCase(key)) {
                label = value.toString();
            }
        }
        NodeDraft node;
        if (id != null) {
            node = gcont.newNodeDraft(id);
        } else {
            node = gcont.newNodeDraft();
        }
        if (label != null) {
            node.setLabel(label);
        }
        if (id == null) {
            report.logIssue(new Issue("node id missing", Issue.Level.WARNING));
        }
        boolean ret = addNodeAttributes(node, "", list);
        gcont.addNode(node);
        return ret;
    }

    private boolean addNodeAttributes(NodeDraft node, String prefix, ArrayList list) {
        boolean ret = true;
        for (int i = 0; i < list.size(); i += 2) {
            String key = (String) list.get(i);
            Object value = list.get(i + 1);
            if ("id".equalsIgnoreCase(key) || "label".equalsIgnoreCase(key)) {
                continue; // already parsed
            }
            if (value instanceof ArrayList) {
                // keep the  hierarchy
                ret = addNodeAttributes(node, prefix + "." + key, (ArrayList) value);
                if (!ret) {
                    break;
                }
            } else {
                node.setValue(key, value);
            }
        }
        return ret;
    }

    private boolean parseEdge(ArrayList list) {
        String id = null;
        for (int i = 0; i < list.size(); i += 2) {
            String key = (String) list.get(i);
            Object value = list.get(i + 1);
            if ("id".equalsIgnoreCase(key)) {
                id = value.toString();
            }
        }
        EdgeDraft edgeDraft;
        if (id != null) {
            edgeDraft = gcont.newEdgeDraft(id);
        } else {
            edgeDraft = gcont.newEdgeDraft();
        }

        for (int i = 0; i < list.size(); i += 2) {
            String key = (String) list.get(i);
            Object value = list.get(i + 1);
            if ("source".equals(key)) {
                NodeDraft source = gcont.getNode(value.toString());
                edgeDraft.setSource(source);
            } else if ("target".equals(key)) {
                NodeDraft target = gcont.getNode(value.toString());
                edgeDraft.setTarget(target);
            } else if ("value".equals(key) || "weight".equals(key)) {
                if (value instanceof Number) {
                    edgeDraft.setWeight(((Number) value).doubleValue());
                }
            } else if ("label".equals(key)) {
                edgeDraft.setLabel(value.toString());
            }
        }
        boolean ret = addEdgeAttributes(edgeDraft, "", list);
        gcont.addEdge(edgeDraft);
        return ret;
    }

    private boolean addEdgeAttributes(EdgeDraft edge, String prefix, ArrayList list) {
        boolean ret = true;
        for (int i = 0; i < list.size(); i += 2) {
            String key = (String) list.get(i);
            Object value = list.get(i + 1);
            if ("id".equalsIgnoreCase(key) || "source".equalsIgnoreCase(key) || "target".equalsIgnoreCase(key) || "value".equalsIgnoreCase(key) || "weight".equalsIgnoreCase(key) || "label".equalsIgnoreCase(key)) {
                continue; // already parsed
            }
            if (value instanceof ArrayList) {
                // keep the hierarchy
                ret = addEdgeAttributes(edge, prefix + "." + key, (ArrayList) value);
                if (!ret) {
                    break;
                }
            } else if ("directed".equalsIgnoreCase(key)) {
                if (value instanceof Number) {
                    EdgeType type = ((Number) value).intValue() == 1 ? EdgeType.FORWARD : EdgeType.NONE;
                    edge.setDirection(type);
                } else {
                    report.logIssue(new Issue("error parsing edge direction", Issue.Level.WARNING));
                }
            } else {
                edge.setValue(key, value);
            }
        }
        return ret;
    }

}
