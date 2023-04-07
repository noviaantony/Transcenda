package com.project202223t2g1t1.transcenda.SqsSnsMessageObject;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class SnsService {
    private AmazonSNS snsClient;

    @Value("${sns.topic}")
    private String topic;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    @Value("${spring.cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${spring.cloud.aws.credentials.secret-key}")
    private String secretKey;

    @PostConstruct
    public void init(){
        this.snsClient = AmazonSNSClientBuilder.standard()
            .withCredentials(new CustomAWSCredentialsProvider(accessKey, secretKey))
            .withRegion(region)
            .build();
    }

    public void sendRewardEmail(String recipient, String body){
        PublishRequest request = new PublishRequest(topic, body,"Reward Email")
                .withMessageAttributes(Collections.singletonMap("email",
                        new MessageAttributeValue().withDataType("String").withStringValue(recipient)));
        snsClient.publish(request);
    }

    public void sendSms(String phoneNumber, String message) {
        PublishRequest request = new PublishRequest()
                .withPhoneNumber(phoneNumber)
                .withMessage(message);
        snsClient.publish(request);
    }
}
