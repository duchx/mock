package com.souche.optimus.dao.mapping;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.ibatis.session.SqlSession;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.mangofactory.swagger.models.Types;
import com.souche.optimus.common.util.CollectionUtils;
import com.souche.optimus.common.util.ThreadLocalUtil;
import com.souche.optimus.exception.CoreException;

/**
 * 表结构映射类
 * @author hongwm
 * @since 2013-8-21
 */
public class TableMappingFactory {
    /**
     * sqlSession
     */
    private static SqlSession sqlSession;

    private static Map<String,Map<String, TableSchema>> tableSchemasMap =
            new ConcurrentHashMap<String,Map<String, TableSchema>>();

    private static Map<SqlSession, String> dbNames = new ConcurrentHashMap<SqlSession, String>();
    
    /**
     * 类名与表名的映射关系
     */
    private static Map<String, String> tableMap = 
            new ConcurrentHashMap<String, String>();

    public static String getTableName(String classTail) {
        if(tableMap.containsKey(classTail)) {
            return tableMap.get(classTail);
        }
        return classTail;
    }

    public static void setTableNameMap(String className, String tableName) {
        String[] t = className.split("\\.");
        tableMap.put(t[t.length-1], tableName);
        tableMap.put(className, tableName);
    }

    public static TableSchema getTableSchema(String table) {
        SqlSession localSession = (SqlSession) ThreadLocalUtil.getSqlSession();
        if(localSession == null) {
            localSession = sqlSession;
        }
        
        return getTableSchema(table, localSession);
    }

    /**
     * 根据表名，获取表结构的定义
     * @param table
     * @return
     */
    public static TableSchema getTableSchema(String table, SqlSession sqlSession) {
        if(table == null) {
            return null;
        }
        String dbName = getDbName(sqlSession);
        if(dbName == null) {
            dbName = "db";
        }
        Map<String, TableSchema> tableSchemas = tableSchemasMap.get(dbName);
        if(tableSchemas == null) {
            tableSchemas = new ConcurrentHashMap<String, TableSchema>();
            tableSchemasMap.put(dbName, tableSchemas);
        }

        TableSchema schema = tableSchemas.get(table);
        if(schema == null) {
            List<TableColumn> columns = getTableColumns(table, sqlSession);
            if(CollectionUtils.isEmpty(columns)) {
                throw new CoreException("can not find schema for table " + table);
            }

            schema = new TableSchema();
            schema.setTableName(table);
            schema.setColumns(columns);
            tableSchemas.put(table, schema);
        }

        return schema;
    }

    /**
     * 获取数据库连接的库名
     * @param sqlSession
     * @return
     */
    public static String getDbName(SqlSession sqlSession) {
        if(sqlSession == null) {
            sqlSession = TableMappingFactory.sqlSession;
        }

        String dbName = dbNames.get(sqlSession);
        if(dbName != null) {
            return dbName;
        }

        try {
            Connection c = sqlSession.getConfiguration().getEnvironment().getDataSource().getConnection();
            String dbname = c.getCatalog();
            DataSourceUtils.releaseConnection(c, sqlSession.getConfiguration().getEnvironment().getDataSource());
            dbNames.put(sqlSession, dbname);
            return dbname;
        } catch (Exception e) {
        }
        return null;
    }

    public static List<TableColumn> getTableColumns(String table, SqlSession sqlSession) {
    	List<TableColumn> columns = new ArrayList<TableColumn>();
        Connection conn = null;
		try {
			conn = sqlSession.getConfiguration().getEnvironment().getDataSource().getConnection();
		
			DatabaseMetaData dbmd = conn.getMetaData();
			ResultSet rs = dbmd.getColumns(conn.getCatalog(), "%", "mock_data", "%");
			while(rs.next()){
				TableColumn column = new TableColumn();
				column.setField(rs.getString("COLUMN_NAME"));
				column.setType(rs.getString("DATA_TYPE"));
				columns.add(column);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(conn==null)return columns;
			DataSourceUtils.releaseConnection(conn, sqlSession.getConfiguration().getEnvironment().getDataSource());
		}
        return columns;
    }

    public static boolean isPostgresqlConnection(SqlSession sqlSession) {
        DataSource datasource = sqlSession.getConfiguration().getEnvironment().getDataSource();
        if(!(datasource instanceof BasicDataSource)) {
            return false;
        }
        
        String driverName = ((BasicDataSource)sqlSession.getConfiguration().getEnvironment().getDataSource()).getDriverClassName();
        return driverName.contains("postgresql");
    }
    
    public static void loadTableSchemas() {
        List<String> tables = sqlSession.selectList("common.ALL_TABLE");
        for(String table : tables) {
            getTableSchema(table);
        }
    }

    public static void setSqlSession(SqlSession sqlSession) {
        TableMappingFactory.sqlSession = sqlSession;
    }
}
