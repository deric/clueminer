package edu.hawaii.jmotif.logic.sax.trie;

import java.util.List;

/**
 * Just a container for discords/motifs.
 *
 * @author psenin
 *
 */
public class DiscordsAndMotifs {

  private final DiscordRecords discords;

  private final MotifRecords motifs;

  /**
   * Constructor.
   *
   * @param discordCollectionSize maxSize of discords collection.
   * @param motifsCollectionSize maxSize of motifs collection.
   */
  public DiscordsAndMotifs(int discordCollectionSize, int motifsCollectionSize) {
    this.discords = new DiscordRecords();
    this.discords.setMaxCapacity(discordCollectionSize);
    this.motifs = new MotifRecords();
    this.motifs.setMaxCapacity(discordCollectionSize);
  }

  /**
   * Add the motif into the storage.
   *
   * @param motifRecord The motif to add.
   */
  public void addMotif(MotifRecord motifRecord) {
    this.motifs.add(motifRecord);
  }

  /**
   * Add the discord into the storage.
   *
   * @param discordRecord The discord record.
   */
  public void addDiscord(DiscordRecord discordRecord) {
    this.discords.add(discordRecord);
  }

  /**
   * Returns the current min distance in discords - so alleviates the searching troubles.
   *
   * @return The min distance.
   */
  public double getMinDistance() {
    return this.discords.getMinDistance();
  }

  /**
   * Get the hit motifs.
   *
   * @param num The number of instances asked.
   * @return The sorted by decreasing frequency list of motifs.
   */
  public List<MotifRecord> getTopMotifs(int num) {
    return this.motifs.getTopHits(num);
  }

  /**
   * Get the list of top (most distant) discords.
   *
   * @param num The number of instances asked.
   * @return The sorted by decreasing distance list of discords.
   */
  public List<DiscordRecord> getTopDiscords(int num) {
    return this.discords.getTopHits(num);
  }

}
