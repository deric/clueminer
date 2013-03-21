package org.clueminer.attributes;

import org.clueminer.dataset.api.AbstractAttribute;
import org.clueminer.dataset.api.IAttributeType;
import org.clueminer.dataset.row.Tools;

/**
 *
 * @author Tomas Barton
 */
public abstract class NominalAttribute extends AbstractAttribute {

    private static final long serialVersionUID = -3830980883541763869L;

    protected NominalAttribute(String name, IAttributeType type) {
        super(name, type);
        //	registerStatistics(new NominalStatistics());
        //	registerStatistics(new UnknownStatistics());
    }

    protected NominalAttribute(NominalAttribute other) {
        super(other);
    }

    @Override
    public boolean isNominal() {
        return true;
    }

    @Override
    public boolean isNumerical() {
        return false;
    }

    /**
     * Returns a string representation and maps the value to a string if type is
     * nominal. The number of digits is ignored.
     */
    @Override
    public String asString(double value, int digits, boolean quoteNominal) {
        if (Double.isNaN(value)) {
            return "?";
        } else {
            try {
                String result = getMapping().mapIndex((int) value);
                if (quoteNominal) {
                    result = Tools.escape(result);
                    result = "\"" + result + "\"";
                }
                return result;
            } catch (Throwable e) {
                return "?";
            }
        }
    }
}
