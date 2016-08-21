package com.dobydigital.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.PostConstruct;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@MessageDriven(
    activationConfig = {
        @ActivationConfigProperty( propertyName = "destinationType", propertyValue = "javax.jms.Queue" ),
        @ActivationConfigProperty( propertyName = "destination", propertyValue = "testQueue" ),
        @ActivationConfigProperty( propertyName = "connectionFactory", propertyValue = "jms/connectionFactory" ),
    }
)
public class SqsMessageConsumer implements MessageListener
{
    private static final Logger log = LoggerFactory.getLogger( SqsMessageConsumer.class );
    @Inject
    private TestInjectBean testInjectBean;

    @PostConstruct
    void postConstruct()
    {

    }

    @Override
    public void onMessage( Message message )
    {
        log.info( "onMessage() - message:{}", message );
        try
        {
            log.info( "onMessage() - textMessage:{}", ( (TextMessage) message ).getText() );
        }
        catch ( JMSException e )
        {
            throw new RuntimeException( e );
        }
    }
}
