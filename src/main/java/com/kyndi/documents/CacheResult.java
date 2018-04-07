package com.kyndi.documents;

import java.io.File;

public class CacheResult {
  private String id;
  private File file;
  private CacheOperation operation;
  private boolean success;
  private Throwable error;

  private CacheResult(String id, File file,
      CacheOperation operation, boolean success, Throwable error) {
    this.id = id;
    this.file = file;
    this.operation = operation;
    this.success = success;
    this.error = error;
  }

  public String getId() {
    return id;
  }

  public File getFile() {
    return file;
  }

  public CacheOperation getOperation() {
    return operation;
  }

  public boolean isSuccess() {
    return success;
  }

  public Throwable getError() {
    return error;
  }

  public boolean isHit() {
    return this.operation == CacheOperation.HIT;
  }

  public boolean isMiss() {
    return this.operation == CacheOperation.MISS;
  }

  static class Builder {
    private CacheOperation operation;
    private String id;
    private File file;
    private boolean success = true;
    private Throwable error;

    Builder(String id) {
      this.id = id;
    }

    Builder put(File file) {
      this.file = file;
      this.operation = CacheOperation.PUT;

      return this;
    }

    Builder hit(File file) {
      this.operation = CacheOperation.HIT;
      this.file = file;

      return this;
    }

    Builder miss() {
      this.operation = CacheOperation.MISS;

      return this;
    }

    Builder evict() {
      this.operation = CacheOperation.EVICT;

      return this;
    }

    Builder fail(Throwable throwable) {
      this.error = throwable;
      this.success = false;
      this.operation = CacheOperation.NOOP;
      return this;
    }

    CacheResult build() {
      return new CacheResult(
          this.id,
          this.file,
          this.operation,
          this.success,
          this.error
      );
    }
  }

  enum CacheOperation {
    PUT, HIT, MISS, EVICT, NOOP
  }


}
