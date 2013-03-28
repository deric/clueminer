package org.clueminer.clustering.explorer;

import org.clueminer.dataset.api.Instance;
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
public class InstanceNode extends AbstractNode {

    private Instance instance;

    public InstanceNode(Instance inst) {
        super(Children.LEAF, Lookups.singleton(inst));
        this.instance = inst;
        setShortDescription("<html><b>" + inst.getName() + "</b><br>Elements: " + inst.size() + "</html>");
    }

    @Override
    public String getHtmlDisplayName() {
        String msg = "<html>" + instance.getName() + "</html>";
        return msg;
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        set.setDisplayName("Instance properties");
        Instance obj = getLookup().lookup(Instance.class);

        try {
            Property indexProp = new PropertySupport.Reflection(obj, Integer.class, "getID", null);
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
