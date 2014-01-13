package org.clueminer.hclust;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.clueminer.clustering.api.dendrogram.DendroNode;

/**
 *
 * @author Tomas Barton
 */
public class BTreePrinter {

    public static <T extends Comparable<?>> void printNode(DendroNode root) {
        int maxLevel = BTreePrinter.maxLevel(root);
        System.out.println("root level = " + root.level());
        printNodeInternal(Collections.singletonList(root), 1, maxLevel);
    }

    private static <T extends Comparable<?>> void printNodeInternal(List<DendroNode> nodes, int level, int maxLevel) {
        if (nodes.isEmpty() || BTreePrinter.isAllElementsNull(nodes)) {
            return;
        }

        int floor = maxLevel - level;
        System.out.println("maxlevel = " + maxLevel);
        System.out.println("level = " + level);

        int endgeLines = (int) Math.pow(2, (Math.max(floor - 1, 0)));
        System.out.println("endge: " + endgeLines);
        int firstSpaces = (int) Math.pow(2, (floor)) - 1;
        System.out.println("first spaces: " + firstSpaces);
        int betweenSpaces = (int) Math.pow(2, (floor + 1)) - 1;
        System.out.println("between spaces " + betweenSpaces);

        BTreePrinter.printWhitespaces(firstSpaces);

        List<DendroNode> newNodes = new ArrayList<DendroNode>();
        for (DendroNode node : nodes) {
            if (node != null) {
                System.out.print(node.toString());
                newNodes.add(node.getLeft());
                newNodes.add(node.getRight());
            } else {
                newNodes.add(null);
                newNodes.add(null);
                System.out.print(" ");
            }

            BTreePrinter.printWhitespaces(betweenSpaces);
        }
        System.out.println("");

        for (int i = 1; i <= endgeLines; i++) {
            for (int j = 0; j < nodes.size(); j++) {
                BTreePrinter.printWhitespaces(firstSpaces - i);
                if (nodes.get(j) == null) {
                    BTreePrinter.printWhitespaces(endgeLines + endgeLines + i + 1);
                    continue;
                }

                if (nodes.get(j).getLeft() != null) {
                    System.out.print("/");
                } else {
                    BTreePrinter.printWhitespaces(1);
                }

                BTreePrinter.printWhitespaces(i + i - 1);

                if (nodes.get(j).getRight() != null) {
                    System.out.print("\\");
                } else {
                    BTreePrinter.printWhitespaces(1);
                }

                BTreePrinter.printWhitespaces(endgeLines + endgeLines - i);
            }

            System.out.println("");
        }

        printNodeInternal(newNodes, level + 1, maxLevel);
    }

    private static void printWhitespaces(int count) {
        for (int i = 0; i < count; i++) {
            System.out.print(" ");
        }
    }

    private static int maxLevel(DendroNode node) {
        if (node.isLeaf()) {
            return 0;
        }

        return Math.max(BTreePrinter.maxLevel(node.getLeft()), BTreePrinter.maxLevel(node.getRight())) + 1;
    }

    private static <T> boolean isAllElementsNull(List<T> list) {
        for (Object object : list) {
            if (object != null) {
                return false;
            }
        }

        return true;
    }
}
