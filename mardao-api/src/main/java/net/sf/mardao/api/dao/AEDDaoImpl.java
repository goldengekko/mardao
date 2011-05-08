package net.sf.mardao.api.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Text;

public abstract class AEDDaoImpl<T, ID extends Serializable> implements Dao<T, ID> {

    /** Using slf4j logging */
    protected final Logger   LOGGER = LoggerFactory.getLogger(getClass());

    /** mostly for logging */
    protected final Class<T> persistentClass;

    protected AEDDaoImpl(Class<T> type) {
        this.persistentClass = type;
    }

    /**
     * Converts a datastore Entity into the domain object. Implemented in Generated<T>DaoImpl
     * 
     * @param entity
     *            the datastore Entity
     * @return a domain object
     */
    protected abstract T convert(Entity entity);

    /**
     * Converts a datastore Key into the domain primary key. Implemented in Generated<T>DaoImpl
     * 
     * @param key
     *            the datastore Key
     * @return a domain primary key
     */
    protected abstract ID convert(Key key);

    protected abstract List<ID> convert(List<Key> keys);

    protected String convert(Object value) {
        if (null == value) {
            return null;
        }
        if (value instanceof Text) {
            final Text text = (Text) value;
            return text.getValue();
        }
        return (String) value;
    }

    protected abstract Key createKey(T entity);

    protected abstract Key createKey(ID primaryKey);

    public abstract Key createKey(Key parentKey, ID primaryKey);

    protected abstract List<Key> createKeys(Key parentKey, List<ID> primaryKeys);

    protected List<Key> createKeys(List<ID> primaryKeys) {
        return createKeys(null, primaryKeys);
    }

    protected abstract String getTableName();

    protected abstract String getPrimaryKeyColumnName();

    protected abstract List<String> getColumnNames();

    protected Entity createEntity(ID primaryKey) {
        if (null != primaryKey) {
            return new Entity(createKey(primaryKey));
        }
        return new Entity(getTableName());
    }

    protected Entity createEntity(Key parentKey, ID primaryKey) {
        if (null != primaryKey) {
            return new Entity(createKey(parentKey, primaryKey));
        }
        return new Entity(getTableName(), parentKey);
    }

    protected Entity createEntity(Map<String, Object> nameValuePairs) {
        @SuppressWarnings("unchecked")
        ID primaryKey = (ID) nameValuePairs.get(getPrimaryKeyColumnName());
        final Entity entity = createEntity(primaryKey);
        populate(entity, nameValuePairs);
        return entity;
    }

    protected abstract void populate(Entity entity, Map<String, Object> nameValuePairs);

    protected static void populate(Entity entity, String name, Object value) {
        if (null != value) {
            // String properties must be 500 characters or less.
            // Instead, use com.google.appengine.api.datastore.Text, which can store strings of any length.
            if (value instanceof String) {
                final String s = (String) value;
                if (500 < s.length()) {
                    value = new Text(s);
                }
            }
        }
        entity.setProperty(name, value);
    }

    protected DatastoreService getDatastoreService() {
        return DatastoreServiceFactory.getDatastoreService();
    }

    protected List<T> asIterable(PreparedQuery pq, int limit, int offset) {
        final List<T> returnValue = new ArrayList<T>();

        FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();

        if (0 < limit) {
            fetchOptions.limit(limit);
        }

        if (0 < offset) {
            fetchOptions.offset(offset);
        }

        T domain;
        for(Entity entity : pq.asIterable(fetchOptions)) {
            domain = convert(entity);
            returnValue.add(domain);
            // LOGGER.debug("  entity {} -> domain {}", entity, domain);
        }
        return returnValue;
    }

    protected T asSingleEntity(PreparedQuery pq) {
        final Entity entity = pq.asSingleEntity();
        final T domain = convert(entity);
        return domain;
    }

    protected List<ID> asKeys(PreparedQuery pq, int limit, int offset) {
        final List<ID> returnValue = new ArrayList<ID>();

        FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();

        if (0 < limit) {
            fetchOptions.limit(limit);
        }

        if (0 < offset) {
            fetchOptions.offset(offset);
        }

        ID key;
        for(Entity entity : pq.asIterable(fetchOptions)) {
            key = convert(entity.getKey());
            returnValue.add(key);
        }

        return returnValue;
    }

    protected List<Key> asCoreKeys(PreparedQuery pq, int limit, int offset) {
        final List<Key> returnValue = new ArrayList<Key>();

        FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();

        if (0 < limit) {
            fetchOptions.limit(limit);
        }

        if (0 < offset) {
            fetchOptions.offset(offset);
        }

        ID key;
        for(Entity entity : pq.asIterable(fetchOptions)) {
            returnValue.add(entity.getKey());
        }

        return returnValue;
    }

