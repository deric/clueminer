package edu.hawaii.jmotif.logic.apriori;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * Provides implementation of core methods.
 *
 * @author psenin
 *
 */
public final class AprioriFactory {

    private static final String CR = "\n";
    private static final String LOG_DATA = "                ";

    /**
     * Disable the constructor.
     */
    private AprioriFactory() {
        assert true;
    }

    /**
     * Runs the apriori over the set of transactions.
     *
     * @param transactions The original transactions list.
     * @param minSupport The minimum support value required.
     * @param log The logger to use.
     * @return The vector of all itemsets which satisfy required minimal support
     * values.
     */
    public static Vector<Itemset> runApriori(List<List<Token>> transactions, int minSupport,
            Logger log) {

        // transform original transactions
        Vector<Itemset> originalTransactions = toItemset(transactions);

        log.finer("Running Apriori on the set of " + originalTransactions.size() + " transactions:");

        logItemsetData(originalTransactions, log, "");

        // create the lowFreq hash storage used for pruning step of the algorithm
        Vector<Map<Integer, Integer>> lowFreq = new Vector<Map<Integer, Integer>>();

        // perform the first round
        Vector<Itemset> res = new Vector<Itemset>();

        // perform the first round
        Vector<Itemset> candidates = firstRound(originalTransactions, lowFreq, minSupport, log);

        // now build limitsets incrementally
        int step = 1;
        do {
            // need to generate candidates candidates, prune them
            if (1 == step) {
                candidates = secondRound(candidates, originalTransactions, lowFreq, minSupport, log);
            } else {
                candidates = nthRound(candidates, originalTransactions, lowFreq, minSupport, log);
                if (candidates.size() > 1) {
                    res.addAll(candidates);
                }
            }
            step++;
        } while (candidates.size() > 1);

        return res;
    }

    /**
     * The first round of itemset generation.
     *
     * @param originalTransactions The list collection.
     * @param lowFreq The low-frequency sequences.
     * @param minSupport The minimal required support.
     * @param log The logger to use.
     * @return The very first itemset with rank 1.
     */
    protected static Vector<Itemset> firstRound(Vector<Itemset> originalTransactions,
            Vector<Map<Integer, Integer>> lowFreq, int minSupport, Logger log) {
        log.finer("performing first round");
        // the result init
        Vector<Itemset> res = new Vector<Itemset>();
        // iterating over all transaction in the outer loop and over all transaction items in inner
        for (Itemset i : originalTransactions) {
            for (Token item : i.getItems()) {
                // create the itemset with a single item
                Itemset is = new Itemset(item);
                // find if such a thing is already in the result list
                int idx = getIndex(is, res);
                if (idx == -1) {
                    // if not - found, add it with a support equals 1
                    res.add(is);
                } else {
                    // if found - increment the support value
                    res.get(idx).incrementSupport(1);
                }
            }
        }
        // before exiting - populate the frequencies of all singletons
        Map<Integer, Integer> freqs = new TreeMap<Integer, Integer>();
        for (Itemset i : res) {
            freqs.put(i.hashCode(), i.getSupport());
        }
        lowFreq.add(0, freqs);
        // and clean up ones which have support less then minimal
        Vector<Itemset> cleanRes = new Vector<Itemset>();
        for (int i = 0; i < res.size(); i++) {
            if (res.get(i).getSupport() > minSupport) {
                cleanRes.add(res.get(i));
            }
        }

        // done deal
        logItemsetData(cleanRes, log, "");
        logLowFreqs(lowFreq, log);

        return cleanRes;
    }

    /**
     * The second round itemset generation. here we just consider all
     * permutations.
     *
     * @param candidates The first itemset (one member, freq = 1).
     * @param originalTransactions The list of original transactions.
     * @param lowFreq The map of hashes and frequencies.
     * @param log The logger to use.
     * @param minSupport The minimal required support.
     * @return The itemset with rank 2.
     */
    protected static Vector<Itemset> secondRound(Vector<Itemset> candidates,
            Vector<Itemset> originalTransactions, Vector<Map<Integer, Integer>> lowFreq, int minSupport,
            Logger log) {
        Vector<Itemset> res = new Vector<Itemset>();
        log.finer("Running second round, - getting all singletons combined");
        Map<Integer, Integer> freqs = new TreeMap<Integer, Integer>();
        // iterate over each of the elements and the rest of the list
        for (int i = 0; i < candidates.size(); i++) {
            Itemset current = candidates.get(i);
            // rest goes here
            for (int j = i + 1; j < candidates.size(); j++) {
                // new itemset generated
                Itemset newOne = new Itemset();
                newOne.addAll(current);
                newOne.addAll(candidates.get(j));
                // check if it is already in the res list
                int idx = getIndex(newOne, res);
                if (idx != -1) {
                    continue;
                }
                // if not - check if we need to add this to res list
                newOne.setSupport(0);
                for (Itemset oi : originalTransactions) {
                    if (checkContains(newOne, oi)) {
                        newOne.incrementSupport(1);
                    }
                }
                if (newOne.getSupport() >= minSupport) {
                    res.add(newOne);
                } else {
                    freqs.put(newOne.hashCode(), newOne.getSupport());
                }
            }
        }

        // now update all frequencies
        for (Itemset i : res) {
            freqs.put(i.hashCode(), i.getSupport());
        }
        lowFreq.add(1, freqs);

        // and clean up ones which have support less then minimal
        Vector<Itemset> cleanRes = new Vector<Itemset>();
        for (int i = 0; i < res.size(); i++) {
            if (res.get(i).getSupport() > minSupport) {
                cleanRes.add(res.get(i));
            }
        }

        // done deal
        logItemsetData(cleanRes, log, "");
        logLowFreqs(lowFreq, log);

        return cleanRes;
    }

