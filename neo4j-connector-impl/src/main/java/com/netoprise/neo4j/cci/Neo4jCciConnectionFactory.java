/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.netoprise.neo4j.cci;

import javax.naming.NamingException;
import javax.naming.Reference;

import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.ConnectionSpec;
import javax.resource.cci.RecordFactory;
import javax.resource.cci.ResourceAdapterMetaData;
import javax.resource.spi.ConnectionManager;


/**
 * Neo4jCciConnectionFactory
 *
 * @version $Revision: $
 */
public class Neo4jCciConnectionFactory implements ConnectionFactory
{
   private Reference reference;

   /**
    * Default constructor
    */
   public Neo4jCciConnectionFactory()
   {

   }

   /**
    * Default constructor
    * @param cxManager ConnectionManager
    */
   public Neo4jCciConnectionFactory(ConnectionManager cxManager)
   {

   }

   /**
    * Gets a connection to an EIS instance. 
    *
    * @return Connection instance the EIS instance.
    * @throws ResourceException Failed to get a connection to
    */
   public Connection getConnection() throws ResourceException
   {
      return new Neo4jCciConnection(new Neo4jConnectionSpec());
   }

   /**
    * Gets a connection to an EIS instance. 
    *
    * @param connSpec Connection parameters and security information specified as ConnectionSpec instance
    * @return Connection instance the EIS instance.
    * @throws ResourceException Failed to get a connection to
    */
   public Connection getConnection(ConnectionSpec connSpec) throws ResourceException
   {
      return new Neo4jCciConnection(connSpec);
   }

   /**
    * Gets metadata for the Resource Adapter. 
    *
    * @return ResourceAdapterMetaData instance
    * @throws ResourceException Failed to get metadata information 
    */
   public ResourceAdapterMetaData getMetaData() throws ResourceException
   {
      return new Neo4jRaMetaData();
   }

   /**
    * Gets a RecordFactory instance.
    *
    * @return RecordFactory instance
    * @throws ResourceException Failed to create a RecordFactory
    * @throws javax.resource.NotSupportedException Operation not supported
    */
   public RecordFactory getRecordFactory() throws ResourceException
   {
      return null;
   }

   /**
    * Get the Reference instance.
    *
    * @return Reference instance
    */
   public Reference getReference() throws NamingException
   {
      return reference;
   }

   /**
    * Set the Reference instance.
    *
    * @param reference A Reference instance
    */
   public void setReference(Reference reference)
   {
      this.reference = reference;
   }


}
