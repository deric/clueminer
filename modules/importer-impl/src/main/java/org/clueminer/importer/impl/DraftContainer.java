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

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.JComponent;
import org.clueminer.attributes.BasicAttrRole;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.AttributeBuilder;
import org.clueminer.dataset.api.AttributeRole;
import org.clueminer.dataset.api.ColorGenerator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.InstanceBuilder;
import org.clueminer.importer.Issue;
import org.clueminer.importer.Issue.Level;
import org.clueminer.io.importer.api.AttributeDraft;
import org.clueminer.io.importer.api.Container;
import org.clueminer.io.importer.api.ContainerLoader;
import org.clueminer.io.importer.api.InstanceDraft;
import org.clueminer.io.importer.api.Report;
import org.clueminer.math.Matrix;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 * Container for storing pre-loaded data during import. Types are relaxed at this
 * stage.
 *
 * @author Tomas Barton
 * @param <E>
 */
@org.openide.util.lookup.ServiceProvider(service = Container.class)
public class DraftContainer<E extends InstanceDraft> implements Dataset<E>, Container, ContainerLoader<E> {

    private String source;
    private FileObject file;
    protected static final int NULL_INDEX = -1;

    protected ObjectList<E> instanceList;
    protected Int2ObjectOpenHashMap<AttributeDraft> attributeList;
    private Dataset<? extends Instance> dataset;
    protected Object2ObjectMap<String, AttributeDraft> attributeMap;
    protected Report report;
    private Object2IntMap<String> instanceMap;
    private AttributeBuilder attributeBuilder;
    private int linesCnt;
    private Class<?> defaultNumericType = Double.class;
    private String dataType = "discrete";
    private String md5 = null;
    private String name;
    private final TreeSet<Object> classes = new TreeSet<>();

    public DraftContainer() {
        report = new Report();
        instanceList = new ObjectArrayList<>();
        attributeList = new Int2ObjectOpenHashMap<>();
        attributeMap = new Object2ObjectOpenHashMap<>();
        instanceMap = new Object2IntOpenHashMap<>();
        instanceMap.defaultReturnValue(NULL_INDEX);
    }

