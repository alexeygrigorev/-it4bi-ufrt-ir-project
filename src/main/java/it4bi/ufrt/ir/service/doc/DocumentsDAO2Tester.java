package it4bi.ufrt.ir.service.doc;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DocumentsDAO2Tester {

	@Autowired 
	private static DocumentsDAO2 docsDAO2;
	
	@Autowired
	private static DocumentsDAO docsDAO22;
	
	public static void main(String[] args) throws SQLException {

		ApplicationContext context = new ClassPathXmlApplicationContext("dataSources.xml");
	 
	        DocumentsDAO2 docsDAO = new DocumentsDAO2( (NamedParameterJdbcTemplate) context.getBean("appJdbcTemplate"));
		
	        docsDAO.test();
	        
	        System.out.println(docsDAO22);
	        
	        docsDAO2.test();
		
	}
}
