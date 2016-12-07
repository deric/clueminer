package org.clueminer.project.impl;

import java.io.Serializable;
import org.clueminer.project.api.ProjectMetaData;

/**
 *
 * @author Tomas Barton
 */
public class ProjectMetaDataImpl implements ProjectMetaData, Serializable {

    private static final long serialVersionUID = -1772837206862687790L;
    private String author;
    private String title = "";
    private String keywords = "";
    private String description = "";

    public ProjectMetaDataImpl() {
        String username = System.getProperty("user.name");
        if (username != null) {
            author = username;
        }
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getKeywords() {
        return keywords;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }
}