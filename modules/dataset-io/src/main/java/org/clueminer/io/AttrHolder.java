package org.clueminer.io;

/**
 *
 * @author Tomas Barton
 */
public class AttrHolder {

    private String name;
    private String type;
    private String range;
    private String allowed;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public String getAllowed() {
        return allowed;
    }

    public void setAllowed(String allowed) {
        this.allowed = allowed;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("AttrHolder[");
        sb.append("name: ").append(getName()).append(", ")
                .append("type: ").append(getType()).append(", ");
        if (range != null && !range.isEmpty()) {
            sb.append("range: ").append(getRange());
        }
        if (allowed != null && !allowed.isEmpty()) {
            sb.append("allowed: ").append(getAllowed());
        }
        sb.append("]");
        return sb.toString();
    }

}