    @Override
    public void setSource(String src) {
        if (src == null) {
            throw new RuntimeException("source can't be null");
        }
        this.source = src;
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
    public void addInstance(E instance, int row) {
        checkInstanceDraft(instance);

        if (instanceMap.containsKey(instance.getId())) {
            String message = NbBundle.getMessage(DraftContainer.class,
                    "ImportContainerException_instanceExist", instance.getId(), row);
            report.logIssue(new Issue(message, Level.WARNING));
            return;
        }

        int index = instanceList.size();
        instanceList.add(instance);
        instanceMap.put(instance.getId(), index);
    }

    @Override
    public E set(int instanceIdx, E inst) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AttributeDraft getAttribute(String key, Class typeClass) {
        AttributeDraft attr = attributeMap.get(key);

        if (!attr.getJavaType().equals(typeClass)) {
            report.logIssue(new Issue(NbBundle.getMessage(DraftContainer.class,
                    "ImportContainerException_Attribute_Type_Mismatch",
                    key, attr.getClass()), Level.SEVERE));

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
    public Iterable<E> getInstances() {
        return new NullFilterIterable<>(instanceList);
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
    public Iterable<AttributeDraft> getAttrIter() {
        return (Iterable<AttributeDraft>) attributeList.values();
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated use attribute builder instead
     */
    @Override
    public AttributeDraft createAttribute(int index, String name) {
        AttributeDraft attr;
        //try to avoid duplicate attribute
        if (!hasAttributeAtIndex(index)) {
            attr = new AttributeDraftImpl(name);
            attr.setIndex(index);
            attr.setJavaType(defaultNumericType);
            attr.setRole(BasicAttrRole.INPUT);
            attributeMap.put(name, attr);
            attributeList.put(index, attr);
        } else {
            attr = attributeList.get(index);
            if (!attr.getName().equals(name)) {
                //update attribute's names map
                attributeMap.remove(attr.getName());
                attr.setName(name);
                attributeMap.put(name, attr);
            }
        }
        return attr;
    }

    public E getInstance(String id) {
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
    public FileObject getFile() {
        return file;
    }

    @Override
    public void setFile(FileObject file) {
        this.file = file;
        if (file != null) {
            setSource(file.getPath());
        }
    }

    /**
     * remove this? we have number of instances
     *
     * @param count
     */
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
    public void setDefaultNumericType(Class<?> type) {
        defaultNumericType = type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDataType() {
        return dataType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasPrimaryKey() {
        for (AttributeDraft attr : attributeList.values()) {
            if (attr.getRole() == BasicAttrRole.ID) {
                return true;
            }
        }
        return false;
    }

    @Override
    public AttributeDraft getAttribute(int index) {
        return attributeList.get(index);
    }

    /**
     * Check if we have an attribute for given column index
     *
     * @param index
     * @return
     */
    @Override
    public boolean hasAttributeAtIndex(int index) {
        return attributeList.containsKey(index);
    }

    @Override
    public E getInstance(int index) {
        return instanceList.get(index);
    }

    /**
     * After reset we can start clean import with new settings
     */
    @Override
    public void reset() {
        report = new Report();
        instanceList = new ObjectArrayList<>();
        //we keep attributes from previous iteration
        //reset only instances
        instanceMap = new Object2IntOpenHashMap<>();
        instanceMap.defaultReturnValue(NULL_INDEX);
    }

    @Override
    public void resetAttributes() {
        attributeList = new Int2ObjectOpenHashMap<>();
        attributeMap = new Object2ObjectOpenHashMap<>();
    }

    @Override
    public String getMD5() {
        return md5;
    }

    @Override
    public void setMD5(String md5) {
        this.md5 = md5;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public SortedSet<Object> getClasses() {
        return classes;
    }

    @Override
    public boolean addAll(Dataset<? extends E> d) {
        Iterator<? extends E> it = d.iterator();
        while (it.hasNext()) {
            add(it.next());
        }
        return !it.hasNext();
    }

    @Override
    public int attributeCount() {
        return attributeList.size();
    }

    @Override
    public E instance(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public E getRandom(Random rand) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int classIndex(Object clazz) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object classValue(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void changedClass(Object orig, Object current, Object source) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Attribute[] copyAttributes() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addAttribute(Attribute attr) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Attribute getAttribute(String attributeName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getAttributeValue(String attributeName, int instanceIdx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getAttributeValue(Attribute attribute, int instanceIdx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double get(int instanceIdx, int attributeIndex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setAttributeValue(String attributeName, int instanceIdx, double value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setAttribute(int index, Attribute attr) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setAttributes(Map<Integer, Attribute> attributes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setAttributes(Attribute[] attributes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public InstanceBuilder<E> builder() {
        return new InstanceDraftBuilder<>();
    }

    @Override
    public AttributeBuilder attributeBuilder() {
        return new AttributeDraftBuilder(this);
    }

    @Override
    public Dataset<? extends E> copy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Dataset<? extends E> duplicate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JComponent getPlotter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double min() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double max() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void resetStats() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection<? extends Number> attrCollection(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<Integer, Attribute> getAttributes() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getId() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setId(String id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean add(E i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public E get(int index) {
        return (E) instanceList.get(index);
    }

    @Override
    public boolean hasIndex(int idx) {
        return instanceList.get(idx) != null;
    }

    @Override
    public int size() {
        return instanceList.size();
    }

    @Override
    public boolean isEmpty() {
        return instanceList.size() == 0;
    }

    @Override
    public Dataset<E> getParent() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setParent(Dataset<E> parent) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean hasParent() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Attribute[] attributeByRole(AttributeRole role) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void set(int instanceIdx, int attrIdx, double value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double[][] arrayCopy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setColorGenerator(ColorGenerator cg) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void ensureCapacity(int size) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getCapacity() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addChild(String key, Dataset<E> dataset) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Dataset<E> getChild(String key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterator<String> getChildIterator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Matrix asMatrix() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static class NullFilterIterable<T extends InstanceDraft> implements Iterable<T> {

        private final Collection<T> collection;

        public NullFilterIterable(Collection elementCollection) {
            this.collection = elementCollection;
        }

        @Override
        public Iterator<T> iterator() {
            return new NullFilterIterator<>(collection);
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
