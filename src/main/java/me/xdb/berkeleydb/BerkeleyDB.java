package me.xdb.berkeleydb;

import com.sleepycat.je.*;
import java.util.logging.Level;  
import java.util.logging.Logger; 

import java.io.File;

public class BerkeleyDB {
	
    private static final Logger logger = Logger.getLogger(BerkeleyDB.class.toString()); 
    
    private static final Durability DEFAULT_DURABILITY =
            new Durability(Durability.SyncPolicy.NO_SYNC,
                    null,    // unused by non-HA applications.
                    null);   // unused by non-HA applications.

//    Durability newDurability =
//            new Durability(Durability.SyncPolicy.WRITE_NO_SYNC,
//                    null,    // unused by non-HA applications.
//                    null);   // unused by non-HA applications. 
    
    private static BerkeleyDB berkeleyDB = null;
    
    //关键参数，将BerkeleyDB作为内存缓存工具，还是磁盘读写工具
    private boolean canUseMemory = false;

    private Environment myDbEnvironment;
    private Database myDatabase;
    String appID = "cacheApp";
    private String cacheDir = "/xdb/cache";
    private String cacheFile = "data";

    private BerkeleyDB() {

    }

    public static BerkeleyDB getInstance() {

        if (berkeleyDB == null) {
            synchronized (logger) {
                if (berkeleyDB != null) {
                    return berkeleyDB;
                }

                berkeleyDB = new BerkeleyDB();
                berkeleyDB.init();
            }
        }
        return berkeleyDB;
    }

    public void put(String key, CacheData value) {

        try {
            if (value.getData() == null)
                return;

            myDatabase.put(null, new DatabaseEntry(key.getBytes()), value.toDatabaseEntry());
        } catch (Exception e) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.warning("BerkeleyDB put error!");
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        try {
            DatabaseEntry theKey = new DatabaseEntry(key.getBytes());
            DatabaseEntry theData = new DatabaseEntry();

            if (myDatabase.get(null, theKey, theData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {

                byte[] retData = theData.getData();
                CacheData cacheData = SerializeUtils.unserialize(retData);
                if (cacheData.isExpired()) {
                    //删除旧数据
                    delete(key);
                    return null;
                }

                return (T) cacheData;
            }

        } catch (Exception e) {
        	if (logger.isLoggable(Level.WARNING)) {
                logger.warning("BerkeleyDB get error!");
            }
        }

        return null;
    }

    public int delete(String key) {

        if (key == null) {
            return 0;
        }
        DatabaseEntry theKey = new DatabaseEntry(key.getBytes());
        try {
            if (myDatabase.delete(null, theKey) == OperationStatus.SUCCESS) {
                return 1;
            }
        } catch (DatabaseException e) {
        	if (logger.isLoggable(Level.WARNING)) {
                logger.warning("BerkeleyDB delete error!");
            }
        }

        return 0;
    }


    private void init() {
        if (myDatabase != null) {
            return;
        }
    	if (logger.isLoggable(Level.INFO)) {
            logger.info("BerkeleyDB begin load!");
        }

        initDatabase();
        clearOldData();

    	if (logger.isLoggable(Level.INFO)) {
            logger.info("BerkeleyDB finish load!");
        }
    }

    private void initDatabase() {
        try {
            if (System.getProperty("XDB_CACHE_ID") != null) {
                this.appID = System.getProperty("XDB_CACHE_ID");
            }

            if (System.getProperty("XDB_CACHE_DIR") != null) {
                this.cacheDir = System.getProperty("XDB_CACHE_DIR");
            }

            if (System.getProperty("XDB_CACHE_FILE") != null) {
                this.appID = System.getProperty("XDB_CACHE_FILE");
            }

            String dirStr = this.cacheDir + "/" + this.appID;

            EnvironmentConfig envConfig = new EnvironmentConfig();
            envConfig.setDurability(DEFAULT_DURABILITY);
            envConfig.setTransactional(false);
            envConfig.setAllowCreate(true);
            File dir = new File(dirStr);
            if (!dir.exists()) {
                dir.mkdirs();
            } else {
                for (File f : dir.listFiles()) {	//清空上次缓存的数据
                    f.delete();
                }
            }

            myDbEnvironment = new Environment(dir, envConfig);

            DatabaseConfig dbConfig = new DatabaseConfig();
            dbConfig.setAllowCreate(true);
            dbConfig.setTemporary(canUseMemory);
            dbConfig.setTransactional(false);
            //dbConfig.setDeferredWrite(true);
            myDatabase = myDbEnvironment.openDatabase(null, cacheFile, dbConfig);

        } catch (DatabaseException dbe) {
        	if (logger.isLoggable(Level.SEVERE)) {
                logger.severe("BerkeleyDB数据库初始化异常");               
            }
        }
    }

    private void clearOldData() {

    }

    public boolean isCanUseMemory() {
        return canUseMemory;
    }

    public void setCanUseMemory(boolean canUseMemory) {
        this.canUseMemory = canUseMemory;
    }
}
