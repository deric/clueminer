package org.clueminer.project.io;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.stream.XMLStreamWriter;
import org.clueminer.project.impl.WorkspaceProviderImpl;
import org.clueminer.project.api.Project;
import org.clueminer.project.api.ProjectInformation;
import org.clueminer.project.api.ProjectMetaData;
import org.clueminer.project.api.Workspace;
import org.clueminer.project.api.WorkspaceInformation;
import org.clueminer.spi.WorkspacePersistenceProvider;
import org.clueminer.utils.Version;
import org.openide.util.Cancellable;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public class ClueminerWriter implements Cancellable {

    private int tasks = 0;
    private Map<String, WorkspacePersistenceProvider> providers;
    private static String FORMAT_VERSION = "1.0";

    public ClueminerWriter() {
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

    public void writeAll(Project project, XMLStreamWriter writer) throws Exception {
        writer.writeStartDocument("UTF-8", FORMAT_VERSION);
        writer.writeStartElement("clueminerFile");
        writer.writeAttribute("version", Version.CLUEMINER);
        writer.writeComment("File saved from Clueminer "+ Version.CLUEMINER);

        writeCore(writer);
        writeProject(writer, project);

        writer.writeEndElement();
        writer.writeEndDocument();
    }

    public void writeCore(XMLStreamWriter writer) throws Exception {
        //Core
        writer.writeStartElement("core");
        writer.writeAttribute("tasks", String.valueOf(tasks));
        writer.writeStartElement("lastModifiedDate");

        //LastModifiedDate
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        writer.writeCharacters(sdf.format(cal.getTime()));
        writer.writeComment("yyyy-MM-dd HH:mm:ss");

        //Append
        writer.writeEndElement();
        writer.writeEndElement();
    }

    public void writeProject(XMLStreamWriter writer, Project project) throws Exception {
        ProjectInformation info = project.getLookup().lookup(ProjectInformation.class);
        ProjectMetaData metaData = project.getLookup().lookup(ProjectMetaData.class);
        WorkspaceProviderImpl workspaces = project.getLookup().lookup(WorkspaceProviderImpl.class);

        writer.writeStartElement("project");
        writer.writeAttribute("name", info.getName());

        //MetaData
        writer.writeStartElement("metadata");

        writer.writeStartElement("title");
        writer.writeCharacters(metaData.getTitle());
        writer.writeEndElement();

        writer.writeStartElement("keywords");
        writer.writeCharacters(metaData.getKeywords());
        writer.writeEndElement();

        writer.writeStartElement("description");
        writer.writeCharacters(metaData.getDescription());
        writer.writeEndElement();

        writer.writeStartElement("author");
        writer.writeCharacters(metaData.getAuthor());
        writer.writeEndElement();

        writer.writeEndElement();

        //Workspaces
        writer.writeStartElement("workspaces");
        for (Workspace ws : workspaces.getWorkspaces()) {
            writeWorkspace(writer, ws);
        }
        writer.writeEndElement();
        writer.writeEndElement();
    }

    public void writeWorkspace(XMLStreamWriter writer, Workspace workspace) throws Exception {
        WorkspaceInformation info = workspace.getLookup().lookup(WorkspaceInformation.class);

        writer.writeStartElement("workspace");
        writer.writeAttribute("name", info.getName());
        if (info.isOpen()) {
            writer.writeAttribute("status", "open");
        } else if (info.isClosed()) {
            writer.writeAttribute("status", "closed");
        } else {
            writer.writeAttribute("status", "invalid");
        }

        writeWorkspaceChildren(writer, workspace);

        writer.writeEndElement();
    }

    public void writeWorkspaceChildren(XMLStreamWriter writer, Workspace workspace) throws Exception {
        for (WorkspacePersistenceProvider pp : providers.values()) {
            try {
                writer.writeComment("Persistence from " + pp.getClass().getName());
                pp.writeXML(writer, workspace);
            } catch (UnsupportedOperationException e) {
            }
        }
    }

    @Override
    public boolean cancel() {
        return true;
    }
}
