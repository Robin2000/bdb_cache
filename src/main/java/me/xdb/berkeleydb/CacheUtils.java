package me.xdb.berkeleydb;

public final class CacheUtils {
	
	public static Object get(String key) {
		CacheData cache = BerkeleyDB.getInstance().get(key);
		if(cache != null) {
			return cache.getData();
		}
		return null;
	}
	
	public static void put(String key,Object data) {
		if(data == null) {
			return;
		}
		BerkeleyDB.getInstance().put(key, new CacheData(data));
	}
}
