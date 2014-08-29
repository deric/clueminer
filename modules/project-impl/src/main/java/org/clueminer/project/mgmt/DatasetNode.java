package org.clueminer.project.mgmt;

import org.clueminer.dataset.api.Dataset;
import org.openide.ErrorManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Tomas Barton
 */
class DatasetNode extends AbstractNode {

    private final Dataset dataset;

    public DatasetNode(Dataset dataset) {
        super(Children.LEAF, Lookups.singleton(dataset));
        this.dataset = dataset;
        setShortDescription("<html><b>" + dataset.getName() + "</b><br>size: " + dataset.size() + "</html>");
    }

    @Override
    public String getHtmlDisplayName() {
        String msg = "<html>" + dataset.getName() + "</html>";
        return msg;
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        set.setDisplayName("Dataset properties");
        Dataset obj = getLookup().lookup(Dataset.class);

        try {
            Property indexProp = new PropertySupport.Reflection(obj, Integer.class, "getId", null);
            Property nameProp = new PropertySupport.Reflection(obj, Integer.class, "getName", null);
            Property sizeProp = new PropertySupport.Reflection(obj, Integer.class, "size", null);

            indexProp.setName("ID");
            sizeProp.setName("Size");
            nameProp.setName("Name");

            set.put(indexProp);
            set.put(sizeProp);
            set.put(nameProp);

        } catch (NoSuchMethodException ex) {
            ErrorManager.getDefault();
        }
        sheet.put(set);
        return sheet;
    }

}
