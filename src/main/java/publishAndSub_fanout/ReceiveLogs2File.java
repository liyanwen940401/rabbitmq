package publishAndSub_fanout;

import com.rabbitmq.client.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
 * @date 2019/6/10 10:39
 */
public class ReceiveLogs2File {
    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] argv) throws IOException, InterruptedException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        // 打开连接和创建频道，与发送端一样
        Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        // 声明一个随机队列
        String queueName = channel.queueDeclare().getQueue();
        //告诉转发器转发消息到队列。Binding就是关联转发器和队列
        channel.queueBind(queueName, EXCHANGE_NAME, "");
        // 创建队列消费者
        final Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                print2File(message);
//			    System.out.println(" [x] Received '" + message + "'");
            }
        };
        channel.basicConsume(queueName, true, consumer);
    }

    private static void print2File(String msg) {
        try {
            String dir = ReceiveLogs2File.class.getClassLoader().getResource("").getPath();
            String logFileName = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            File file = new File(dir, logFileName + ".log");
            FileOutputStream fos = new FileOutputStream(file, true);
            fos.write(((new SimpleDateFormat("HH:mm:ss").format(new Date())+" - "+msg + "\r\n").getBytes()));
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
