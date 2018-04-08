package com.kyndi.documents;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.InputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.kyndi.documents.CacheResult.CacheOperation;

@RunWith(SpringRunner.class)
@ContextConfiguration(
    loader = SpringBootContextLoader.class,
    classes = Application.class
)
@WebAppConfiguration
public class DocumentCacheTest {
  private static final String largePdfFileName = "AAR0704.pdf";
  private static final String smallPdfFileName = "AAB0102.pdf";

  @Autowired
  private DocumentService docSvc;
  
  @Autowired
  private DocumentCache docCache;

  @Test
  public void testLargeDocumentCachePutItem() throws Exception {
		InputStream itemInputStream = docSvc.getFile(largePdfFileName);
		CacheResult cacheResult = this.docCache.putItem(largePdfFileName, itemInputStream);
		assertEquals(cacheResult.getOperation(), CacheOperation.NOOP);
		assertEquals(cacheResult.isSuccess(), false);
		assertNotNull(cacheResult.getError());
  }
  
  @Test
  public void testSmallDocumentCachePutItem() throws Exception {
		InputStream itemInputStream = docSvc.getFile(smallPdfFileName);
		CacheResult cacheResult = this.docCache.putItem(smallPdfFileName, itemInputStream);
		assertEquals(cacheResult.getOperation(), CacheOperation.PUT);
		assertEquals(cacheResult.isSuccess(), true);
		assertNull(cacheResult.getError());
  }

  @Test
  public void testDocumentCacheEvict() throws Exception {
		InputStream itemInputStream = docSvc.getFile(smallPdfFileName);
		this.docCache.putItem(smallPdfFileName, itemInputStream); // enqueue
		CacheResult cacheResult = this.docCache.evict(smallPdfFileName);
		assertEquals(cacheResult.getOperation(), CacheOperation.EVICT);
		assertEquals(cacheResult.isSuccess(), true);
		assertNull(cacheResult.getError());
  }

  @Test
  public void testDocumentCacheEvictAll() throws Exception {
		InputStream itemInputStream = docSvc.getFile(smallPdfFileName);
		this.docCache.putItem(smallPdfFileName, itemInputStream); // enqueue
		CacheResult cacheResult = this.docCache.evictAll();
		assertEquals(cacheResult.getOperation(), CacheOperation.EVICT);
		assertEquals(cacheResult.isSuccess(), true);
		assertNull(cacheResult.getError());
  }
  
  @Test
  public void testDocumentCacheGetItem() throws Exception {
		InputStream itemInputStream = docSvc.getFile(smallPdfFileName);
		this.docCache.putItem(smallPdfFileName, itemInputStream); // enqueue
		CacheResult cacheResult = this.docCache.getItem(smallPdfFileName);
		assertEquals(cacheResult.getOperation(), CacheOperation.HIT);
		assertEquals(cacheResult.isSuccess(), true);
		assertNull(cacheResult.getError());
  }
  
  @Test
  public void testDocumentCacheGetItemNotExisting() throws Exception {
		CacheResult cacheResult = this.docCache.getItem("notExisting"+smallPdfFileName);
		assertEquals(cacheResult.getOperation(), CacheOperation.MISS);
		assertEquals(cacheResult.isSuccess(), true);
		assertNull(cacheResult.getError());
  }
}
