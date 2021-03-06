package net.sf.mardao.api.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract implementation of GenericDao interface methods.
 *
 * Generated on 2011-01-22T21:37:01.884+0700.
 * @author mardao DAO generator (net.sf.mardao.plugin.GenerateSourcesMojo)
 */
public abstract class AEDDaoWrapper<T, ID extends Serializable> 
	implements GenericDao<T, ID> 
{
	/** Using slf4j logging */
   protected final Logger LOG = LoggerFactory.getLogger(getClass());
	
	/** The specific helper reference */
	protected AbstractHelper<T, ID> genericDao;
	
	/**
	 * Setter for the AbstractHelper attribute
	 */
	public void setHelper(AbstractHelper<T, ID> genericDao) {
		this.genericDao = genericDao;
	}

	// ----------------------- generic implements -------------------------
	
	/**
	 * find-by method for unique attributes
	 * @param args the specified attribute name-value map
	 * @param orderBy the attribute to order by
	 * @param ascending true if ascending
	 * @return the matching entities for the specified attributes
	 */
	protected List<T> findBy(Map<String,Object> args, String orderBy, boolean ascending) {
		return genericDao.findBy(args, orderBy, ascending);
	}
	
	/**
	 * find-by method for unique attributes
	 * @param args the specified attribute name-value map
	 * @return the unique entity for the specified attributes
	 */
	protected T findBy(Map<String,Object> args) {
		LOG.debug("findBy({})", args);
		return genericDao.findBy(args);
	}

	
	/**
	 * @param limit set to -1 for no LIMIT clause
	 */
    protected List<T> findBy(String orderBy, boolean ascending, int limit, Expression... expressions) {
        return genericDao.findBy(orderBy, ascending, limit, expressions);
    }

	protected T populate(String uniqueFieldName, Object value, Map<String, Object> nameValuePairs) {
		T entity = genericDao.findUniqueBy(uniqueFieldName, value);
		if (null == entity) {
			// make sure unique value exists in map:
			nameValuePairs.put(uniqueFieldName, value);
			
			// now, persist using Map:
			entity = genericDao.persist(nameValuePairs);
		}
		return entity;
	}

	/**
	 * For updating many rows in one go
	 */
	protected int update(Map<String, Object> values, Expression... expressions) {
		return genericDao.update(values, expressions); 
	}	
	
	protected String getTableName() {
		return genericDao.getTableName();
	}
	
	String getPrimaryKeyColumnName() {
		return genericDao.getPrimaryKeyColumnName();
	}
	
	List<String> getColumnNames() {
		return genericDao.getColumnNames();
	}
	
	// ----------------------- implements GenericDao -------------------------
	
	public T findByPrimaryKey(ID id) {
		LOG.debug("findByPrimaryKey({})", id);
		return genericDao.findByPrimaryKey(id);
	}
	
	public void persist(T entity) {
		LOG.debug("persist({})", entity);
		genericDao.persist(entity);
	}
	
	public void update(T entity) {
		LOG.debug("update({})", entity);
		genericDao.update(entity);
	}
	
	public void delete(T entity) {
		LOG.debug("delete({})", entity);
		genericDao.delete(entity);
	}
	
	public List<T> findAll() {
		LOG.debug("findAll()");
		return genericDao.findAll();
	}
	
}
