package com.kyndi.documents;

import java.io.InputStream;
import java.util.List;

/**
 * Document service interface
 */
public interface DocumentService {

  /**
   * Returns an input stream for a given file name
   *
   * @param fileName to retrieve
   * @return an {@see java.io.InputStream}
   */
  InputStream getFile(String fileName);

  /**
   * Find the document metadata for a given file name.
   *
   * @param fileName to retrieve
   * @return a {@see com.kyndi.documents.Document} or null if not found
   */
  Document findByName(String fileName);

  /**
   * Find all the documents available to the service
   *
   * @return a {@see java.util.List} of {@see com.kyndi.documents.Document} objects
   */
  List<Document> findAll();

}
