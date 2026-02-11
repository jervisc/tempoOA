## Code Review

You are reviewing the following code submitted as part of a task to implement an item cache in a highly concurrent application. The anticipated load includes: thousands of reads per second, hundreds of writes per second, tens of concurrent threads.
Your objective is to identify and explain the issues in the implementation that must be addressed before deploying the code to production. Please provide a clear explanation of each issue and its potential impact on production behaviour.

```java
import java.util.concurrent.ConcurrentHashMap;

public class SimpleCache<K, V> {
    private final ConcurrentHashMap<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();
    private final long ttlMs = 60000; // 1 minute

    public static class CacheEntry<V> {
        private final V value;
        private final long timestamp;

        public CacheEntry(V value, long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }

        public V getValue() {
            return value;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
	// Reduce GC Risk: Avoid 'new CacheEntry' on every write. 
	// Since we are having hundreds writes/sec, it could be same value is already exist,
	// SUGGESTION: now we should only update the value instead of create a new CacheEntry
    public void put(K key, V value) {
        cache.put(key, new CacheEntry<>(value, System.currentTimeMillis()));
    }


 	// Memory Leak Risk
	// There is no active eviction policy or capacity limit on when we get a cache. 
	// Right now at hundreds of writes/sec, expired entries will never be removed unless re-accessed,
	// otherwise it is leading to OutOfMemoryError very soon.
	// SUGGESTION: Implement an remove expired key or use a bounded cache.
	
	
	// Non-atomic "Check-then-Act" execution
	// The current 'get' method only returns null on miss or expiry,
	// which forces the caller to handle the data loading logic outside the cache class.
	// Under high load, if a hot key expires, multiple threads will hit the database simultaneously, 
	// and potentially crashing the backend service.
	// SUGGESTION: Use 'computeIfAbsent' with a loader function to ensure only one thread 
	// loads the data while others wait.
	
    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry != null) {
            if (System.currentTimeMillis() - entry.getTimestamp() < ttlMs) {
                return entry.getValue();
            }
        }
        return null;
    }

    public int size() {
        return cache.size();
    }
}
```
