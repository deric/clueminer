package org.clueminer.meta.api;

/**
 *
 * @author deric
 */
public enum MetaFlag {

    NONE, //result not matched
    MATCHED,
    HASH, //clustering hashCode match
    FINGERPRINT,
    REJECTED

}
