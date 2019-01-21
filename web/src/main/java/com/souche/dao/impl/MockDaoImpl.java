package com.souche.dao.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import com.souche.dao.IMockDao;
import com.souche.dao.modal.MockData;
import com.souche.optimus.dao.BasicDao;
import com.souche.optimus.dao.query.QueryObj;
import com.souche.optimus.dao.query.QueryParam;

@Repository("MockDaoImpl")
public class MockDaoImpl implements IMockDao {

    @Autowired
    @Qualifier("basicDaoImpl")
    private BasicDao basicDao;

    @Autowired
    private SqlSession sqlSession;
    
	@Override
	public MockData get(String key) {
		QueryObj queryObj = new QueryObj();
		queryObj.setQuerydo(new MockData());
		queryObj.setQueryParam(new QueryParam("key = #{key}", key));
		/*try {
			Connection conn = sqlSession.getConfiguration().getEnvironment().getDataSource().getConnection();
			DatabaseMetaData dbmd = conn.getMetaData();
			ResultSet rs = dbmd.getColumns(conn.getCatalog(), "%", "mock_data", "%");
			while(rs.next()){
				System.out.println(rs.getString("COLUMN_NAME"));
			}
            DataSourceUtils.releaseConnection(conn, sqlSession.getConfiguration().getEnvironment().getDataSource());
		} catch (SQLException e) {
			e.printStackTrace();
		}*/
		return basicDao.findObjectByQuery(queryObj, MockData.class);
	}

	@Override
	public Long save(MockData mock) {
		return (long)basicDao.insert(mock);
	}

	@Override
	public Integer delete(String key) {
		MockData mock = new MockData();
		mock.setKey(key);
		return basicDao.realDelete(mock);
	}
    
    
}
