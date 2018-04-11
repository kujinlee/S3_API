package com.sample.documents;

import java.util.Date;

/**
 * A resource representation of a document
 */
public class Document {
  private String name;
  private String md5Checksum;
  private long size; // bytes
  private Date lastModified;

  public Document(String name, String md5Checksum, long size, Date lastModified) {
    this.name = name;
    this.md5Checksum = md5Checksum;
    this.size = size;
    this.lastModified = lastModified;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMd5Checksum() {
    return md5Checksum;
  }

  public void setMd5Checksum(String md5Checksum) {
    this.md5Checksum = md5Checksum;
  }

  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }

  public Date getLastModified() {
    return lastModified;
  }

  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }
}
