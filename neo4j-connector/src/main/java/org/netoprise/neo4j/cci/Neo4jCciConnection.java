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
package org.netoprise.neo4j.cci;

import javax.resource.ResourceException;

import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionMetaData;
import javax.resource.cci.ConnectionSpec;
import javax.resource.cci.Interaction;
import javax.resource.cci.LocalTransaction;
import javax.resource.cci.ResultSetInfo;

import org.netoprise.neo4j.Neo4jManagedConnection;
import org.netoprise.neo4j.Neo4jManagedConnectionFactory;

/**
 * Neo4jCciConnection
 *
 * @version $Revision: $
 */
public class Neo4jCciConnection implements Connection
{
   /**
    * Default constructor
    */
   public Neo4jCciConnection()
   {

   }

   /**
    * Default constructor
    * @param connSpec ConnectionSpec
    */
   public Neo4jCciConnection(ConnectionSpec connSpec)
   {

   }

   public Neo4jCciConnection(Neo4jManagedConnection neo4jManagedConnection,
		Neo4jManagedConnectionFactory mcf) {
	// TODO Auto-generated constructor stub
}

/**
    * Initiates close of the connection handle at the application level.
    *
    * @throws ResourceException Exception thrown if close on a connection handle fails.
    */
   public void close() throws ResourceException
   {

   }

   /**
    * Creates an Interaction associated with this Connection. 
    *
    * @return Interaction instance
    * @throws ResourceException Failed to create an Interaction
    */
   public Interaction createInteraction() throws ResourceException
   {
      return null;
   }

   /**
    * Returns an LocalTransaction instance that enables a component to 
    * demarcate resource manager local transactions on the Connection.
    *
    * @return LocalTransaction instance
    * @throws ResourceException Failed to return a LocalTransaction
    * @throws javax.resource.NotSupportedException Demarcation of Resource manager 
    */
   public LocalTransaction getLocalTransaction() throws ResourceException
   {
      return null;
   }

   /**
    * Gets the information on the underlying EIS instance represented through an active connection.
    *
    * @return ConnectionMetaData instance representing information about the EIS instance
    * @throws ResourceException Failed to get information about the connected EIS instance. 
    */
   public ConnectionMetaData getMetaData() throws ResourceException
   {
      return new Neo4jConnectionMetaData();
   }

   /**
    * Gets the information on the ResultSet functionality supported by 
    * a connected EIS instance.
    *
    * @return ResultSetInfo instance
    * @throws ResourceException Failed to get ResultSet related information
    * @throws javax.resource.NotSupportedException ResultSet functionality is not supported
    */
   public ResultSetInfo getResultSetInfo() throws ResourceException
   {
      return null;
   }


}
