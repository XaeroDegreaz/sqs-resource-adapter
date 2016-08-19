package com.dobydigital.sqs;

import com.amazon.sqs.javamessaging.AmazonSQSMessagingClientWrapper;
import com.amazon.sqs.javamessaging.SQSConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
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
import javax.transaction.xa.XAResource;

public class SqsResourceAdapter implements ResourceAdapter
{
    private final static Logger log = LoggerFactory.getLogger( SqsResourceAdapter.class );
    private String awsAccessKeySecret;
    private ConnectionFactory factory;
    private Connection connection;

    @Override
    public void start( BootstrapContext ctx ) throws ResourceAdapterInternalException
    {
    }

    @Override
    public void stop()
    {

    }

    @Override
    public void endpointActivation( MessageEndpointFactory endpointFactory, ActivationSpec spec ) throws ResourceException
    {
        SqsActivationSpec activationSpec = (SqsActivationSpec) spec;
        try
        {
            MessageListener listener = (MessageListener) Class.forName( activationSpec.getListener() ).newInstance();
            connection = factory.createConnection();
            Session session = connection.createSession( false, Session.AUTO_ACKNOWLEDGE );
            AmazonSQSMessagingClientWrapper client = ( (SQSConnection) connection ).getWrappedAmazonSQSClient();
            if ( !client.queueExists( activationSpec.getDestination() ) )
            {
                client.createQueue( activationSpec.getDestination() );
            }
            Queue queue = session.createQueue( activationSpec.getDestination() );
            MessageConsumer messageConsumer = session.createConsumer( queue );
            messageConsumer.setMessageListener( listener );
            connection.start();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
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

    public String getAwsAccessKeySecret()
    {
        return awsAccessKeySecret;
    }

    public void setAwsAccessKeySecret( String awsAccessKeySecret )
    {
        this.awsAccessKeySecret = awsAccessKeySecret;
    }

    public void setFactory( ConnectionFactory factory )
    {
        this.factory = factory;
    }
}