    /**
     * Run N-th round of apriori.
     *
     * @param candidates The list of candidates from previous round.
     * @param originalTransactions The list of original transactions.
     * @param minSupport The minimal support value used for pruning.
     * @param lowFreq The array of hashes and frequencies.
     * @param log The logger to use.
     * @return The list of candidates after n-th round.
     */
    protected static Vector<Itemset> nthRound(Vector<Itemset> candidates,
            Vector<Itemset> originalTransactions, Vector<Map<Integer, Integer>> lowFreq, int minSupport,
            Logger log) {
        Vector<Itemset> res = new Vector<Itemset>();
        log.finer("Running N-th round");
        Map<Integer, Integer> freqs = new TreeMap<Integer, Integer>();
        // iterate over each of the elements and the rest of the list
        for (int i = 0; i < candidates.size(); i++) {
            Itemset current = candidates.get(i);
            // rest goes here
            for (int j = i + 1; j < candidates.size(); j++) {
                // fix an item
                Itemset nextCurrent = candidates.get(j);
                // new itemset generated
                if (canJoin(current, nextCurrent)) {
                    log.finer("Checking AND CAN JOIN " + current + " and " + nextCurrent);
                    // check if it's frequent
                    Itemset joint = join(current, nextCurrent);
                    //
                    // here we just check in the hash for the maximal minimal support value we can find.
                    // if it's less then the user-defined, this candidate wouldn't work
                    int freq;
                    if ((freq = checkFrequent(joint, originalTransactions, lowFreq, log)) >= minSupport) {
                        log.finer("item " + joint + " passed hash history check");
                        // at least history check indicates that this thing might be frequent
                        // so let's get the full frequency portrait
                        freq = getFrequency(joint, originalTransactions);
                        joint.setSupport(freq);
                        log.finer("determined support for " + joint.toString() + ": " + freq);
                        if (!res.contains(joint)) {
                            res.add(joint);
                        }
                    } else {
                        log.finer("Item " + joint + " excluded from frequen by the hash history check");
                    }
                } else {
                    log.finer("Checking AND CAN NOT JOIN " + current + " and " + nextCurrent);
                }
            }
        }

        if (!res.isEmpty()) {
            // now update all frequencies
            for (Itemset i : res) {
                freqs.put(i.hashCode(), i.getSupport());
            }
            lowFreq.add(res.get(0).getItems().size() - 1, freqs);
        }
        // done deal
        logItemsetData(res, log, "");
        logLowFreqs(lowFreq, log);

        return res;
    }

    /**
     * Compute the support of the itemset.
     *
     * @param joint The itemset.
     * @param originalTransactions The set of transactions.
     * @return The support of the itemset.
     */
    protected static int getFrequency(Itemset joint, Vector<Itemset> originalTransactions) {
        int freq = 0;
        for (Itemset i : originalTransactions) {
            if (checkContains(joint, i)) {
                freq += 1;
            }
        }
        return freq;
    }

    /**
     * Compute the frequency of the itemset. This task is a bit optimized since
     * it uses hashes of infrequent itemsets.
     *
     * @param joint The itemset under the frequency computation.
     * @param originalTransactions The database of original transactions.
     * @param lowFreq The database of all frequencies.
     * @param log The logger to use.
     * @return Computed support value.
     */
    protected static int checkFrequent(Itemset joint, Vector<Itemset> originalTransactions,
            Vector<Map<Integer, Integer>> lowFreq, Logger log) {
        log.finer("running check frequent, checking " + joint.toString());
        int lowestFreq = Integer.MAX_VALUE;
        //
        // candLen is the counter of itemset length
        for (int candLen = 1; candLen <= lowFreq.size(); candLen++) {
            // all sub-itemsets of the frequent itemset should be frequent, here we are figuring out
            // minimal frequency through hash
            //
            // candPos is the position of the candidate
            for (int candPos = 0; candPos < joint.getItems().size() - candLen + 1; candPos++) {
                Itemset check = new Itemset();
                for (int iOffset = 0; iOffset < candLen; iOffset++) {
                    check.addItem(joint.getItems().get(candPos + iOffset));
                }
                //
                // now need to check the frequency of this hash
                Integer hash = check.hashCode();
                Integer hashFreq;
                if ((hashFreq = lowFreq.get(candLen - 1).get(hash)) != null) {
                    log.finer("    checking " + check.toString() + " SUPPORT: " + hashFreq);
                    if (hashFreq < lowestFreq) {
                        lowestFreq = hashFreq;
                    }
                } else {
                    log.finer("    checking " + check.toString() + ", NOT FOUND in the hash");
                }
            }
        }
        log.finer("minimal support found " + lowestFreq);
        return lowestFreq;
    }

