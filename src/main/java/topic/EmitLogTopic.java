package topic;

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
 * @date 2019/8/29 10:24
 */
public class EmitLogTopic {
    private final static String EXCHANGE_NAME = "topic_logs";

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
        Channel channel = connection.createChannel();
        // 指定转发——广播
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");

        //所有设备和日志级别
        String[] facilities ={"auth","cron","kern","auth.A"};
        String[] severities={"error","info","warning"};

        for(int i=0;i<4;i++){
            for(int j=0;j<3;j++){
                //每一个设备，每种日志级别发送一条日志消息
                String routingKey = facilities[i]+"."+severities[j%3];
                String result ="";
                char[] chars = new char[i+1];
                Arrays.fill(chars, '.');
                result = new String(chars);
                // 发送的消息
                String message =" Hello World!"+result;
                //参数1：exchange name
                //参数2：routing key
                channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes());
                System.out.println(" [x] Sent [" + routingKey +"] : '"+ message + "'");
            }
        }
        // 关闭频道和连接
        channel.close();
        connection.close();
    }
}
