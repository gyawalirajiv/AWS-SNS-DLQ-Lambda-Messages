package messages.functions;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;

public class MessageExceptionFunction {
    public void handler(SNSEvent event, Context context){
        LambdaLogger logger = context.getLogger();
        event.getRecords().forEach(record -> {
            logger.log("Dead Letter Record: " + record.toString());
        });
    }
}
