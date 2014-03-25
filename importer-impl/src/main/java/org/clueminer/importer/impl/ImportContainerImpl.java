package org.clueminer.importer.impl;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.importer.Issue;
import org.clueminer.importer.Issue.Level;
import org.clueminer.io.importer.api.AttributeDraft;
import org.clueminer.io.importer.api.Container;
import org.clueminer.io.importer.api.ContainerUnloader;
import org.clueminer.io.importer.api.InstanceDraft;
import org.clueminer.io.importer.api.Report;
import org.clueminer.types.ContainerLoader;
import org.openide.util.NbBundle;

/**
 *
 * @author Tomas Barton
 */
public class ImportContainerImpl implements Container, ContainerLoader, ContainerUnloader {

    private String source;
    protected static final int NULL_INDEX = -1;

    private final ObjectList<InstanceDraft> instanceList;
    private final ObjectList<AttributeDraft> attributeList;
    private Dataset<? extends Instance> dataset;
    private final Object2ObjectMap<String, AttributeDraft> attributeColumns;
    private Report report;
    private final Object2IntMap<String> instanceMap;

    public ImportContainerImpl() {
        instanceList = new ObjectArrayList<InstanceDraft>();
        attributeList = new ObjectArrayList<AttributeDraft>();
        attributeColumns = new Object2ObjectOpenHashMap<String, AttributeDraft>();
        instanceMap = new Object2IntOpenHashMap<String>();
        instanceMap.defaultReturnValue(NULL_INDEX);
    }

    @Override
    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public ContainerLoader getLoader() {
        return this;
    }

    @Override
    public void addInstance(InstanceDraft instance) {
        checkInstanceDraft(instance);

        if (instanceMap.containsKey(instance.getId())) {
            String message = NbBundle.getMessage(ImportContainerImpl.class, "ImportContainerException_instanceExist", instance.getId());
            report.logIssue(new Issue(message, Level.WARNING));
            return;
        }

        int index = instanceList.size();
        instanceList.add(instance);
        instanceMap.put(instance.getId(), index);
    }

    @Override
    public void setReport(Report report) {
        this.report = report;
    }

    @Override
    public Report getReport() {
        return report;
    }

    @Override
    public void closeLoader() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean verify() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setDataset(Dataset<? extends Instance> dataset) {
        this.dataset = dataset;
    }

    @Override
    public Dataset<? extends Instance> getDataset() {
        return dataset;
    }

    @Override
    public int getInstanceCount() {
        return instanceList.size();
    }

    @Override
    public Iterable<InstanceDraft> getInstances() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getAttributeCount() {
        return attributeList.size();
    }

    @Override
    public Iterable<AttributeDraft> getAttributes() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void checkId(String id) {
        if (id == null) {
            throw new NullPointerException();
        }
        if (id.isEmpty()) {
            throw new IllegalArgumentException("The id can't be empty");
        }
    }

    private void checkInstanceDraft(InstanceDraft elmt) {
        if (elmt == null) {
            throw new NullPointerException();
        }
        if (!(elmt instanceof InstanceDraftImpl)) {
            throw new ClassCastException();
        }
    }

    public boolean hasAttribute(String key) {
        return attributeColumns.containsKey(key);
    }

}
