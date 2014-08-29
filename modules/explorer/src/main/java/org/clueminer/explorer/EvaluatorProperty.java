package org.clueminer.explorer;

import java.lang.reflect.InvocationTargetException;
import org.openide.nodes.PropertySupport;

/**
 *
 * @author Tomas Barton
 */
public class EvaluatorProperty extends PropertySupport.ReadOnly<String> {

    private String value;


    public EvaluatorProperty(String name, double value) {
        super(name, String.class, name, "");
        this.value = String.format("%1$,.2f", value);
    }

    public EvaluatorProperty(String name, Class<String> type, String displayName, String shortDescription) {
        super(name, type, displayName, shortDescription);
    }

    @Override
    public String getValue() throws IllegalAccessException, InvocationTargetException {
        return value;
    }

}
