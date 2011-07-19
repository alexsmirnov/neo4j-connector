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

import javax.annotation.Resource;
import javax.ejb.EJB;
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
import org.jboss.osgi.testing.ManifestBuilder;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.ResourceAdapterArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.graphdb.GraphDatabaseService;

import com.netoprise.neo4j.connection.Neo4JConnectionFactory;

/**
 * ConnectorTest
 * 
 * @version $Revision: $
 */
@RunWith(Arquillian.class)
public class ConnectorTest {

	private static String deploymentName = "neo4j-connector.rar";


	@Inject
	@EJB
	private Neo4jClient client;
	/**
	 * Define the deployment
	 * 
	 * @return The deployment archive
	 */
	@Deployment(name="neo4j-connector",order=1,testable=false)
	public static ResourceAdapterArchive createDeployment() {
		File connector = new File("target/" + deploymentName);
		ResourceAdapterArchive raa = ShrinkWrap.createFromZipFile(
				ResourceAdapterArchive.class, connector);
		raa.addAsManifestResource("ironjacamar.xml");
		return raa;
	}

	   @Deployment(name="test",order=2)
	   public static WebArchive createTestArchive() {
	      return ShrinkWrap.create(WebArchive.class, "test.war")
	         .addClasses(Neo4jClient.class)
	         .addAsManifestResource(
	            EmptyAsset.INSTANCE, 
	            ArchivePaths.create("beans.xml"))
	         .addAsManifestResource(createManifest(), "MANIFEST.MF");
	   }


	private static Asset createManifest() {
		return ManifestBuilder.newInstance().addManifestHeader("Dependencies", "deployment."+deploymentName+" export services");
	}

	/**
	 * Test Basic
	 * 
	 * @exception Throwable
	 *                Thrown if case of an error
	 */
	@Test
	@OperateOnDeployment("test")
	public void testBasic() throws Throwable {
//		ModelControllerClient controllerClient = null;
//		try {
//			controllerClient = ModelControllerClient.Factory.create(
//					"localhost", 9999);
//			ModelNode address = new ModelNode();
//            address.add("subsystem", "jca");
////            address.add("security-domain", "other");
//			ModelNode operation = new ModelNode();
//			operation.get("operation").set("read-children-types");
//            operation.get("address").set(address);
//            // TODO - configure JCA here.
//			ModelNode result = controllerClient.execute(operation);
//			assertEquals("success", result.get("outcome").asString());
//			System.out.println("Operation result: "+result.toString());
//		} finally {
//			if (null != controllerClient) {
//				controllerClient.close();
//			}
//		}
		Neo4JConnectionFactory connectionFactory = client.getConnectionFactory();
		assertNotNull(connectionFactory);
//		GraphDatabaseService connection = connectionFactory.getConnection();
//		assertNotNull(connection);
//		connection.shutdown();
	}

	@Test
	@OperateOnDeployment("test")
	public void helloClient() throws Exception {
		assertEquals("Hello world",client.sayHello("world"));
	}
	
	@Test
	@OperateOnDeployment("test")
	public void listJNDI(){
		try {
			Context context = new InitialContext();
			System.out.println("Context namespace: " + context.getNameInNamespace());
			NamingEnumeration<NameClassPair> content = context.list("comp");
			while (content.hasMoreElements()) {
				NameClassPair nameClassPair = (NameClassPair) content
						.nextElement();
				System.out.println("Name :"+nameClassPair.getName()+" with type:"+nameClassPair.getClassName());
			}
		} catch (NamingException e) {
			throw new RuntimeException(e);
		}
	}
}
