package com.dobydigital.jms;

import com.amazon.sqs.javamessaging.AmazonSQSMessagingClientWrapper;
import com.amazon.sqs.javamessaging.SQSConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path( "/" )
@Produces( "text/plain" )
public class SqsMessageProducer
{
    @Resource( name = "jms/connectionFactory" )
    private ConnectionFactory factory;
    private final static Logger log = LoggerFactory.getLogger( SqsMessageProducer.class );

    @GET
    @Path( "/send" )
    public String send() throws Exception
    {
        log.debug( "send()" );
        Connection connection = factory.createConnection();
        Session session = connection.createSession( false, Session.AUTO_ACKNOWLEDGE );
        AmazonSQSMessagingClientWrapper client = ( (SQSConnection) connection ).getWrappedAmazonSQSClient();
        if ( !client.queueExists( "testQueue" ) )
        {
            client.createQueue( "testQueue" );
        }
        Queue queue = session.createQueue( "testQueue" );
        MessageProducer producer = session.createProducer( queue );
        TextMessage message = session.createTextMessage( "test" );
        producer.send( message );
        connection.close();
        return "Sent.";
    }
}
