package kr.co.readingtown.common.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.metrics.AwsSdkMetrics;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    @Value("${aws.s3.accessKey}")
    private String accessKey;

    @Value("${aws.s3.secretKey}")
    private String secretKey;

    @Value("${aws.s3.region}")
    private String region;

    // 애플리케이션 시작 시 AWS SDK의 JMX/metrics 등록을 비활성화
    @PostConstruct
    public void disableAwsSdkJmx() {
        try {
            // JVM의 컨테이너 감지 비활성화
            System.setProperty("jdk.internal.platform.disableContainerSupport", "true");

            // AWS SDK의 JMX MBean 등록 비활성화
            AwsSdkMetrics.unregisterMetricAdminMBean();

        } catch (Throwable ignored) {
            // 에러 무시 (JMX 비활성화만 목적)
        }
    }

    @Bean
    public AmazonS3 getAmazonS3Client() {

        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);

        return AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();

    }
}
