package com.dobydigital.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@MessageDriven(
    activationConfig = {
        @ActivationConfigProperty( propertyName = "listener", propertyValue = "com.dobydigital.jms.SqsMessageConsumer" ),
        @ActivationConfigProperty( propertyName = "destinationType", propertyValue = "javax.sqsresourceadapter.Queue2" ),
        @ActivationConfigProperty( propertyName = "destination", propertyValue = "testQueue" )
    }
)

public class SqsMessageConsumer2 implements MessageListener
{
    private static final Logger log = LoggerFactory.getLogger( SqsMessageConsumer2.class );

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
