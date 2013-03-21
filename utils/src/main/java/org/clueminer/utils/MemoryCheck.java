package org.clueminer.utils;

import javax.swing.JOptionPane;

/**
 *
 * @author Tomas Barton
 */
public class MemoryCheck {

    /**
     * Determines available memory in Java
     */
    public static int javaMemoryAssess(int n, boolean ordered) {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long totalFreeMemory = freeMemory + (maxMemory - allocatedMemory);
        int maxN = (int) (Math.sqrt((totalFreeMemory / 1024) * 256) * Math.sqrt(2));
        String opt = " ";
        if (ordered) {
            maxN = (int) (Math.sqrt((totalFreeMemory / 1024) * 256) / Math.sqrt(2));
            opt = " optimized ";
        }
        Object[] optionInfo = {"INFO", "OK"};
        if (maxN < n) {
            int option = JOptionPane.showOptionDialog(null,
                    "Java does not currently have enough memory to run this analysis." + "\n"
                    + "Free memory: " + freeMemory / 1024 + " kb" + "\n"
                    + "Allocated memory: " + allocatedMemory / 1024 + " kb" + "\n"
                    + "Max memory: " + maxMemory / 1024 + " kb" + "\n"
                    + "Total free memory: " + totalFreeMemory / 1024 + " kb" + "\n"
                    + "Your system can handle up to " + maxN + " items for" + opt  + "\n"
                    + "You are attempting to run " + n + " items." + "\n"
                    + "----------------------------------------------------------------------------------" + "\n"
                    + "Click 'INFO' for instructions on increasing your Java memory.", "Not Enough Java Memory Error", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                    null, optionInfo, optionInfo[0]);
            if (option == 0) {
                return 2;
            }
            return 1;
        }
        return 0;
    }
}
