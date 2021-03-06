package net.sf.mardao.test.domain;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Entity;
import net.sf.mardao.core.Parent;
import net.sf.mardao.core.domain.AbstractLongEntity;

/**
 *
 * @author os
 */
@Entity
public class DInventory extends AbstractLongEntity {
    
    @Parent(kind="DOrganization")
    private Serializable organisationKey;

    @Basic
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Serializable getOrganisationKey() {
        return organisationKey;
    }

    public void setOrganisationKey(Serializable organisationKey) {
        this.organisationKey = organisationKey;
    }
    
    
}
