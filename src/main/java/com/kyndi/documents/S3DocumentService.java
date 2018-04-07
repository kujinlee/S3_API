package com.kyndi.documents;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class S3DocumentService implements DocumentService {
  private String awsAccessKey;
  private String awsSecretKey;
  private String bucketName;
  private AmazonS3 s3Client;

  @PostConstruct
  private void setUpS3Client() {
    final BasicAWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
    s3Client = AmazonS3ClientBuilder
        .standard()
        .withCredentials(new AWSStaticCredentialsProvider(credentials))
        .withRegion(Regions.US_WEST_1)
      .build();
  }

  @Override
  public Document findByName(String fileName) {
    S3Object object = s3Client.getObject(bucketName, fileName);

    if (object == null) {
      return null;
    }

    return new Document(
        fileName,
        object.getObjectMetadata().getETag(),
        object.getObjectMetadata().getContentLength(),
        object.getObjectMetadata().getLastModified()
    );
  }

  @Override
  public List<Document> findAll() {
    ObjectListing objects = s3Client.listObjects(bucketName);

    return objects.getObjectSummaries()
        .stream()
        .map(summary -> new Document(
          summary.getKey(),
          summary.getETag(),
          summary.getSize(),
          summary.getLastModified()
        ))
        .collect(Collectors.toList());
  }

  @Override
  public InputStream getFile(String fileName) {
    S3Object object = s3Client.getObject(bucketName, fileName);

    return object.getObjectContent();
  }

  @Value("${bucket-name}")
  public void setBucketName(String bucketName) {
    this.bucketName = bucketName;
  }

  @Value("${aws-access-key}")
  public void setAwsAccessKey(String awsAccessKey) {
    this.awsAccessKey = awsAccessKey;
  }

  @Value("${aws-secret-key}")
  public void setAwsSecretKey(String awsSecretKey) {
    this.awsSecretKey = awsSecretKey;
  }
}
