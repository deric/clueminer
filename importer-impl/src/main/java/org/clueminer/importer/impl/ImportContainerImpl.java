package org.clueminer.importer.impl;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import org.clueminer.dataset.api.AttributeBuilder;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.importer.Issue;
import org.clueminer.importer.Issue.Level;
import org.clueminer.io.importer.api.AttributeDraft;
import org.clueminer.io.importer.api.Container;
import org.clueminer.io.importer.api.ContainerLoader;
import org.clueminer.io.importer.api.InstanceDraft;
import org.clueminer.io.importer.api.Report;
import org.openide.util.NbBundle;

/**
 *
 * @author Tomas Barton
 */
@org.openide.util.lookup.ServiceProvider(service = Container.class)
public class ImportContainerImpl implements Container, ContainerLoader {

    private String source;
    private File file;
    protected static final int NULL_INDEX = -1;

    private final ObjectList<InstanceDraft> instanceList;
    private final ObjectList<AttributeDraft> attributeList;
    private Dataset<? extends Instance> dataset;
    private final Object2ObjectMap<String, AttributeDraft> attributeMap;
    private Report report;
    private final Object2IntMap<String> instanceMap;
    private AttributeBuilder attributeBuilder;
    private int linesCnt;
    private int attrCnt;
    private Object defaultNumericType = Double.class;

    public ImportContainerImpl() {
        report = new Report();
        instanceList = new ObjectArrayList<InstanceDraft>();
        attributeList = new ObjectArrayList<AttributeDraft>();
        attributeMap = new Object2ObjectOpenHashMap<String, AttributeDraft>();
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
    public void addInstance(InstanceDraft instance, int row) {
        checkInstanceDraft(instance);

        if (instanceMap.containsKey(instance.getId())) {
            String message = NbBundle.getMessage(ImportContainerImpl.class, "ImportContainerException_instanceExist", instance.getId(), row);
            report.logIssue(new Issue(message, Level.WARNING));
            return;
        }

        int index = instanceList.size();
        instanceList.add(instance);
        instanceMap.put(instance.getId(), index);
    }

    @Override
    public AttributeDraft getAttribute(String key, Class typeClass) {
        AttributeDraft attr = attributeMap.get(key);

        if (!attr.getType().equals(typeClass)) {
            report.logIssue(new Issue(NbBundle.getMessage(ImportContainerImpl.class, "ImportContainerException_Attribute_Type_Mismatch", key, attr.getClass()), Level.SEVERE));

        }
        return attr;
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
        //TODO: check integrity
    }

    @Override
    public boolean verify() {
        /**
         * TODO: check each instance types etc.
         */
        return true;
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
        return new NullFilterIterable<InstanceDraft>(instanceList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAttributeCount() {
        return attributeList.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<AttributeDraft> getAttributes() {
        return attributeMap.values();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AttributeDraft createAttribute(int index, String name) {
        AttributeDraft attr = new AttributeDraftImpl(name);
        attr.setIndex(index);
        attr.setType(defaultNumericType);
        attributeMap.put(name, attr);
        attributeList.add(index, attr);
        return attr;
    }

    public InstanceDraft getInstance(String id) {
        checkId(id);

        int index = instanceMap.getInt(id);
        if (index == NULL_INDEX) {
            return null;
        }
        return instanceList.get(index);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasAttribute(String key) {
        return attributeMap.containsKey(key);
    }

    @Override
    public AttributeBuilder getAttributeBuilder() {
        return attributeBuilder;
    }

    @Override
    public void setAttributeBuilder(AttributeBuilder builder) {
        this.attributeBuilder = builder;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public void setFile(File file) {
        this.file = file;
        setSource(file.getAbsolutePath());
    }

    @Override
    public void setNumberOfLines(int count) {
        this.linesCnt = count;
    }

    @Override
    public int getNumberOfLines() {
        return linesCnt;
    }

    @Override
    public Object getDefaultNumericType() {
        return defaultNumericType;
    }

    @Override
    public void setDefaultNumericType(Object type) {
        defaultNumericType = type;
    }

    @Override
    public void setAttributeCount(int cnt) {
        this.attrCnt = cnt;
    }

    private static class NullFilterIterable<T extends InstanceDraft> implements Iterable<T> {

        private final Collection<T> collection;

        public NullFilterIterable(Collection elementCollection) {
            this.collection = elementCollection;
        }

        @Override
        public Iterator<T> iterator() {
            return new NullFilterIterator<T>(collection);
        }
    }

    private static class NullFilterIterator<T extends InstanceDraft> implements Iterator<T> {

        private T pointer;
        private final Iterator<T> itr;

        public NullFilterIterator(Collection<T> elementCollection) {
            this.itr = elementCollection.iterator();
        }

        @Override
        public boolean hasNext() {
            while (itr.hasNext()) {
                pointer = itr.next();
                if (pointer != null) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public T next() {
            return pointer;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }
}
