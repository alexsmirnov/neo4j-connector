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
package org.netoprise.neo4j;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.UUID;
import java.util.logging.Logger;

import javax.annotation.Resource;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.Operation;
import org.jboss.dmr.ModelNode;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.ResourceAdapterArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.graphdb.GraphDatabaseService;

import com.netoprise.neo4j.Neo4jManagedConnection;
import com.netoprise.neo4j.Neo4jManagedConnectionFactory;
import com.netoprise.neo4j.Neo4jResourceAdapter;
import com.netoprise.neo4j.connection.Neo4JConnectionFactory;
import com.netoprise.neo4j.connection.Neo4JConnectionFactoryImpl;
import com.netoprise.neo4j.connection.Neo4JConnectionImpl;

/**
 * ConnectorTestCase
 * 
 * @version $Revision: $
 */
@RunWith(Arquillian.class)
public class ConnectorTestCase {
	private static Logger log = Logger.getLogger("ConnectorTestCase");

	private static String deploymentName = "neo4j-connector";

	/**
	 * Define the deployment
	 * 
	 * @return The deployment archive
	 */
	@Deployment
	public static ResourceAdapterArchive createDeployment() {
		File connector = new File("target/" + deploymentName + ".rar");
		ResourceAdapterArchive raa = ShrinkWrap.createFromZipFile(
				ResourceAdapterArchive.class, connector);
		// JavaArchive ja = ShrinkWrap.create(JavaArchive.class,
		// UUID.randomUUID().toString() + ".jar");
		// ja.addClasses(Neo4jResourceAdapter.class,
		// Neo4jManagedConnectionFactory.class, Neo4jManagedConnection.class,
		// Neo4JConnectionFactory.class, Neo4JConnectionFactoryImpl.class,
		// GraphDatabaseService.class,Neo4JConnectionImpl.class);
		// raa.addAsLibrary(ja);

		return raa;
	}

	/** Resource */
	@Resource(mappedName = "java:/eis/ConnectorTestCase")
	private Neo4JConnectionFactory connectionFactory;

	/**
	 * Test Basic
	 * 
	 * @exception Throwable
	 *                Thrown if case of an error
	 */
	@Test
	@RunAsClient
	public void testBasic() throws Throwable {
		ModelControllerClient controllerClient = null;
		try {
			controllerClient = ModelControllerClient.Factory.create(
					"localhost", 9999);
			ModelNode address = new ModelNode();
            address.add("subsystem", "jca");
//            address.add("security-domain", "other");
			ModelNode operation = new ModelNode();
			operation.get("operation").set("read-operation-names");
            operation.get("address").set(address);
            // TODO - configure JCA here.
			ModelNode result = controllerClient.execute(operation);
			assertEquals("success", result.get("outcome").asString());
		} finally {
			if (null != controllerClient) {
				controllerClient.close();
			}
		}
//		assertNotNull(connectionFactory);
//		GraphDatabaseService connection = connectionFactory.getConnection();
//		assertNotNull(connection);
//		connection.shutdown();
	}

}
