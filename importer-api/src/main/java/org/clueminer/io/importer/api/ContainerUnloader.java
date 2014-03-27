package org.clueminer.io.importer.api;

import org.clueminer.dataset.api.AttributeBuilder;

/**
 * ContainerUnloader is responsible for transforming pre-loaded data into real
 * data-structure
 *
 * @author Tomas Barton
 */
public interface ContainerUnloader {

    public int getInstanceCount();

    public Iterable<InstanceDraft> getInstances();

    public int getAttributeCount();

    public Iterable<AttributeDraft> getAttributes();

    public void addInstance(InstanceDraft instance);

    public String getSource();

    public AttributeBuilder getAttributeBuilder();

    public void setAttributeBuilder(AttributeBuilder builder);
}
