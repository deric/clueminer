package org.clueminer.spi;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.clueminer.project.api.Workspace;

/**
 *
 * @author Tomas Barton
 */
public interface WorkspacePersistenceProvider {

    public void writeXML(XMLStreamWriter writer, Workspace workspace);

    public void readXML(XMLStreamReader reader, Workspace workspace);

    public String getIdentifier();
}
