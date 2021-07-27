package messages.functions;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
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
import java.util.logging.Logger;

public class MessageSubscriberFunction {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void handler(SNSEvent event, Context context){
        LambdaLogger logger = context.getLogger();

        event.getRecords().forEach(record -> {
            try {
                Message message = objectMapper.readValue(record.getSNS().getMessage(), Message.class);
                logger.log("RECEIVED A MESSAGE: \n" + message.message + "\n");
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
    }

}
