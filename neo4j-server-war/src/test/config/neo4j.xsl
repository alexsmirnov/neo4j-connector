<?xml version="1.0" encoding="UTF-8"?>
<!-- See http://www.w3.org/TR/xslt -->


<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ra="urn:jboss:domain:resource-adapters:1.0"
	version="1.0">
	<xsl:output method="xml" indent="yes" />

	<xsl:param name="ra.jndi.name" select="'java:/eis/Neo4j'" />
	<xsl:param name="ra.pool.name" select="'Neo4j'" />
	<xsl:param name="ra.neo4j.dir" select="'${jboss.server.base.dir}/graphdb'" />

	<xsl:variable name="neo4jDefinition">
		<ra:subsystem>
			<ra:resource-adapters>
				<ra:resource-adapter>
					<ra:archive>neo4j-connector.rar</ra:archive>

					<ra:config-property name="dir"><xsl:value-of select="$ra.neo4j.dir"/></ra:config-property>
					<ra:config-property name="xa">true</ra:config-property>

					<ra:transaction-support>XATransaction</ra:transaction-support>

					<ra:connection-definitions>
						<ra:connection-definition
							class-name="com.netoprise.neo4j.Neo4jManagedConnectionFactory"
							jndi-name="java:/eis/Neo4j" pool-name="Neo4j">

							<ra:xa-pool>
								<ra:min-pool-size>2</ra:min-pool-size>
								<ra:max-pool-size>5</ra:max-pool-size>
								<ra:prefill>true</ra:prefill>
							    <ra:flush-strategy>FailingConnectionOnly</ra:flush-strategy>
							    <ra:interleaving>false</ra:interleaving>
							    <ra:no-tx-separate-pools>false</ra:no-tx-separate-pools>
							</ra:xa-pool>

							<ra:security>
								<ra:application />
							</ra:security>
						</ra:connection-definition>
					</ra:connection-definitions>
				</ra:resource-adapter>
			</ra:resource-adapters>
		</ra:subsystem>
	</xsl:variable>

	<!-- replace the old definition with the new -->
	<xsl:template
		match="//ra:subsystem">
		<!-- http://docs.jboss.org/ironjacamar/userguide/1.0/en-US/html/deployment.html#deployingds_descriptor -->
		<xsl:copy-of select="$neo4jDefinition" />
	</xsl:template>

	<!-- get rid of the default driver defs <xsl:template match="//ra:subsystem/ra:datasources/ra:drivers"/> -->

	<!-- Copy everything else. -->
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>
