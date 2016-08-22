package com.dobydigital.sqsresourceadapter;

import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import java.util.logging.Logger;

/**
 * A thin wrapper around the com.amazon.sqs.javamessaging.SQSConnectionFactory class.
 * Because that class does not have a default constructor, and it's other constructor is private
 * a wrapper is needed in order to use that factory as with @Resource
 */
public class SqsFactory implements ConnectionFactory, QueueConnectionFactory
{
    private String awsRegion;
    private String awsAccessKey;
    private String awsAccessKeySecret;
    private SqsResourceAdapter resourceAdapter;
    private SQSConnectionFactory.Builder builder;
    private Logger log = Logger.getLogger( SqsFactory.class.getName() );

    public SqsFactory()
    {
        builder = new SQSConnectionFactory.Builder();
        log.info( "SqsFactory() - Constructed." );
    }

    @Override
    public Connection createConnection() throws JMSException
    {
        return finalizeBuilder().build().createConnection();
    }

    @Override
    public Connection createConnection( String userName, String password ) throws JMSException
    {
        return finalizeBuilder().build().createConnection( userName, password );
    }

    @Override
    public QueueConnection createQueueConnection() throws JMSException
    {
        return finalizeBuilder().build().createQueueConnection();
    }

    @Override
    public QueueConnection createQueueConnection( String username, String password ) throws JMSException
    {
        return finalizeBuilder().build().createQueueConnection( username, password );
    }

    private SQSConnectionFactory.Builder finalizeBuilder()
    {
        return builder.withRegionName( getAwsRegion() ).withAWSCredentialsProvider( new AWSCredentialsProvider()
        {
            @Override
            public AWSCredentials getCredentials()
            {
                return new BasicAWSCredentials( getAwsAccessKey(), getAwsAccessKeySecret() );
            }

            @Override
            public void refresh()
            {

            }
        } );
    }

    public String getAwsRegion()
    {
        return awsRegion;
    }

    public void setAwsRegion( String awsRegion )
    {
        this.awsRegion = awsRegion;
    }

    public String getAwsAccessKey()
    {
        return awsAccessKey;
    }

    public void setAwsAccessKey( String awsAccessKey )
    {
        this.awsAccessKey = awsAccessKey;
    }

    public String getAwsAccessKeySecret()
    {
        return awsAccessKeySecret;
    }

    public void setAwsAccessKeySecret( String awsAccessKeySecret )
    {
        this.awsAccessKeySecret = awsAccessKeySecret;
    }

    public SqsResourceAdapter getResourceAdapter()
    {
        return resourceAdapter;
    }

    public void setResourceAdapter( SqsResourceAdapter resourceAdapter )
    {
        this.resourceAdapter = resourceAdapter;
        resourceAdapter.setConnectionFactory( this );
    }
}
