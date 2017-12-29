package me.xdb.berkeleydb;

import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.persist.model.Entity;

@Entity
public class CacheData implements java.io.Serializable {

    private static final long serialVersionUID = 1L;
    private long cacheTime;
    private Object data;

    public CacheData(Object value) {
        this.data = value;
        this.cacheTime = System.currentTimeMillis();
    }

    public byte[] toBytes() {
        return SerializeUtils.serialize(this);
    }

    public DatabaseEntry toDatabaseEntry() {
        return new DatabaseEntry(toBytes());
    }

    public long getCacheTime() {
        return cacheTime;
    }

    public void setCacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
    }

    @SuppressWarnings("unchecked")
    public <T> T getData() {
        return (T) data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    /*子类实现该方法的正确处理*/
    public boolean isExpired() {
        return false;
    }
}
