package edu.hawaii.jmotif.logic.sax.trie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The discord records collection.
 *
 * @author Pavel Senin
 *
 */
public class DiscordRecords {

    /**
     * Default capacity.
     */
    private static final Integer defaultCapacity = 10;
    /**
     * Storage container.
     */
    private final ArrayList<DiscordRecord> discords;
    // max capacity var
    private Integer maxCapacity;

    /**
     * Constructor.
     */
    public DiscordRecords() {
        this.maxCapacity = defaultCapacity;
        this.discords = new ArrayList<DiscordRecord>(this.maxCapacity);
    }

    /**
     * Constructor.
     *
     * @param capacity The initial capacity.
     */
    public DiscordRecords(int capacity) {
        this.maxCapacity = capacity;
        this.discords = new ArrayList<DiscordRecord>(this.maxCapacity);
    }

    /**
     * Set the max capacity of this collection. The overflow elements will be
     * pushed into the collection pushing out ones with less distance value.
     *
     * @param newSize The size to set.
     */
    public void setMaxCapacity(int newSize) {
        this.maxCapacity = newSize;
    }

    /**
     * Add a new discord to the list. Here is a trick. This method will also
     * check if the current distance is less than best so far (best in the
     * table). If so - there is no need to continue that inner loop - the MAGIC
     * optimization.
     *
     * @param discord The discord instance to add.
     * @return if the discord got added.
     */
    public boolean add(DiscordRecord discord) {

        // System.out.println(" + discord record " + discord);

        // check if here is still a room for the new element
        //
        if (this.discords.size() < this.maxCapacity) {
            if (this.discords.isEmpty()) {
                assert true;
            }
            // if so - just add it in
            this.discords.add(discord);
        } else {
            // more complicated - need to check if it will fit in there
            // DiscordRecord last = discords.get(discords.size() - 1);
            DiscordRecord first = discords.get(0);
            if (first.getDistance() < discord.getDistance()) {
                // System.out.println(" - discord record " + discords.get(0));
                discords.remove(0);
                discords.add(discord);
            }
        }
        Collections.sort(discords);
        if (this.discords.get(0).compareTo(discord) > 0) {
            return true;
        }
        return false;
    }

    /**
     * Returns the number of the top hits.
     *
     * @param num The number of instances to return. If the number larger than
     * the storage size - returns the storage as is.
     * @return the top discord hits.
     */
    public List<DiscordRecord> getTopHits(Integer num) {
        Collections.sort(discords);
        if (num >= this.discords.size()) {
            return this.discords;
        }
        List<DiscordRecord> res = this.discords.subList(0, num - 1);
        Collections.reverse(res);
        return res;
    }

    /**
     * Get the minimal distance found among all instances in the collection.
     *
     * @return The minimal distance found among all instances in the collection.
     */
    public double getMinDistance() {
        if (this.discords.size() > 0) {
            return discords.get(0).getDistance();
        }
        return -1;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(1024);
        for (DiscordRecord r : discords) {
            sb.append(r.getDistance()).append(" at ").append(r.getPosition()).append("\n");
        }
        return sb.toString();
    }
}
