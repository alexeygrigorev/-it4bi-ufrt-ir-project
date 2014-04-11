package it4bi.ufrt.ir.service;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:dataSources.xml")
public class DbConnectionTest {

	@Autowired
	@Qualifier("appDataSource")
	DataSource appDataSource;

	@Autowired
	@Qualifier("dwhDataSource")
	DataSource dwhDataSource;

	@Test
	public void tesAppDbConnection() throws Exception {
		Connection connection = appDataSource.getConnection();
		DatabaseMetaData metaData = connection.getMetaData();
		assertEquals("Microsoft SQL Server", metaData.getDatabaseProductName());
		connection.close();
	}

	@Test
	public void testDwhConnection() throws Exception {
		Connection connection = dwhDataSource.getConnection();
		DatabaseMetaData metaData = connection.getMetaData();
		assertEquals("Microsoft SQL Server", metaData.getDatabaseProductName());
		connection.close();
	}

}
