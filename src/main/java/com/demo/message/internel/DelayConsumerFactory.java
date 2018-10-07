package com.demo.message.internel;

import com.demo.message.QGDelayMessageConsumer;
import com.demo.message.QGMessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 *  延迟队列的Consumer工厂 读取consumer的class，并且创建queue，绑定到exchange中。
 *  @see: <a href=http://git.dev.yoho.cn/yoho-documents/api-interfaces/wikis/rabbitmq-delay-queue/>
 */
public   class DelayConsumerFactory implements ApplicationContextAware {

    private final Logger logger = LoggerFactory.getLogger(DelayConsumerFactory.class);

    private final ConnectionFactory connectionFactory;
    private final RabbitAdmin admin;
    private final SimpleMessageConverter simpleMessageConverter ;
    private final String context;
    private final TopicExchange topicExchange;
    private final TopicExchange expireExchange;

    /**
     * 并发consumer个数
     */
    private int concurrentConsumers = 10;

    public DelayConsumerFactory(ConnectionFactory connectionFactory, RabbitAdmin admin, SimpleMessageConverter simpleMessageConverter, String context) {
        this.connectionFactory = connectionFactory;
        this.admin = admin;
        this.simpleMessageConverter = simpleMessageConverter;
        this.context = context;
        this.topicExchange = new TopicExchange("amq.topic");

        expireExchange = new TopicExchange("qg.expire");
        this.admin.declareExchange(expireExchange);
    }



    /**
     * 获取所有实现了  {@link QGMessageConsumer}接口的bean
     * @param applicationContext context
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        Map<String, QGDelayMessageConsumer>  consumerBeans =   applicationContext.getBeansOfType(QGDelayMessageConsumer.class);
        this.addMessageListener(consumerBeans);
        logger.info("Loaded rabbit consumers {} success.", consumerBeans);

    }

    private void addMessageListener(Map<String, QGDelayMessageConsumer> consumerBeans)
    {
        if(consumerBeans == null){
            logger.info("can not load any rabbit consumer...");
            return;
        }
        
        try{
        	for(QGDelayMessageConsumer consumer : consumerBeans.values())
        	{
        		SimpleMessageListenerContainer listenerContainer = new SimpleMessageListenerContainer();
        		
        		listenerContainer.setConnectionFactory(connectionFactory);
        		listenerContainer.setMessageConverter(this.simpleMessageConverter);

                listenerContainer.setConcurrentConsumers(concurrentConsumers);
                listenerContainer.setPrefetchCount(concurrentConsumers);

        		//linstener
        		MessageListenerAdapter adapter = new MessageListenerAdapter(consumer);
        		adapter.setMessageConverter(this.simpleMessageConverter);
        		listenerContainer.setMessageListener(adapter);

                //TimeQueue & expireQueue
                this.declareTimeQueue(consumer);
                String expireQueue = this.declareExpiredQueue(consumer);
                listenerContainer.setQueueNames(expireQueue);
        		
        		listenerContainer.start();
        		
        		logger.info("starting rabbitmq container {} success.", consumer);
        	}
        }catch(Exception e)
        {
        	logger.warn("starting rabbitmq container failed.",e);
        }
    }


    /**
     * 先添加一个expire之后的消息的queue，consumer最终会连接到这个queue，并且处理消息
     * @param consumer
     * @return
     */
    private String declareExpiredQueue(QGDelayMessageConsumer consumer) {

        //queue name
        String queueName =   "qg_delay:" + this.context + ":" + consumer.getMessageTopic();

        Queue queue = new Queue(queueName);
        admin.declareQueue(queue);

        //binding to yoho.expire with  [ yoho_delay.*.TOPIC ]
        String routingKey = "qg_delay.*." + consumer.getMessageTopic();
        Binding binding =  BindingBuilder.bind(queue).to(expireExchange).with(routingKey);
        admin.declareBinding(binding);

        return  queueName;
    }

    /**
     * 先添加一个expire之后的消息的queue，consumer最终会连接到这个queue，并且处理消息
     * @param consumer
     * @return
     */
    private String declareTimeQueue(QGDelayMessageConsumer consumer) {

        //queue name:  delay.5m.queue
        String queueName =   "delay:" + consumer.getDelayInMinutes() + "m" + ".queue";

        Map<String, Object> arguments=  new HashMap<>();
        arguments.put("x-message-ttl", consumer.getDelayInMinutes() * 60 * 1000); //毫秒
        arguments.put("x-dead-letter-exchange", expireExchange.getName());
        Queue queue = new Queue(queueName, true, false, false, arguments);

        admin.declareQueue(queue);

        //binding to amq.topic with  [ yoho_delay.2m.* ]
        String routingKey = "qg_delay." + consumer.getDelayInMinutes() + "m" + ".*";
        Binding binding =  BindingBuilder.bind(queue).to(topicExchange).with(routingKey);
        admin.declareBinding(binding);

        return  queueName;
    }
    public int getConcurrentConsumers() {
        return concurrentConsumers;
    }

    public void setConcurrentConsumers(int concurrentConsumers) {
        this.concurrentConsumers = concurrentConsumers;
    }

}
