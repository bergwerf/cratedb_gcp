package nl.hbergwerf.example;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.ReadChannel;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.*;

public class Main {
  public static Storage storage;

  public static String bucketName = "bucket";

  public static void main(String[] args) throws IOException {
    // Connect to local mock Cloud Storage instance (running in Docker).
    /*storage = StorageOptions.newBuilder()
      .setProjectId("hello")
      .setHost("http://localhost:4443").build().getService();*/

    // Read parameters.
    String prop_path = "src/storage.properties";
    FileInputStream prop_input = new FileInputStream(prop_path);
    Properties prop = new Properties();
    prop.load(prop_input);
    bucketName = prop.getProperty("bucket_name");

    ServiceAccountCredentials credentials =
            ServiceAccountCredentials.fromStream(new FileInputStream(
                    prop.getProperty("service_account_key")));

    // Connect to a bucket via a JSON key.
    StorageOptions.Builder b = StorageOptions.newBuilder();
    b.setCredentials(credentials);
    storage = b.build().getService();

    // Create a bucket.
    //Bucket bucket = storage.create(BucketInfo.of(bucketName));

    // Retrieve a bucket.
    Bucket bucket = storage.get(bucketName);

    // Create blob data.
    byte[] data = "Hello, Cloud Storage!".getBytes(StandardCharsets.UTF_8);
    bucket.create("hello", data, "text/plain");

    // This object is now available under the following URL:
    // http://localhost:4443/storage/v1/b/bucket/o/hello?alt=media

    // Check if the blob exists.
    printExists("hello");
    printExists("world");

    // Read blob via input stream.
    printReadStream("hello");

    // Write blob via input stream.
    writeViaStream("world", new ByteArrayInputStream("Streamed text".getBytes()));
    writeViaStream("hello", new ByteArrayInputStream("Does this overwrite?".getBytes()));

    //bucket.create("hello", new byte[] {});
  }

  public static void printExists(String blobName) {
    final BlobId id = BlobId.of(bucketName, blobName);
    final boolean ex = storage.get(id) != null;
    System.out.println("Blob `" + blobName + "` exists: " + ex);
  }

  public static void printReadStream(String blobName) throws IOException {
    final BlobId id = BlobId.of(bucketName, blobName);
    final Blob blob = storage.get(id);
    final ReadChannel channel = blob.reader();
    final InputStream stream = Channels.newInputStream(channel);
    final String content = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    System.out.println("Blob `" + blobName + "` content:");
    System.out.println(content);
  }

  public static Blob getBlob(String blobName, boolean failIfAlreadyExists) {
    final BlobId id = BlobId.of(bucketName, blobName);
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
