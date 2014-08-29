package org.clueminer.io.importer.api;

import java.util.Collection;
import java.util.LinkedHashMap;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public class AttributeParserFactory extends ServiceFactory<AttributeParser> {

    private static AttributeParserFactory instance;

    public static AttributeParserFactory getInstance() {
        if (instance == null) {
            instance = new AttributeParserFactory();
        }
        return instance;
    }

    private AttributeParserFactory() {
        providers = new LinkedHashMap<String, AttributeParser>();
        Collection<? extends AttributeParser> list = Lookup.getDefault().lookupAll(AttributeParser.class);
        for (AttributeParser c : list) {
            providers.put(c.getName(), c);
        }
    }
}
