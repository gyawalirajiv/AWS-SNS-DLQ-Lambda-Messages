package messages.functions;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import messages.modal.Message;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MessagePublisherFunction {

    public static final String MESSAGES_TOPIC = "MESSAGES_TOPIC";
    private final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
    private final AmazonSNS sns = AmazonSNSClientBuilder.defaultClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void handler(S3Event event, Context context){
        LambdaLogger logger = context.getLogger();

        event.getRecords().forEach(record -> {
            S3ObjectInputStream s3ObjectInputStream = s3.getObject(record.getS3().getBucket().getName(),
                    record.getS3().getObject().getKey())
                    .getObjectContent();
            logger.log("Received an event from S3.");

            try {
                List<Message> messages = Arrays.asList(objectMapper.readValue(s3ObjectInputStream, Message[].class));
                logger.log(messages.toString());
                s3ObjectInputStream.close();
                messages.forEach(message -> {
                    try {
                        sns.publish(System.getenv(MESSAGES_TOPIC), objectMapper.writeValueAsString(message));
                    } catch (JsonProcessingException e) {
                        logger.log("Invalid JSON data!");
                        throw new RuntimeException("Error while processing S3 event",e);
                    }
                });
                logger.log("MESSAGE PUBLISHED");
            } catch (IOException e) {
                logger.log("Invalid IO data!");
                throw new RuntimeException("Error while processing S3 event",e);
            }

        });
    }

}
