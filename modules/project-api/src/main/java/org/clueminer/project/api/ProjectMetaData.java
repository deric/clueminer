package org.clueminer.project.api;

/**
 *
 * @author Tomas Barton
 */
public interface ProjectMetaData {

    public String getKeywords();

    public String getAuthor();

    public String getDescription();

    public String getTitle();

    public void setAuthor(String author);

    public void setDescription(String description);

    public void setKeywords(String keywords);

    public void setTitle(String title);
}