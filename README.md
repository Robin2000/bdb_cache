# bdb_cache | Java本地缓存
优点
====
* 继承BerkeleyDB的优势：轻量、可靠、高性能、嵌入式。
* 依赖少：仅仅依赖 BerkeleyDB。
* 损耗小：单例实现，无并发性能损耗。
* 易用：自动将缓存对象序列化，自动判断过期。

用法
====
```
CacheUtils.put(String key,Object value)
Object value=CacheUtils.get(String key)

特别说明
====
如果download.oracle.com无法连接，请执行lib目录下的install即可。