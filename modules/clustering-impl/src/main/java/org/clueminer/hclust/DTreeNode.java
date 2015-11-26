package org.clueminer.hclust;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import org.clueminer.clustering.api.dendrogram.DendroNode;

/**
 *
 * @author Tomas Barton
 */
public class DTreeNode implements DendroNode {

    private boolean root = false;
    protected DendroNode left;
    protected DendroNode right;
    protected DendroNode parent;
    private double height;
    private double position;
    private int level = -1;
    private int id;
    private double min = Double.MAX_VALUE;

    public DTreeNode() {
    }

    public DTreeNode(int id) {
        this.id = id;
    }

    public DTreeNode(DendroNode parent) {
        this.parent = parent;
    }

    public DTreeNode(boolean root) {
        this.root = root;
    }

    /**
     * Leaf doesn't have any children
     *
     * @return
     */
    @Override
    public boolean isLeaf() {
        return !hasLeft() && !hasRight();
    }

    @Override
    public boolean isRoot() {
        return root;
    }

    @Override
    public DendroNode getLeft() {
        return left;
    }

    @Override
    public boolean hasLeft() {
        return left != null;
    }

    @Override
    public DendroNode getRight() {
        return right;
    }

    @Override
    public boolean hasRight() {
        return right != null;
    }

    @Override
    public DendroNode setLeft(DendroNode left) {
        this.left = left;
        if (left != null) {
            left.setParent(this);
        }
        return this.left;
    }

    @Override
    public DendroNode setRight(DendroNode right) {
        this.right = right;
        if (right != null) {
            right.setParent(this);
        }
        return this.right;
    }

    @Override
    public int level() {
        if (level == -1) {
            if (hasLeft() && hasRight()) {
                return 1 + Math.max(getLeft().level(), getRight().level());
            } else if (hasLeft() && !hasRight()) {
                return 1 + getLeft().level();
            } else if (!hasLeft() && hasRight()) {
                return 1 + getRight().level();
            } else {
                return 0;
            }
        } else {
            return level;
        }
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public DendroNode getParent() {
        return parent;
    }

    @Override
    public void setParent(DendroNode parent) {
        this.parent = parent;
    }

    @Override
    public int childCnt() {
        int cnt = 0;
        if (hasLeft()) {
            cnt += 1 + getLeft().childCnt();
        }
        if (hasRight()) {
            cnt += 1 + getRight().childCnt();
        }
        return cnt;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public DendroNode setHeight(double height) {
        this.height = height;
        return this;
    }

    @Override
    public double getPosition() {
        return position;
    }

    @Override
    public DendroNode setPosition(double position) {
        this.position = position;
        return this;
    }

    @Override
    public DendroNode setId(int id) {
        this.id = id;
        return this;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ").append("#").append(getId()).append(", ")
                .append(String.format("%.2f", getHeight()))
                .append(", level = ").append(level).append(" ");
        if (left != null) {
            sb.append("L: ").append(left.getId());
        }
        if (right != null) {
            sb.append(", R: ").append(right.getId());
        }
        sb.append(" ]");
        return sb.toString();
    }

    protected void printNodeValue(OutputStreamWriter out) throws IOException {
        out.write("#" + getId() + " (" + String.format("%.2f", getHeight()) + ")");
        out.write('\n');
    }

    // use string and not stringbuffer on purpose as we need to change the indent at each recursion
    @Override
    public void printTree(OutputStreamWriter out, boolean isRight, String indent) throws IOException {
        if (left != null) {
            left.printTree(out, false, indent + (isRight ? " |      " : "        "));
        }

        out.write(indent);
        if (isRight) {
            out.write(" \\");
        } else {
            out.write(" /");
        }
        out.write("----- ");
        printNodeValue(out);
        if (right != null) {
            right.printTree(out, true, indent + (isRight ? "        " : " |      "));
        }
    }

    @Override
    public void printTreeWithHeight(OutputStreamWriter out, boolean isRight, String indent) throws IOException {
        String spaces = "";
        String dashes = "";
        for (int i = 0; i < parent.getHeight() - height - 1; i++) {
            spaces += "      ";
            dashes += "------";
        }

        if (left != null) {
            left.printTreeWithHeight(out, false, indent + (isRight ? " |    " : "      ") + spaces);
        }

        out.write(indent);
        if (isRight) {
            out.write(" \\");
        } else {
            out.write(" /");
        }
        out.write(dashes + "----- ");
        printNodeValue(out);
        if (right != null) {
            right.printTreeWithHeight(out, true, indent + (isRight ? "      " : " |    ") + spaces);
        }
    }

    /**
     * Valid only for leaves
     *
     * @return
     */
    @Override
    public int getIndex() {
        return -1;
    }

    protected String printBinary(int number, int padding) {
        String binString = Integer.toBinaryString(number);
        if (padding > 0) {
            int length = padding - binString.length();
            char[] padArray = new char[length];
            Arrays.fill(padArray, '0');
            String buff = new String(padArray);
            return buff + binString;
        }
        return Integer.toBinaryString(number);
    }

    /**
     * {@inheritDoc}
     *
     * @param min
     */
    @Override
    public void setMin(double min) {
        this.min = min;
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public double getMin() {
        return min;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void swapChildren() {
        if (hasLeft() && hasRight()) {
            DendroNode tmp = getLeft();
            setLeft(getRight());
            setRight(tmp);
        } else {
            throw new RuntimeException("can't swap children nodes. missing nodes");
        }
    }
}
