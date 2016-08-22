package com.dobydigital.sqsresourceadapter;

import com.amazon.sqs.javamessaging.AmazonSQSMessagingClientWrapper;
import com.amazon.sqs.javamessaging.SQSConnection;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.resource.spi.work.Work;
import javax.resource.spi.work.WorkManager;
import javax.transaction.xa.XAResource;
import java.util.HashSet;
import java.util.Set;

public class SqsResourceAdapter implements ResourceAdapter
{
    private ConnectionFactory connectionFactory;
    private WorkManager workManager;
    private Set<Work> works = new HashSet<>();

    @Override
    public void start( BootstrapContext ctx ) throws ResourceAdapterInternalException
    {
        workManager = ctx.getWorkManager();
    }

    @Override
    public void stop()
    {
        works.forEach( Work::release );
    }

    @Override
    public void endpointActivation( MessageEndpointFactory endpointFactory, ActivationSpec spec ) throws ResourceException
    {
        MessageListener endpoint = (MessageListener) endpointFactory.createEndpoint( null );
        SqsActivationSpec activationSpec = (SqsActivationSpec) spec;

        Work work = new Work()
        {
            private Connection connection;

            @Override
            public void release()
            {
                try
                {
                    connection.close();
                }
                catch ( JMSException e )
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void run()
            {
                try
                {
                    connection = connectionFactory.createConnection();
                    Session session = connection.createSession( false, Session.AUTO_ACKNOWLEDGE );
                    AmazonSQSMessagingClientWrapper client = ( (SQSConnection) connection ).getWrappedAmazonSQSClient();
                    if ( !client.queueExists( activationSpec.getDestination() ) )
                    {
                        client.createQueue( activationSpec.getDestination() );
                    }
                    Queue queue = session.createQueue( activationSpec.getDestination() );
                    MessageConsumer messageConsumer = session.createConsumer( queue );
                    messageConsumer.setMessageListener( endpoint );
                    connection.start();
                }
                catch ( JMSException e )
                {
                    e.printStackTrace();
                }
            }
        };
        works.add( work );
        workManager.doWork( work );
    }

    @Override
    public void endpointDeactivation( MessageEndpointFactory endpointFactory, ActivationSpec spec )
    {

    }

    @Override
    public XAResource[] getXAResources( ActivationSpec[] specs ) throws ResourceException
    {
        return new XAResource[ 0 ];
    }

    public void setConnectionFactory( ConnectionFactory connectionFactory )
    {
        if ( this.connectionFactory == null )
        {
            this.connectionFactory = connectionFactory;
        }
    }
}
