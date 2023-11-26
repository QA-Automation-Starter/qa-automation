/*
 * Copyright 2023 Adrian Herscu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.aherscu.qa.jgiven.commons.utils;

import static dev.aherscu.qa.jgiven.commons.utils.UnitilsJUnitTest.*;

import org.testng.annotations.*;
import org.unitils.dbunit.annotation.*;
import org.unitils.dbunit.datasetloadstrategy.impl.*;

import lombok.extern.slf4j.*;

@DataSet(loadStrategy = InsertLoadStrategy.class)
@Slf4j
public class UnitilsTestNGTest extends UnitilsTestNG {
    @Test
    public void shouldUseDb() {
        assertExistenceOfInitialAndDataSetValues();
    }

    // ISSUE when running in parallel=methods mode fails with:
    /*
    org.unitils.core.UnitilsException: Error while performing database update: create table "APP"."DBMAINTAIN_SCRIPTS" ( FILE_NAME VARCHAR(150), VERSION VARCHAR(25), FILE_LAST_MODIFIED_AT BIGINT, CHECKSUM VARCHAR(50), EXECUTED_AT VARCHAR(20), SUCCEEDED BIGINT )

	at org.unitils.database.DatabaseModule$DatabaseTestListener.beforeTestSetUp(DatabaseModule.java:477)
	at org.unitils.core.Unitils$UnitilsTestListener.beforeTestSetUp(Unitils.java:273)
	at dev.aherscu.qa.jgiven.commons.utils.UnitilsTestNG.unitilsBeforeTestSetUp_aroundBody4(UnitilsTestNG.java:179)
	at dev.aherscu.qa.jgiven.commons.utils.UnitilsTestNG.unitilsBeforeTestSetUp_aroundBody5$advice(UnitilsTestNG.java:115)
	at dev.aherscu.qa.jgiven.commons.utils.UnitilsTestNG.unitilsBeforeTestSetUp(UnitilsTestNG.java:1)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:566)
	at org.testng.internal.invokers.MethodInvocationHelper.invokeMethod(MethodInvocationHelper.java:139)
	at org.testng.internal.invokers.MethodInvocationHelper.invokeMethodConsideringTimeout(MethodInvocationHelper.java:69)
	at org.testng.internal.invokers.ConfigInvoker.invokeConfigurationMethod(ConfigInvoker.java:390)
	at org.testng.internal.invokers.ConfigInvoker.invokeConfigurations(ConfigInvoker.java:325)
	at org.testng.internal.invokers.TestInvoker.runConfigMethods(TestInvoker.java:810)
	at org.testng.internal.invokers.TestInvoker.invokeMethod(TestInvoker.java:577)
	at org.testng.internal.invokers.TestInvoker.invokeTestMethod(TestInvoker.java:227)
	at org.testng.internal.invokers.MethodRunner.runInSequence(MethodRunner.java:50)
	at org.testng.internal.invokers.TestInvoker$MethodInvocationAgent.invoke(TestInvoker.java:957)
	at org.testng.internal.invokers.TestInvoker.invokeTestMethods(TestInvoker.java:200)
	at org.testng.internal.invokers.TestMethodWorker.invokeTestMethods(TestMethodWorker.java:148)
	at org.testng.internal.invokers.TestMethodWorker.run(TestMethodWorker.java:128)
	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
	at java.base/java.lang.Thread.run(Thread.java:829)
Caused by: org.unitils.core.UnitilsException: Error while performing database update: create table "APP"."DBMAINTAIN_SCRIPTS" ( FILE_NAME VARCHAR(150), VERSION VARCHAR(25), FILE_LAST_MODIFIED_AT BIGINT, CHECKSUM VARCHAR(50), EXECUTED_AT VARCHAR(20), SUCCEEDED BIGINT )
	at org.unitils.core.dbsupport.DefaultSQLHandler.executeUpdateAndCommit(DefaultSQLHandler.java:149)
	at org.unitils.dbmaintainer.version.impl.DefaultExecutedScriptInfoSource.createExecutedScriptsTable(DefaultExecutedScriptInfoSource.java:421)
	at org.unitils.dbmaintainer.version.impl.DefaultExecutedScriptInfoSource.checkExecutedScriptsTable(DefaultExecutedScriptInfoSource.java:373)
	at org.unitils.dbmaintainer.version.impl.DefaultExecutedScriptInfoSource.getExecutedScripts(DefaultExecutedScriptInfoSource.java:185)
	at org.unitils.dbmaintainer.DBMaintainer.updateDatabase(DBMaintainer.java:235)
	at org.unitils.database.DataSourceWrapper.updateDatabase(DataSourceWrapper.java:143)
	at org.unitils.database.DataSourceWrapper.createDataSource(DataSourceWrapper.java:119)
	at org.unitils.database.DataSourceWrapper.getDataSourceAndActivateTransactionIfNeeded(DataSourceWrapper.java:162)
	at org.unitils.database.DataSourceWrapper.getTransactionalDataSourceAndActivateTransactionIfNeeded(DataSourceWrapper.java:100)
	at org.unitils.database.DatabaseModule.getDataSource(DatabaseModule.java:346)
	at org.unitils.database.DatabaseModule.injectDataSource(DatabaseModule.java:301)
	at org.unitils.database.DatabaseModule$DatabaseTestListener.beforeTestSetUp(DatabaseModule.java:475)
	... 23 more
Caused by: java.sql.SQLException: Table/View 'DBMAINTAIN_SCRIPTS' already exists in Schema 'APP'.
	at org.apache.derby.impl.jdbc.SQLExceptionFactory.getSQLException(SQLExceptionFactory.java:115)
	at org.apache.derby.impl.jdbc.Util.generateCsSQLException(Util.java:230)
	at org.apache.derby.impl.jdbc.TransactionResourceImpl.wrapInSQLException(TransactionResourceImpl.java:431)
	at org.apache.derby.impl.jdbc.TransactionResourceImpl.handleException(TransactionResourceImpl.java:360)
	at org.apache.derby.impl.jdbc.EmbedConnection.handleException(EmbedConnection.java:2405)
	at org.apache.derby.impl.jdbc.ConnectionChild.handleException(ConnectionChild.java:88)
	at org.apache.derby.impl.jdbc.EmbedStatement.executeStatement(EmbedStatement.java:1436)
	at org.apache.derby.impl.jdbc.EmbedStatement.execute(EmbedStatement.java:710)
	at org.apache.derby.impl.jdbc.EmbedStatement.executeLargeUpdate(EmbedStatement.java:183)
	at org.apache.derby.impl.jdbc.EmbedStatement.executeUpdate(EmbedStatement.java:172)
	at com.zaxxer.hikari.pool.ProxyStatement.executeUpdate(ProxyStatement.java:119)
	at com.zaxxer.hikari.pool.HikariProxyStatement.executeUpdate(HikariProxyStatement.java)
	at org.unitils.core.dbsupport.DefaultSQLHandler.executeUpdateAndCommit(DefaultSQLHandler.java:142)
	... 34 more
Caused by: ERROR X0Y32: Table/View 'DBMAINTAIN_SCRIPTS' already exists in Schema 'APP'.
	at org.apache.derby.shared.common.error.StandardException.newException(StandardException.java:300)
	at org.apache.derby.shared.common.error.StandardException.newException(StandardException.java:295)
	at org.apache.derby.impl.sql.catalog.DataDictionaryImpl.duplicateDescriptorException(DataDictionaryImpl.java:2038)
	at org.apache.derby.impl.sql.catalog.DataDictionaryImpl.addDescriptor(DataDictionaryImpl.java:2028)
	at org.apache.derby.impl.sql.execute.CreateTableConstantAction.executeConstantAction(CreateTableConstantAction.java:241)
	at org.apache.derby.impl.sql.execute.MiscResultSet.open(MiscResultSet.java:61)
	at org.apache.derby.impl.sql.GenericPreparedStatement.executeStmt(GenericPreparedStatement.java:472)
	at org.apache.derby.impl.sql.GenericPreparedStatement.execute(GenericPreparedStatement.java:351)
	at org.apache.derby.impl.jdbc.EmbedStatement.executeStatement(EmbedStatement.java:1344)
	... 40 more
     */
    @Test
    public void shouldUseDbAgain() {
        assertExistenceOfInitialAndDataSetValues();
    }
}
