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
package org.netoprise.neo4j.connection;

import java.util.logging.Logger;

import org.netoprise.neo4j.Neo4jManagedConnection;
import org.netoprise.neo4j.Neo4jManagedConnectionFactory;

/**
 * Neo4JConnectionImpl
 *
 * @version $Revision: $
 */
public class Neo4JConnectionImpl implements Neo4JConnection
{
   /** The logger */
   private static Logger log = Logger.getLogger("Neo4JConnectionImpl");

   /** ManagedConnection */
   private Neo4jManagedConnection mc;

   /** ManagedConnectionFactory */
   private Neo4jManagedConnectionFactory mcf;

   /**
    * Default constructor
    * @param mc Neo4JManagedConnection
    * @param mcf Neo4JManagedConnectionFactory
    */
   public Neo4JConnectionImpl(Neo4jManagedConnection mc, Neo4jManagedConnectionFactory mcf)
   {
      this.mc = mc;
      this.mcf = mcf;
   }

   /**
    * Call createNode
    * @param name String
    * @return String
    */
   public String createNode(String name)
   {
      log.info("createNode()");
      return null;

   }

   /**
    * Close
    */
   public void close()
   {
//      mc.closeHandle(this);
   }

}
