package TaskQueues;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Date;
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
 * @date 2019/8/28 17:31
 */
public class FairDispatchWorker {
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv) throws IOException, InterruptedException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        //保证一次只分发一个
        channel.basicQos(1);
        final Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
                try {
                    for (char ch: message.toCharArray()) {
                        if (ch == '.') {
                            Thread.sleep(1000);
                        }
                    }
                } catch (InterruptedException e) {
                } finally {
                    System.out.println(" [x] Done! at " +new Date().toLocaleString());
                    // 手动应答
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }
        };
        // false: 表示手动应答,需要手动调用basicAck()来应答
        channel.basicConsume(QUEUE_NAME, false, consumer);
    }
}
