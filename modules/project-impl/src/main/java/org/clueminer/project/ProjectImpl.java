package org.clueminer.project;

import java.io.Serializable;
import org.clueminer.project.api.Project;
import org.clueminer.project.api.Selection;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Tomas Barton
 */
public class ProjectImpl implements Project, Lookup.Provider, Serializable {

    private static final long serialVersionUID = -7829301155784379994L;
    //Lookup
    private transient InstanceContent instanceContent;
    private transient AbstractLookup lookup;
    private Selection selection;

    public ProjectImpl() {
        init();
    }

    protected final void init() {
        instanceContent = new InstanceContent();
        lookup = new AbstractLookup(instanceContent);
        selection = new SelectionImpl();

        //Init Default Content
        ProjectMetaDataImpl metaDataImpl = new ProjectMetaDataImpl();
        add(metaDataImpl);
        ProjectInformationImpl projectInformationImpl = new ProjectInformationImpl(this);
        add(projectInformationImpl);
        WorkspaceProviderImpl workspaceProviderImpl = new WorkspaceProviderImpl(this);
        add(workspaceProviderImpl);
    }

    @Override
    public void add(Object instance) {
        instanceContent.add(instance);
    }

    @Override
    public void remove(Object instance) {
        instanceContent.remove(instance);
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    @Override
    public String getName() {
        ProjectInformationImpl info = getLookup().lookup(ProjectInformationImpl.class);
        if (info != null) {
            return info.getName();
        }
        return "(unknown)";
    }

    @Override
    public Selection getSelection() {
        return selection;
    }
}
