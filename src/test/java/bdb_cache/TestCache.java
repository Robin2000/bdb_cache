package bdb_cache;

import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import me.xdb.berkeleydb.BerkeleyDB;
import me.xdb.berkeleydb.CacheData;

public class TestCache {

	private BerkeleyDB xdb;
	
	@Test
	public void testPutSet() {
		
		String key ="abc";
		
		xdb.put(key, new CacheData(123));
		
		CacheData result=xdb.get(key);
		
		assertTrue("未从缓存取到数据", result!=null);
		
		System.out.println("从缓存取得:" + result.getData());
		
		System.out.println("缓存时间:" + new Date(result.getCacheTime()));
	}
	
	@Before
	public void setUp() {
		xdb=BerkeleyDB.getInstance();
	}
}
