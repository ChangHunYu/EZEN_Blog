package ezen.blog.config;

import io.minio.*;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

@TestConfiguration
public class MinioTestConfig {

    @Autowired
    private MinioClient minioClient;

    public static class MinioCleanupListener implements TestExecutionListener {

        @Override
        public void beforeTestMethod(TestContext testContext) throws Exception {
            MinioClient minioClient = testContext.getApplicationContext().getBean(MinioClient.class);
            cleanupMinioTestBuckets(minioClient);
        }

        private void cleanupMinioTestBuckets(MinioClient minioClient) throws Exception {
            String[] testBuckets = {"test-user-profile-images", "test-post-images"};

            for (String bucketName : testBuckets) {
                boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
                if (found) {
                    Iterable<Result<Item>> results = minioClient.listObjects(
                            ListObjectsArgs.builder().bucket(bucketName).recursive(true).build());
                    for (Result<Item> result : results) {
                        Item item = result.get();
                        minioClient.removeObject(
                                RemoveObjectArgs.builder().bucket(bucketName).object(item.objectName()).build());
                    }
                } else {
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                }
            }
        }
    }
}
