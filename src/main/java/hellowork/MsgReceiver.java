package hellowork;

import com.rabbitmq.client.*;

import java.io.IOException;
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
 * @date 2019/4/29 15:15
 */
public class MsgReceiver {
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv) throws IOException, InterruptedException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        // 打开连接和创建频道，与发送端一样
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // 声明队列，队列在消费者这里又声明了一遍。
        // 这是为了防止先启动消费者，当为消费者指定队列时，如果RabbitMQ服务器上未声明过队列，就会抛出IO异常。
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        // 创建队列消费者
        QueueingConsumer consumer = new QueueingConsumer(channel);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        // 指定消费队列
        channel.basicConsume(QUEUE_NAME, true, consumer);
        while (true) {
            // nextDelivery是一个阻塞方法（内部实现其实是阻塞队列的take方法）
            /*
            QueueingConsumer队列消费者，用于监听队列中的消息。
            调用nextDelivery方法时，内部实现就是调用队列的take方法。
            该方法的作用：获取并移除此队列的头部，在元素变得可用之前一直等待（如果有必要）。说白了就是如果没有消息，就处于阻塞状态。
             */
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String message = new String(delivery.getBody());
            System.out.println(" [x] Received '" + message + "'");
        }
    }
}
