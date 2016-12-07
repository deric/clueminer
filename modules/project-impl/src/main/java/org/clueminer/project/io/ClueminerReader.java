package org.clueminer.project.io;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import org.clueminer.project.impl.ProjectControllerImpl;
import org.clueminer.project.impl.ProjectImpl;
import org.clueminer.project.impl.ProjectInformationImpl;
import org.clueminer.project.impl.WorkspaceImpl;
import org.clueminer.project.impl.WorkspaceInformationImpl;
import org.clueminer.project.impl.WorkspaceProviderImpl;
import org.clueminer.project.api.Project;
import org.clueminer.project.api.Workspace;
import org.clueminer.spi.WorkspacePersistenceProvider;
import org.openide.util.Cancellable;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public class ClueminerReader implements Cancellable {

  private ProjectImpl project;
    private boolean cancel = false;
    private Map<String, WorkspacePersistenceProvider> providers;
    private WorkspacePersistenceProvider currentProvider;

    public ClueminerReader() {
        providers = new LinkedHashMap<String, WorkspacePersistenceProvider>();
        for (WorkspacePersistenceProvider w : Lookup.getDefault().lookupAll(WorkspacePersistenceProvider.class)) {
            try {
                String id = w.getIdentifier();
                if (id != null && !id.isEmpty()) {
                    providers.put(w.getIdentifier(), w);
                }
            } catch (Exception e) {
            }
        }
    }

    @Override
    public boolean cancel() {
        cancel = true;
        return true;
    }

    public Project readAll(XMLStreamReader reader, Project project) throws Exception {
        ProjectInformationImpl info = project.getLookup().lookup(ProjectInformationImpl.class);
        WorkspaceProviderImpl workspaces = project.getLookup().lookup(WorkspaceProviderImpl.class);
        this.project = (ProjectImpl) project;

        boolean end = false;
        while (reader.hasNext() && !end) {
            Integer eventType = reader.next();
            if (eventType.equals(XMLEvent.START_ELEMENT)) {
                String name = reader.getLocalName();
                if ("clueminerFile".equalsIgnoreCase(name)) {
                    //Version
                    String version = reader.getAttributeValue(null, "version");
                    if (version == null || version.isEmpty() || Double.parseDouble(version) < 0.1) {
                        throw new ClueminerFormatException("Clueminer project file version must be at least 0.1");
                    }
                } else if ("project".equalsIgnoreCase(name)) {
                    info.setName(reader.getAttributeValue(null, "name"));
                }  else if ("workspace".equalsIgnoreCase(name)) {
                    Workspace workspace = readWorkspace(reader);

                    //Current workspace
                    if (workspace.getLookup().lookup(WorkspaceInformationImpl.class).isOpen()) {
                        workspaces.setCurrentWorkspace(workspace);
                    }
                }
            } else if (eventType.equals(XMLStreamReader.END_ELEMENT)) {
                if ("project".equalsIgnoreCase(reader.getLocalName())) {
                    end = true;
                }
            }
        }

        return project;
    }
    
     public Workspace readWorkspace(XMLStreamReader reader) throws Exception {
        WorkspaceImpl workspace = project.getLookup().lookup(WorkspaceProviderImpl.class).newWorkspace();
        WorkspaceInformationImpl info = workspace.getLookup().lookup(WorkspaceInformationImpl.class);

        //Name
        info.setName(reader.getAttributeValue(null, "name"));

        //Status
        String workspaceStatus = reader.getAttributeValue(null, "status");
        if (workspaceStatus.equals("open")) {
            info.open();
        } else if (workspaceStatus.equals("closed")) {
            info.close();
        } else {
            info.invalid();
        }

        //Hack to set this workspace active, when readers need to use attributes for instance
        ProjectControllerImpl pc = Lookup.getDefault().lookup(ProjectControllerImpl.class);
        pc.setTemporaryOpeningWorkspace(workspace);

        //WorkspacePersistent
        readWorkspaceChildren(workspace, reader);
        if (currentProvider != null) {
            //One provider not correctly closed
            throw new ClueminerFormatException("The '" + currentProvider.getIdentifier() + "' persistence provider is not ending read.");
        }
        pc.setTemporaryOpeningWorkspace(null);

        return workspace;
    }

    public void readWorkspaceChildren(Workspace workspace, XMLStreamReader reader) throws Exception {
        boolean end = false;
        while (reader.hasNext() && !end) {
            Integer eventType = reader.next();
            if (eventType.equals(XMLEvent.START_ELEMENT)) {
                String name = reader.getLocalName();
                WorkspacePersistenceProvider pp = providers.get(name);
                if (pp != null) {
                    currentProvider = pp;
                    try {
                        pp.readXML(reader, workspace);
                    } catch (UnsupportedOperationException e) {
                    }
                }
            } else if (eventType.equals(XMLStreamReader.END_ELEMENT)) {
                if ("workspace".equalsIgnoreCase(reader.getLocalName())) {
                    end = true;
                    currentProvider = null;
                }
            }
        }
    }

}
