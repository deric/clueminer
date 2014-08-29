package org.clueminer.spi;

import java.util.Collection;
import java.util.LinkedHashMap;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public class FileImporterFactory extends ServiceFactory<FileImporter> {

    private static FileImporterFactory instance;

    public static FileImporterFactory getInstance() {
        if (instance == null) {
            instance = new FileImporterFactory();
        }
        return instance;
    }

    private FileImporterFactory() {
        providers = new LinkedHashMap<String, FileImporter>();
        Collection<? extends FileImporter> list = Lookup.getDefault().lookupAll(FileImporter.class);
        for (FileImporter c : list) {
            providers.put(c.getName(), c);
        }
        sort();
    }

}
