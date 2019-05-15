package hellowork;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

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
 * @date 2019/4/29 15:14
 */
public class MsgSender {
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] args) throws IOException, TimeoutException {
        /**
         * 创建连接连接到MabbitMQ
         */
        ConnectionFactory factory = new ConnectionFactory();
        // 设置MabbitMQ所在主机ip或者主机名
        factory.setHost("127.0.0.1");
        // 创建一个连接
        Connection connection = factory.newConnection();
        // 创建一个频道
        //Channel是我们与RabbitMQ打交道的最重要的一个接口，
        // 大部分的业务操作是在Channel这个接口中完成的，
        // 包括定义Queue、定义Exchange、绑定Queue与Exchange、发布消息等
        Channel channel = connection.createChannel();
        // 指定一个队列
        /*•	Name 队列名字
        •	Durable（true, 消息代理重启后，队列依旧存在）
        •	Exclusive（true, 只被一个连接（connection）使用，而且当连接关闭后队列即被删除）
        •	Auto-delete（true, 当最后一个消费者退订后即被删除）
        •   Arguments（一些消息代理用他来完成类似与TTL的某些额外功能）*/
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        // 发送的消息
        String message = "hello world!";
        // 往队列中发出一条消息
        /*
        第一个参数是需要输入一个exchange。
        在RabbitMQ中，所有的消息都必须要通过exchange发送到各个queue里面去。
        发送者发送消息，其实也就是把消息放到exchange中去。而exchange知道应该把消息放到哪里去。
        在这个方法中，我们没有输入exchange的名称，只是定义了一个空的echange，而在第二个参数routeKey中输入了我们目标队列的名称。
        RabbitMQ会定义一个默认的exchange，这个exchange会把消息直接投递到输入的队列中，这样服务端只需要直接去这个定义了的队列中获取消息就可以了。
         */
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");
        // 关闭频道和连接
        channel.close();
        connection.close();
    }
}
