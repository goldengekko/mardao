README for the mardao Architect's Java DAO generator
Version: 1.20
License: http://www.gnu.org/licenses/lgpl.html

Mardao is a maven plugin, which you configure in your project's pom.xml.
It then generates DAO classes using your annotated domain classes as input.

For support, visit the mardao-usage mailing list at 
https://lists.sourceforge.net/lists/listinfo/mardao-usage

pom.xml example:

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<artifactId>basic-plugin-test</artifactId>
	<packaging>jar</packaging>
	<name>${groupId}::${artifactId}</name>

	<properties>
		<mardao.version>1.20</mardao.version>
		<spring.version>3.0.6.RELEASE</spring.version>
	</properties>

	<build>
		<plugins>
			<plugin>
				<artifactId>maven-compiler-plugin</artifactId>
				<configuration>
					<source>1.6</source>
					<target>1.6</target>
				</configuration>
				<executions>
					<!-- extra compile step of domain classes before generator processing -->
					<execution>
						<id>compile-entities</id>
						<phase>generate-sources</phase>
						<goals>
							<goal>compile</goal>
						</goals>
						<configuration>
							<verbose>false</verbose>
							<fork>true</fork>
							<includes>
								<include>**/domain/*.java</include>
							</includes>
						</configuration>
					</execution>
				</executions>
			</plugin>
			<!--
				two phases: to generate generics (in generate-sources), then
				generate daos (in process-sources)
			-->
			<plugin>
				<groupId>net.sf.mardao</groupId>
				<artifactId>mardao-maven-plugin</artifactId>
				<version>${mardao.version}</version>
				<executions>
					<execution>
						<id>generate-generics</id>
						<phase>generate-sources</phase>
						<goals>
							<goal>generate-sources</goal>
						</goals>
					</execution>
					<execution>
						<id>generate-daos</id>
						<phase>process-sources</phase>
						<goals>
							<goal>process-classes</goal>
						</goals>
					</execution>
				</executions>
				<configuration>
					<!-- default is Spring JDBC 
					<persistenceType>AED</persistenceType>
					-->
					<!--
						Templates are retreived from plugin jar by default:
						<templateFolder>C:/workspaces/Misc/eclipse/Scanner/src/main/resources</templateFolder>
					-->
					<basePackage>net.sf.mardao.test.basic</basePackage>
					<!--
						Scan here for entity classes:
						<classpathElement>target/entity-classes</classpathElement>
					-->
					<!--
						<additionalClasspathElements> <param>c:/develop</param>
						<param>h:/</param> </additionalClasspathElements>
					-->
					<!--
						Override default ${basePackage}.domain
						<domanBasePackage></domanBasePackage>
					-->
					<!--
						Override default ${basePackage}.dao
						<daoBasePackage></daoBasePackage>
					-->
					<!--
						default is transactions-optional:
						<persistenceUnitName>JpaPU2</persistenceUnitName>
					-->
					<!-- default is true: 
					<containerManagedEntityManager>false</containerManagedEntityManager> -->
				</configuration>
			</plugin>
			<plugin>
				<groupId>org.codehaus.mojo</groupId>
				<artifactId>build-helper-maven-plugin</artifactId>
				<version>1.5</version>
				<executions>
					<execution>
						<id>add-source</id>
						<phase>generate-sources</phase>
						<goals>
							<goal>add-source</goal>
						</goals>
						<configuration>
							<sources>
								<source>${basedir}/target/generated-sources/dao</source>
							</sources>
						</configuration>
					</execution>
				</executions>
			</plugin>

			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-source-plugin</artifactId>
				<executions>
					<execution>
						<id>attach-sources</id>
						<goals>
							<goal>jar</goal>
						</goals>
					</execution>
				</executions>
			</plugin>

		</plugins>
	</build>

	<dependencies>
		<dependency>
			<groupId>commons-dbcp</groupId>
			<artifactId>commons-dbcp</artifactId>
			<version>1.2.2</version>
		</dependency>
		<dependency>
			<groupId>log4j</groupId>
			<artifactId>log4j</artifactId>
		</dependency>
		<dependency>
			<groupId>net.sf.mardao</groupId>
			<artifactId>mardao-api</artifactId>
			<version>${mardao.version}</version>
		</dependency>
		<dependency>
			<groupId>org.aspectj</groupId>
			<artifactId>aspectjweaver</artifactId>
			<version>1.6.2</version>
			<scope>compile</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-context</artifactId>
			<version>${spring.version}</version>
			<scope>compile</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-jdbc</artifactId>
			<version>${spring.version}</version>
			<scope>compile</scope>
		</dependency>
		<dependency>
			<groupId>org.apache.geronimo.specs</groupId>
			<artifactId>geronimo-jpa_3.0_spec</artifactId>
			<version>1.1.1</version>
		</dependency>
		<!--  Test scope dependencies: -->
		<dependency>
			<groupId>junit</groupId>
			<artifactId>junit</artifactId>
		</dependency>
		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
			<version>5.1.10</version>
			<scope>test</scope>
		</dependency>
	</dependencies>
</project>
================================================================================
Release History

Release 1.20 Overview:
Support for @Basic arrays, rename to mardao-maven-plugin
Release date: 2012-03-19

New features:
Support for @Basic arrays

Fixed bugs:

--------------------------------------------------------------------------------
Release 1.17 Overview:
Support for Android DAO generation, based on SQLiteDatabase and SQLiteHelper
Release date: 2011-12-18
You set the plugin configuration

<persistenceType>Android</persistenceType>

and use the dependency

<dependency>
	<groupId>net.sf.mardao</groupId>
	<artifactId>mardao-android</artifactId>
	<version>${mardao.version}</version>
<dependency>

to use for Android.

New features:
Support for Android

Fixed bugs:

--------------------------------------------------------------------------------
Release 1.9 Overview:
Good support for AppEngine low-level API, and parent keys.
You set
<persistenceType>AED</persistenceType>
to generate for AppEngine low-level.

New features:

Fixed bugs:

--------------------------------------------------------------------------------
Release 1.4 Overview:
One important Date bugfix, and support for Expression queries

New features:
3093502 	protected findBy(Expression... ) 	Closed 	2010-10-23 	f94os 	5 

Fixed bugs:
3093501 	Use ResultSet.getTimestamp() instead of getDate() 	Closed 	2010-10-23 	f94os 	Fixed 	7

--------------------------------------------------------------------------------
Release 1.3 Overview:
Only one bugfix, for findBy(Map args) methods

New features in 1.3:

Fixed bugs in 1.3:
3038659  	 findBy(Map args) does not find entities

--------------------------------------------------------------------------------
Release 1.2 Overview:
Only one bugfix for Eclipse IDE

New features in 1.2:

Fixed bugs in 1.2:
3017379  	 Generated source does not compile in java 1.5 (Eclipse IDE only?)

--------------------------------------------------------------------------------
Release 1.1 Overview:
Support @Table and @Column.
Generate per-table CREATE scripts
Use Column names instead of Entity attribute names
More JUnit tests

New features in 1.1:
3010142  	 @Column(name="currentEmployerID")
3010141 	 @Table(name="tblEmployee")

Fixed bugs in 1.1:
3011930  	 Use column names in AbstractDao, not attribute names 

--------------------------------------------------------------------------------
Version: 1.0 Initial release.