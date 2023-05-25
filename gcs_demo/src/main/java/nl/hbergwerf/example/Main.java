package nl.hbergwerf.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;

import com.google.cloud.ReadChannel;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.*;

public class Main {
  public static Storage storage;

  public static String bucket = "bucket";

  public static void main(String[] args) throws IOException {
    // Connect to local mock Cloud Storage instance (running in Docker).
    storage = StorageOptions.newBuilder()
      .setProjectId("hello")
      .setHost("http://localhost:4443").build().getService();

    // Create a bucket.
    storage.create(BucketInfo.of(bucket));
    
    // Create a blob ID.
    BlobId blobId = BlobId.of(bucket, "hello");

    // Create blob metadata.
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
      .setContentType("text/plain").build();

    // Create blob data.
    byte[] data = "Hello, Cloud Storage!".getBytes(StandardCharsets.UTF_8);
    storage.create(blobInfo, data);

    // This object is now available under the following URL:
    // http://localhost:4443/storage/v1/b/bucket/o/hello?alt=media

    // Check if the blob exists.
    printExists("hello");
    printExists("world");

    // Read blob via input stream.
    printReadStream("hello");

    // Write blob via input stream.
    writeViaStream("world", new ByteArrayInputStream("Streamed text".getBytes()));
  }

  public static void printExists(String blobName) {
    final BlobId id = BlobId.of(bucket, blobName);
    final boolean ex = storage.get(id) != null;
    System.out.println("Blob `" + blobName + "` exists: " + ex);
  }

  public static void printReadStream(String blobName) throws IOException {
    final BlobId id = BlobId.of(bucket, blobName);
    final Blob blob = storage.get(id);
    final ReadChannel channel = blob.reader();
    final InputStream stream = Channels.newInputStream(channel);
    final String content = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    System.out.println("Blob `" + blobName + "` content:");
    System.out.println(content);
  }

  public static Blob getBlob(String blobName, boolean failIfAlreadyExists) {
    final BlobId id = BlobId.of(bucket, blobName);
    final BlobInfo info = BlobInfo.newBuilder(id)
            .setContentType("text/plain").build();
    if (failIfAlreadyExists) {
      // If the blob already exists, this should fail.
      return storage.create(info);
    } else {
      // Try to retrieve the blob. If it does not exist; create it.
      final Blob blob = storage.get(id);
      return blob != null ? blob : storage.create(info);
    }
  }

  public static void writeViaStream(String blobName, InputStream content) throws IOException {
    final Blob blob = getBlob(blobName, true);
    final WriteChannel channel = blob.writer();
    final OutputStream outputStream = Channels.newOutputStream(channel);
    final long transferred = content.transferTo(outputStream);
    channel.close();
    System.out.println("Transferred " + transferred + " bytes to `" + blobName + "`.");
  }
}
