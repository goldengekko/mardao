package net.sf.mardao.api.test;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import net.sf.mardao.api.domain.DLongEntity;

/**
 *
 * @author os
 */
@Entity
public class Book extends DLongEntity {
    @Id
    private Long id;
    
    @Basic
    private String title;
    
    @Override
    public Long getSimpleKey() {
        return id;
    }

    public void setSimpleKey(Long simpleKey) {
        this.id = simpleKey;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}