package com.dobydigital.sqsresourceadapter;

import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.InvalidPropertyException;
import javax.resource.spi.ResourceAdapter;

public class SqsActivationSpec implements ActivationSpec
{
    private ResourceAdapter resourceAdapter;
    private String destination;
    private String connectionFactory;

    @Override
    public void validate() throws InvalidPropertyException
    {
    }

    @Override
    public ResourceAdapter getResourceAdapter()
    {
        return resourceAdapter;
    }

    @Override
    public void setResourceAdapter( ResourceAdapter resourceAdapter ) throws ResourceException
    {
        this.resourceAdapter = resourceAdapter;
    }

    public String getDestination()
    {
        return destination;
    }

    public void setDestination( String destination )
    {
        this.destination = destination;
    }

    public String getConnectionFactory()
    {
        return connectionFactory;
    }

    public void setConnectionFactory( String connectionFactory )
    {
        this.connectionFactory = connectionFactory;
    }
}
