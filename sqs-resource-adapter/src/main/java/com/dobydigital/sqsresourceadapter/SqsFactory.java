package com.dobydigital.sqsresourceadapter;

import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;

public class SqsFactory implements ConnectionFactory, QueueConnectionFactory
{
    private final static Logger log = LoggerFactory.getLogger( SqsFactory.class );
    private String awsRegion;
    private String awsAccessKey;
    private String awsAccessKeySecret;
    private SqsResourceAdapter resourceAdapter;
    private SQSConnectionFactory.Builder builder;
    private String id;

    public static Connection getConnection( String username, String password ) throws JMSException
    {
        return new SqsFactory().createConnection( username, password );
    }

    public SqsFactory()
    {
        builder = new SQSConnectionFactory.Builder();
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

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public SqsResourceAdapter getResourceAdapter()
    {
        return resourceAdapter;
    }

    public void setResourceAdapter( SqsResourceAdapter resourceAdapter )
    {
        this.resourceAdapter = resourceAdapter;
        resourceAdapter.addFactory( this );
    }
}
