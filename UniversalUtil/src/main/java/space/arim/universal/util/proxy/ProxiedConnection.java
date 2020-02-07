/* 
 * UniversalUtil, simple utilities for Spigot and BungeeCord
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * UniversalUtil is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * UniversalUtil is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with UniversalUtil. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.universal.util.proxy;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * A parent class for connections which simply redirect, a.k.a. <i>proxy</i>, calls to another connection (the backing connection). <br>
 * Such "connections" do no work themselves, but merely refer calls to their backing connections. <br>
 * <br>
 * However, the proxy connection's additional call layer provides fine tuned control over reads and writes to the backing connection. <br>
 * Programmers may extend this class to utilise this enhanced control. <br>
 * <br>
 * Note that a reference is retained to the backing connection. Changes to the backing connection are reflected in proxied connections.
 * 
 * @author A248
 *
 */
public abstract class ProxiedConnection implements Connection {

	private final Connection original;
	
	/**
	 * Creates a ProxiedConnection based on a backing connection
	 * 
	 * @param original the original, backing connection
	 */
	protected ProxiedConnection(Connection original) {
		this.original = original;
	}
	
	/**
	 * Gets the original connection upon which this ProxiedConnection is based
	 * 
	 * @return the original, backing connection
	 */
	protected Connection getOriginal() {
		return original;
	}
	
	@Override
	public void close() throws SQLException {
		original.close();
	}
	
	@Override
	public boolean isClosed() throws SQLException {
		return original.isClosed();
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return original.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return original.isWrapperFor(iface);
	}

	@Override
	public Statement createStatement() throws SQLException {
		return original.createStatement();
	}

	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return original.prepareStatement(sql);
	}

	@Override
	public CallableStatement prepareCall(String sql) throws SQLException {
		return original.prepareCall(sql);
	}

	@Override
	public String nativeSQL(String sql) throws SQLException {
		return original.nativeSQL(sql);
	}

	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		original.setAutoCommit(autoCommit);
	}

	@Override
	public boolean getAutoCommit() throws SQLException {
		return original.getAutoCommit();
	}

	@Override
	public void commit() throws SQLException {
		original.commit();
	}

	@Override
	public void rollback() throws SQLException {
		original.rollback();
	}

	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		return original.getMetaData();
	}

	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {
		original.setReadOnly(readOnly);
	}

	@Override
	public boolean isReadOnly() throws SQLException {
		return original.isReadOnly();
	}

	@Override
	public void setCatalog(String catalog) throws SQLException {
		original.setCatalog(catalog);
	}

	@Override
	public String getCatalog() throws SQLException {
		return original.getCatalog();
	}

	@Override
	public void setTransactionIsolation(int level) throws SQLException {
		original.setTransactionIsolation(level);
	}

	@Override
	public int getTransactionIsolation() throws SQLException {
		return original.getTransactionIsolation();
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return original.getWarnings();
	}

	@Override
	public void clearWarnings() throws SQLException {
		original.clearWarnings();
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		return original.createStatement(resultSetType, resultSetConcurrency);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return original.prepareStatement(sql, resultSetType, resultSetConcurrency);
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return original.prepareCall(sql, resultSetType, resultSetConcurrency);
	}

	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return original.getTypeMap();
	}

	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		original.setTypeMap(map);
	}

	@Override
	public void setHoldability(int holdability) throws SQLException {
		original.setHoldability(holdability);
	}

	@Override
	public int getHoldability() throws SQLException {
		return original.getHoldability();
	}

	@Override
	public Savepoint setSavepoint() throws SQLException {
		return original.setSavepoint();
	}

	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		return original.setSavepoint(name);
	}

	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		original.rollback(savepoint);
	}

	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		original.releaseSavepoint(savepoint);
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return original.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return original.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return original.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		return original.prepareStatement(sql, autoGeneratedKeys);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		return original.prepareStatement(sql, columnIndexes);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		return original.prepareStatement(sql, columnNames);
	}

	@Override
	public Clob createClob() throws SQLException {
		return original.createClob();
	}

	@Override
	public Blob createBlob() throws SQLException {
		return original.createBlob();
	}

	@Override
	public NClob createNClob() throws SQLException {
		return original.createNClob();
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		return original.createSQLXML();
	}

	@Override
	public boolean isValid(int timeout) throws SQLException {
		return original.isValid(timeout);
	}

	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		original.setClientInfo(name, value);
	}

	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		original.setClientInfo(properties);
	}

	@Override
	public String getClientInfo(String name) throws SQLException {
		return original.getClientInfo(name);
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		return original.getClientInfo();
	}

	@Override
	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		return original.createArrayOf(typeName, elements);
	}

	@Override
	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		return original.createStruct(typeName, attributes);
	}

	@Override
	public void setSchema(String schema) throws SQLException {
		original.setSchema(schema);
	}

	@Override
	public String getSchema() throws SQLException {
		return original.getSchema();
	}

	@Override
	public void abort(Executor executor) throws SQLException {
		original.abort(executor);
	}

	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		original.setNetworkTimeout(executor, milliseconds);
	}

	@Override
	public int getNetworkTimeout() throws SQLException {
		return original.getNetworkTimeout();
	}
	
}
