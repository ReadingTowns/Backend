package kr.co.readingtown.common.s3;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket}")
    private String bucket;

    private static final String REGION = "ap-northeast-2";

    // 이미지 업로드 용 presigned url 발급
    public String generateUploadPresignedUrl(String key) {

        Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 5);   // 5분 유효

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucket, key)
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(expiration);

        return amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString();
    }

    // 이미지 조회 용 presigned url 발급
    public String generateDownloadPresignedUrl(String key) {

        Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 5);

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucket, key)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(expiration);

        return amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString();
    }

    // 기존 이미지 삭제
    public void deleteImage(String key) {
        if (key == null || key.isBlank()) return;
        amazonS3.deleteObject(bucket, key);
    }

    // 데이터베이스에 저장할 때 사용
    public String keyToUrl(String key) {
        if (key == null || key.isBlank()) return null;
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, REGION, key);
    }

    // key 추출해서 비교할 때 사용
    public String urlToKey(String url) {
        if (url == null || url.isBlank()) return null;

        String prefix = String.format("https://%s.s3.%s.amazonaws.com/", bucket, REGION);
        if (url.startsWith(prefix)) {
            return url.substring(prefix.length());
        }
        return url;
    }
}
