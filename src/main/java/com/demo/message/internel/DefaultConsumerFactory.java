package com.demo.message.internel;

import com.demo.message.QGMessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 *  Consumer工厂 读取consumer的class，并且创建queue，绑定到exchange中。
 */
public   class DefaultConsumerFactory  implements ApplicationContextAware {

   private final Logger logger = LoggerFactory.getLogger(DefaultConsumerFactory.class);


    private final ConnectionFactory connectionFactory;
    private final RabbitAdmin admin;
    private final SimpleMessageConverter simpleMessageConverter ;
    private final String context;
    private final TopicExchange topicExchange;
    /**
     * 并发consumer个数
     */
    private int concurrentConsumers = 10;

    public DefaultConsumerFactory(ConnectionFactory connectionFactory, RabbitAdmin admin, SimpleMessageConverter simpleMessageConverter, String context) {
        this.connectionFactory = connectionFactory;
        this.admin = admin;
        this.simpleMessageConverter = simpleMessageConverter;
        this.context = context;
        this.topicExchange = new TopicExchange("amq.topic");
    }



    /**
     * 获取所有实现了  {@link QGMessageConsumer}接口的bean
     * @param applicationContext context
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        Map<String, QGMessageConsumer>  consumerBeans =   applicationContext.getBeansOfType(QGMessageConsumer.class);
        this.addMessageListener(consumerBeans);
        logger.info("Loaded rabbit consumers {} success.", consumerBeans);

    }

    private void addMessageListener(Map<String, QGMessageConsumer> consumerBeans)
    {
        if(consumerBeans == null){
            logger.info("can not load any rabbit consumer...");
            return;
        }

        try{
        	for(QGMessageConsumer consumer : consumerBeans.values())
        	{
        		SimpleMessageListenerContainer listenerContainer = new SimpleMessageListenerContainer();
                listenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        		listenerContainer.setConnectionFactory(connectionFactory);
        		listenerContainer.setMessageConverter(this.simpleMessageConverter);
                listenerContainer.setConcurrentConsumers(concurrentConsumers);
                listenerContainer.setPrefetchCount(concurrentConsumers);

        		//linstener
        		MessageListenerAdapter adapter = new MessageListenerAdapter(consumer);
        		adapter.setMessageConverter(this.simpleMessageConverter);
        		listenerContainer.setMessageListener(adapter);
        		listenerContainer.setQueueNames(this.declareAndGetQueueName(consumer));

        		listenerContainer.start();

        		logger.info("starting rabbitmq container {} success, concurrentConsumers {}.", consumer, this.concurrentConsumers);
        	}
        }catch(Exception e)
        {
        	logger.warn("starting rabbitmq container failed.",e);
        }
    }


    private String declareAndGetQueueName(QGMessageConsumer consumer)
    {
        //new queue
        String queueName =   "qg:" + this.context + ":" + consumer.getMessageTopic();

        Queue queue = new Queue(queueName);
        admin.declareQueue(queue);

        //binding
        Binding binding =  BindingBuilder.bind(queue).to(topicExchange).with(consumer.getMessageTopic());
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
