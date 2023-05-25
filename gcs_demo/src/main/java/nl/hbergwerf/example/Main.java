package nl.hbergwerf.example;

import java.nio.charset.StandardCharsets;
import com.google.cloud.storage.*;

public class Main {
  public static void main(String[] args) {
    // Connect to local mock Cloud Storage instance (running in Docker).
    Storage storage = StorageOptions.newBuilder()
      .setProjectId("hello")
      .setHost("http://localhost:4443").build().getService();
    
    // Create a bucket.
    Bucket bucket = storage.create(BucketInfo.of("bucket"));
    
    // Create a blob ID.
    BlobId blobId = BlobId.of(bucket.getName(), "hello");

    // Create blob metadata.
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
      .setContentType("text/plain").build();
    
    // Create blob data.
    byte[] data = "Hello, Cloud Storage!".getBytes(StandardCharsets.UTF_8);

    // Upload blob.
    storage.create(blobInfo, data);

    // This object is now available under the following URL:
    // http://localhost:4443/storage/v1/b/bucket/o/hello?alt=media
  }
}