    /**
     * @param ascending
     * @param orderBy
     * @param filters
     * 
     */
    protected PreparedQuery prepare(Map<String, Object> filters, String orderBy, boolean direction) {
        return prepare(filters, orderBy, direction, null, false);
    }

    /**
     * @param direction
     * @param orderBy
     * @param filters
     * 
     */
    protected PreparedQuery prepare(Map<String, Object> filters, String orderBy, boolean direction, String secondaryOrderBy,
            boolean secondaryDirection) {
        LOGGER.debug("prepare {} filters {}", getTableName(), filters);
        final DatastoreService datastore = getDatastoreService();

        Query q = new Query(getTableName());

        // filter query:
        if (null != filters) {
            for(Entry<String, Object> filter : filters.entrySet()) {
                q.addFilter(filter.getKey(), FilterOperator.EQUAL, filter.getValue());
            }
        }

        // sort query?
        if (null != orderBy) {
            q.addSort(orderBy, direction ? SortDirection.ASCENDING : SortDirection.DESCENDING);

            // secondary sort order?
            if (null != secondaryOrderBy) {
                q.addSort(secondaryOrderBy, secondaryDirection ? SortDirection.ASCENDING : SortDirection.DESCENDING);
            }
        }

        return datastore.prepare(q);
    }

    /**
     * @param ascending
     * @param orderBy
     * @param filters
     * 
     */
    protected PreparedQuery prepare(boolean keysOnly, String orderBy, boolean ascending, Expression... filters) {
        return prepare(keysOnly, null, null, orderBy, ascending, filters);
    }

    /**
     * 
     * @param keysOnly
     * @param parentKey
     * @param orderBy
     * @param ascending
     * @param filters
     * @return
     */
    protected PreparedQuery prepare(boolean keysOnly, Key parentKey, Key simpleKey, String orderBy, boolean ascending,
            Expression... filters) {
        LOGGER.debug("findUnique {} with filters {}", getTableName(), filters);
        final DatastoreService datastore = getDatastoreService();

        Query q = new Query(getTableName(), parentKey);

        // keys only?
        if (keysOnly) {
            q.setKeysOnly();
        }

        // filter on keyName:
        if (null != simpleKey) {
            q.addFilter(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, simpleKey);
        }

        // filter query:
        if (null != filters) {
            for(Expression expression : filters) {
                q.addFilter(expression.getColumn(), (FilterOperator) expression.getOperation(), expression.getOperand());
            }
        }

        // sort query?
        if (null != orderBy) {
            q.addSort(orderBy, ascending ? SortDirection.ASCENDING : SortDirection.DESCENDING);
        }

        return datastore.prepare(q);
    }

    /**
     * @param ascending
     * @param orderBy
     * @param filters
     * 
     */
    protected PreparedQuery prepare(String orderBy, boolean ascending, Expression... filters) {
        return prepare(false, orderBy, ascending, filters);
    }

    /**
     * @param ascending
     * @param orderBy
     * @param filters
     * 
     */
    protected PreparedQuery prepareKeys(String orderBy, boolean ascending, Expression... filters) {
        return prepare(true, orderBy, ascending, filters);
    }

    public T findByPrimaryKey(ID primaryKey) {
        return findByPrimaryKey(null, primaryKey);
    }

    public Map<ID, T> findByPrimaryKeys(Iterable<ID> primaryKeys) {
        return findByPrimaryKeys(null, primaryKeys);
    }

    public T findByPrimaryKey(Object parentKey, ID primaryKey) {
        T domain = null;
        final Key key = createKey((Key) parentKey, primaryKey);
        final DatastoreService datastore = getDatastoreService();
        try {
            final Entity entity = datastore.get(key);
            domain = convert(entity);
        }
        catch (EntityNotFoundException ignore) {
        }
        LOGGER.debug("{} -> {}", key.toString(), domain);

        return domain;
    }

    public Map<ID, T> findByPrimaryKeys(Object parentKey, Iterable<ID> primaryKeys) {
        final Map<ID, T> returnValue = new TreeMap<ID, T>();
        final List<Key> keys = new ArrayList<Key>();
        Key key;
        for(ID id : primaryKeys) {
            key = createKey((Key) parentKey, id);
            keys.add(key);
        }
        final DatastoreService datastore = getDatastoreService();
        final Map<Key, Entity> entities = datastore.get(keys);
        T domain;
        ID id;
        for(Entry<Key, Entity> entry : entities.entrySet()) {
            id = convert(entry.getKey());
            domain = convert(entry.getValue());
            returnValue.put(id, domain);
        }
        return returnValue;
    }

