package com.project202223t2g1t1.transcenda.SqsSnsMessageObject;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;

public class CustomAWSCredentialsProvider implements AWSCredentialsProvider {

    private AWSCredentials awsCredentials;

    public CustomAWSCredentialsProvider(String accessKey, String secretKey) {
        awsCredentials = new AWSCredentials() {
            @Override
            public String getAWSAccessKeyId() {
                return accessKey;
            }

            @Override
            public String getAWSSecretKey() {
                return secretKey;
            }
        };
    }

    @Override
    public AWSCredentials getCredentials() {
        return awsCredentials;
    }

    @Override
    public void refresh() {
    }
}