    /**
     * Join two itemsets. By the canonical definition of the "join" we can only
     * join itemsets that are differ by the last items producing k+1 items
     * itemsets adding that different item to one of the two sets.
     *
     * @param current The first itemset.
     * @param nextCurrent Next itemset.
     * @return the joint itemset.
     */
    protected static Itemset join(Itemset current, Itemset nextCurrent) {
        Itemset res = new Itemset(current.getItems());
        res.addItem(nextCurrent.getItems().lastElement());
        res.setSupport(0);
        return res;
    }

    /**
     * Check whether or not it is possible to join itemsets. By the canonical
     * definition of the "join" we can only join itemsets that are differ by the
     * last items producing k+1 items itemsets adding that different item to one
     * of the two sets.
     *
     * @param current The first itemset.
     * @param nextCurrent Next itemset.
     * @return True if able to join.
     */
    protected static boolean canJoin(Itemset current, Itemset nextCurrent) {
        for (int i = 0; i < current.getItems().size() - 1; i++) {
            if (!current.getItems().get(i).equals(nextCurrent.getItems().get(i))) {
                return false;
            }
        }
        if (current.getItems().lastElement().compareTo(nextCurrent.getItems().lastElement()) < 0) {
            return true;
        }
        return false;
    }

    /**
     * Check is items from the itemset are contained in the other one.
     *
     * @param query The query itemset.
     * @param reference The reference
     * @return True if found.
     */
    protected static boolean checkContains(Itemset query, Itemset reference) {
        Vector<Token> refItems = reference.getItems();
        for (Token item : query.getItems()) {
            if (!refItems.contains(item)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get the itemset' position.
     *
     * @param itemset The itemset.
     * @param collection The itemsets collection.
     * @return The itemset position.
     */
    protected static int getIndex(Itemset itemset, Vector<Itemset> collection) {
        int idx = 0;
        boolean found = false;
        for (idx = 0; idx < collection.size(); idx++) {
            if (itemset.equals(collection.get(idx))) {
                found = true;
                break;
            }
        }
        if (found) {
            return idx;
        }
        return -1;
    }

    /**
     * Convert the list of transactions into itemset.
     *
     * @param transactions The data.
     * @return The vector of itemset.
     */
    protected static Vector<Itemset> toItemset(List<List<Token>> transactions) {
        Vector<Itemset> res = new Vector<Itemset>();
        for (List<Token> l : transactions) {
            Itemset is = new Itemset(l);
            res.add(is);
        }
        return res;
    }

    /**
     * Adds convenience for logging.
     *
     * @param transactions The set of transactions to print out.
     * @param log The logger to use, if null - prints into stdout.
     * @param ident The ident string to use when logging.
     */
    public static void logItemsetData(Vector<Itemset> transactions, Logger log, String ident) {
        StringBuffer sb = new StringBuffer(5000);
        int counter = 0;
        for (Itemset i : transactions) {
            if (counter > 0) {
                sb.append(LOG_DATA);
            }
            sb.append(ident + i.toString() + CR);
            counter++;
        }
        if ((sb.length() > 0) && (sb.lastIndexOf(CR) == sb.length() - 1)) {
            sb.delete(sb.length() - 1, sb.length());
        }
        if (null == log) {
            System.out.println(sb.toString());
        } else {
            log.finer(sb.toString());
        }
    }

    /**
     * Log helper for keeping the track of a low-frequent itemset.
     *
     * @param lowFreq Vector of itemsets.
     * @param log The logger.
     */
    private static void logLowFreqs(Vector<Map<Integer, Integer>> lowFreq, Logger log) {
        StringBuffer sb = new StringBuffer(2000);
        sb.append("Frequency hashes..." + CR);
        for (int i = 0; i < lowFreq.size(); i++) {
            sb.append(LOG_DATA + "Iteration " + i + CR);
            for (Entry<Integer, Integer> e : lowFreq.get(i).entrySet()) {
                sb.append(LOG_DATA + " hash: " + e.getKey() + ", freq: " + e.getValue() + CR);
            }
        }
        if (sb.lastIndexOf(CR) == sb.length() - 1) {
            sb.delete(sb.length() - 1, sb.length());
        }
        log.finer(sb.toString());
    }
}