    public List<ID> findAllKeys() {
        PreparedQuery pq = prepare(true, Entity.KEY_RESERVED_PROPERTY, true);
        return asKeys(pq, -1, 0);
    }

    protected Key persist(Entity entity) {
        final DatastoreService datastore = getDatastoreService();

        return datastore.put(entity);
    }

    public void update(T domain) {
        persist(domain);
    }

    public void delete(T domain) {
        final Key key = createKey(domain);
        delete(key);
    }

    public void delete(Key primaryKey) {
        final DatastoreService datastore = getDatastoreService();
        datastore.delete(primaryKey);
    }

    public void deleteByCore(List<Key> primaryKeys) {
        final DatastoreService datastore = getDatastoreService();
        datastore.delete(primaryKeys);
    }

    public void deleteByKeys(List<ID> primaryKeys) {
        deleteByCore(createKeys(primaryKeys));
    }

    // public void delete(List<ID> primaryKeys) {
    // delete(null, primaryKeys);
    // }
    //

    public List<T> findAll() {
        LOGGER.debug(persistentClass.getSimpleName());
        return findBy(null, false, -1, 0);
    }

    protected T findUniqueBy(String fieldName, Object param) {
        PreparedQuery pq = prepare(null, false, new Expression(fieldName, Query.FilterOperator.EQUAL, param));
        return asSingleEntity(pq);
    }

    protected List<T> findBy(String fieldName, Object param) {
        return findBy(null, false, -1, 0, new Expression(fieldName, Query.FilterOperator.EQUAL, param));
    }

    protected List<T> findByKey(String fieldName, Class foreignClass, Object key) {
        throw new UnsupportedOperationException("Not yet implemented for AED");
    }

    protected T findBy(Map<String, Object> args) {
        PreparedQuery pq = prepare(args, null, false);
        return asSingleEntity(pq);
    }

    protected T findBy(Expression... filters) {
        PreparedQuery pq = prepare(null, false, filters);
        return asSingleEntity(pq);
    }

    protected List<T> findBy(Map<String, Object> args, String orderBy, boolean ascending) {
        return findBy(args, orderBy, ascending, -1, 0);
    }

    protected List<T> findBy(Map<String, Object> args, String orderBy, boolean ascending, String secondaryOrderBy,
            boolean secondaryDirection) {
        return findBy(args, orderBy, ascending, secondaryOrderBy, secondaryDirection, -1, 0);
    }

    protected List<T> findBy(Map<String, Object> filters, String orderBy, boolean ascending, int limit, int offset) {
        PreparedQuery pq = prepare(filters, orderBy, ascending);
        return asIterable(pq, limit, offset);
    }

    protected List<T> findBy(Map<String, Object> filters, String primaryOrderBy, boolean primaryDirection,
            String secondaryOrderBy, boolean secondaryDirection, int limit, int offset) {
        PreparedQuery pq = prepare(filters, primaryOrderBy, primaryDirection, secondaryOrderBy, secondaryDirection);
        return asIterable(pq, limit, offset);
    }

    protected List<T> findBy(String orderBy, boolean ascending, int limit, int offset, Expression... filters) {
        PreparedQuery pq = prepare(orderBy, ascending, filters);
        return asIterable(pq, limit, offset);
    }

    protected List<ID> findKeysBy(String orderBy, boolean ascending, int limit, int offset, Expression... filters) {
        PreparedQuery pq = prepareKeys(orderBy, ascending, filters);
        return asKeys(pq, limit, offset);
    }

    protected List<T> findBy(String orderBy, boolean ascending, int limit, Expression... args) {
        return findBy(orderBy, ascending, limit, 0, args);
    }

    public List<T> findByManyToMany(String primaryKeyName, String fieldName, String foreignSimpleClass, String foreignFieldName,
            Object foreignId) {
        throw new UnsupportedOperationException("Not yet implemented for AED");
    }

    protected List<ID> findKeysBy(String fieldName, Object param) {
        return findKeysBy(null, false, -1, 0, new Expression(fieldName, Query.FilterOperator.EQUAL, param));
    }

    public int deleteAll() {
        PreparedQuery pq = prepareKeys(null, false);
        List<Key> keys = asCoreKeys(pq, -1, 0);
        deleteByCore(keys);
        return keys.size();
    }

    public T persist(Map<String, Object> nameValuePairs) {
        final Entity entity = createEntity(nameValuePairs);
        persist(entity);
        return convert(entity);
    }

    public int update(Map<String, Object> values, Expression... expressions) {
        throw new UnsupportedOperationException("Not yet implemented for AED");
    }

}
