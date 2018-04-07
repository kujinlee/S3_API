package com.kyndi.documents;

import java.io.InputStream;

public interface DocumentCache {

  /**
   * Retrieves an item from the cache. If the item does not exist in the cache, the
   * {@see CacheResult} will contain a {@code MISS} {@see CacheOperation}
   *
   * @param id to fetch
   * @return result
   */
  CacheResult getItem(String id);

  /**
   * Store an item in the cache.
   *
   * @param id to use as cache key
   * @param item to store
   * @return result
   */
  CacheResult putItem(String id, InputStream item);

  /**
   * Removes an item from the cache
   *
   * @param id to remove
   * @return result
   */
  CacheResult evict(String id);

  /**
   * Remove all items from the cache
   *
   * @return result
   */
  CacheResult evictAll();

  /**
   * Fetch the current size of the cache in bytes
   *
   * @return size in bytes
   */
  long cacheSizeBytes();

}
