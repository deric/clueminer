/*
 * Copyright (C) 2011-2017 clueminer.org
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
public class InstanceNode extends AbstractNode implements Comparable<InstanceNode> {

    private final Instance instance;

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

    public Instance getInstance() {
        return instance;
    }

    @Override
    public int compareTo(InstanceNode o) {
        Instance curr = this.getInstance();
        Instance other = o.getInstance();

        if (curr.getId() != null && other.getId() != null) {
            return curr.getId().compareTo(other.getId());
        }

        if (curr.getName() != null && other.getName() != null) {
            return curr.getName().compareTo(other.getName());
        }
        //we can't decide
        return 0;
    }
}
