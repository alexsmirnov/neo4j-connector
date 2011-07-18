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
import static junit.framework.Assert.assertNotNull;

import java.io.File;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.dmr.ModelNode;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.ResourceAdapterArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * ConnectorTestCase
 * 
 * @version $Revision: $
 */
@RunWith(Arquillian.class)
public class ConnectorTestCase {

	private static String deploymentName = "neo4j-connector";

	   /** Resource */
//	@Resource(mappedName = "java:/eis/Neo4j")
//	private Neo4JConnectionFactory connectionFactory;

	@Inject
	private Neo4jClient client;
	/**
	 * Define the deployment
	 * 
	 * @return The deployment archive
	 */
	@Deployment(name="neo4j-connector",order=1,testable=false)
	public static ResourceAdapterArchive createDeployment() {
		File connector = new File("target/" + deploymentName + ".rar");
		ResourceAdapterArchive raa = ShrinkWrap.createFromZipFile(
				ResourceAdapterArchive.class, connector);
		return raa;
	}

	   @Deployment(name="test",order=2)
	   public static WebArchive createTestArchive() {
	      return ShrinkWrap.create(WebArchive.class, "test.war")
	         .addClasses(Neo4jClient.class)
	         .addAsManifestResource(
	            EmptyAsset.INSTANCE, 
	            ArchivePaths.create("beans.xml")); 
	   }


	/**
	 * Test Basic
	 * 
	 * @exception Throwable
	 *                Thrown if case of an error
	 */
	@Test
	@RunAsClient
	@OperateOnDeployment("neo4j-connector")
	public void testBasic() throws Throwable {
		ModelControllerClient controllerClient = null;
		try {
			controllerClient = ModelControllerClient.Factory.create(
					"localhost", 9999);
			ModelNode address = new ModelNode();
            address.add("subsystem", "jca");
//            address.add("security-domain", "other");
			ModelNode operation = new ModelNode();
			operation.get("operation").set("read-children-types");
            operation.get("address").set(address);
            // TODO - configure JCA here.
			ModelNode result = controllerClient.execute(operation);
			assertEquals("success", result.get("outcome").asString());
			System.out.println("Operation result: "+result.toString());
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

	@Test
	@OperateOnDeployment("test")
	public void helloClient() throws Exception {
		assertNotNull(client.getConnectionFactory());
		System.out.println("ConnectionClass: "+client.getConnectionFactory().getClass().getName());
		assertEquals("Hello world",client.sayHello("world"));
	}
	
	@Test
	@OperateOnDeployment("test")
	public void listJNDI(){
		try {
			Context context = new InitialContext();
			System.out.println("Context namespace: " + context.getNameInNamespace());
			NamingEnumeration<NameClassPair> content = context.list("eis");
			while (content.hasMoreElements()) {
				NameClassPair nameClassPair = (NameClassPair) content
						.nextElement();
				System.out.println("Name :"+nameClassPair.getName()+" with type:"+nameClassPair.getClassName());
			}
			Object object = context.lookup("eis/Neo4j");
			System.out.println(object.getClass().getName());
		} catch (NamingException e) {
			throw new RuntimeException(e);
		}
	}

}
