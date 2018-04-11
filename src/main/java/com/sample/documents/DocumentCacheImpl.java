package com.sample.documents;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import javax.annotation.PostConstruct;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DocumentCacheImpl implements DocumentCache {
	
	private final Logger logger = LoggerFactory.getLogger(DocumentCacheImpl.class);
	
	private Map<String, File> documentCache;
	private Queue<String> fileQueue; // to keep file ids in inserted order
	private long totalBytes; 

	private long maxCacheSizeBytes;
	
	@PostConstruct
	private void setUpDocumentCache() {
		documentCache = new HashMap<String, File>(); // alternatively this could be LinkedHashMap for FIFO
		totalBytes = 0L;
		fileQueue = new LinkedList<String>();
	}
	
	@Value("${max-cache-size-bytes}")
	public void setMaxCacheSizeBytes(long maxCacheSizeBytes) {
		this.maxCacheSizeBytes = maxCacheSizeBytes;
	}

	@Override
	public CacheResult getItem(String id) {
		File file;
		CacheResult cacheResult;
		if (documentCache.containsKey(id)) {
			file = documentCache.get(id);
			cacheResult = new CacheResult.Builder(id).hit(file).build();
		}  else {
			cacheResult = new CacheResult.Builder(id).miss().build();
		}
		
		return cacheResult;
	}

	@Override
	public CacheResult putItem(String id, InputStream item) {
		File file = new File(id);
		OutputStream fos;
		try {
			fos = new FileOutputStream(file);
			IOUtils.copy(item, fos); // copy from item to fos
			fos.close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		// check if file size is bigger than maxCacheSizeBytes 
		// or other files need to be evicted to make room for the file
		boolean cacheSuccess = false;
		if (file.length() < maxCacheSizeBytes) {
			while (file.length()+totalBytes >= maxCacheSizeBytes ) {
				// evicts enough files to make room for inputItem
				String removedId = fileQueue.poll();
				if (removedId != null) {
					evict(removedId);
				}
			}
			documentCache.put(id, file);
			totalBytes += file.length();
			cacheSuccess = true;
		} 

		CacheResult cacheResult;
		if (cacheSuccess) {
			cacheResult = new CacheResult.Builder(id).put(file).build();
		} else {
			Throwable error = new Throwable("File is too big to cache: file id:"+id);
			cacheResult = new CacheResult.Builder(id).fail(error).build();
		}
		return cacheResult;
	}

	/**
	 * item supposed to be evicted in FIFO
	 * TODO: decide what to do if the file specified with id is not the oldest one
	 * If that can be allowed, fileQueue need to remove the file id other than from queue head
	 * 
	 */
	@Override
	public CacheResult evict(String id) {
		File file = documentCache.remove(id);
		CacheResult cacheResult;
		if (file != null) {
			cacheResult = new CacheResult.Builder(id).evict().build();
			totalBytes -= file.length();
		} else {
			cacheResult = new CacheResult.Builder(id).miss().build();
		}
		return cacheResult;
	}

	@Override
	public CacheResult evictAll() {
		
		documentCache.clear();
		totalBytes = 0;
		CacheResult cacheResult = new CacheResult.Builder(null).evict().build();
		return cacheResult;
	}

	@Override
	public long cacheSizeBytes() {
		return totalBytes; // current sum of file.length() in the cache
	}

}
