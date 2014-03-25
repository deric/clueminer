package org.clueminer.io.importer.api;

/**
 *
 * @author Tomas Barton
 */
public interface ContainerUnloader {

    public int getInstanceCount();

    public Iterable<InstanceDraft> getInstances();

    public int getAttributeCount();

    public Iterable<AttributeDraft> getAttributes();

    public void addInstance(InstanceDraft instance);
}
