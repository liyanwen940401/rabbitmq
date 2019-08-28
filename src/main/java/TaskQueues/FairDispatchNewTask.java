package TaskQueues;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

/**
 * <p></p>
 * <p>
 * <PRE>
 * <BR>	修改记录
 * <BR>-----------------------------------------------
 * <BR>	修改日期			修改人			修改内容
 * </PRE>
 *
 * @author liyw
 * @version V1.0
 * @date 2019/5/14 16:20
 */
public class FairDispatchNewTask {
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] args) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        int prefetchCount = 1;
        //限制发给同一个消费者不得超过1条消息
        channel.basicQos(prefetchCount);
        for (int i = 0; i < 8; i++) {
            String result ="";
            if(i!=8){
                char[] chars = new char[8-i];
                Arrays.fill(chars, '.');
                result = new String(chars);
            }
            String message = "Hello World"+ result+(8-i);
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
        }
        channel.close();
        connection.close();
    }
}
