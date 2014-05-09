package it4bi.ufrt.ir.service.doc;

import static org.junit.Assert.assertEquals;
import it4bi.ufrt.ir.business.UserDatabase;
import it4bi.ufrt.ir.service.users.User;
import it4bi.ufrt.ir.service.users.UsersDAO;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DocumentsDAO2Tester {

	
	public static void main(String[] args) throws SQLException {

		ApplicationContext context = new ClassPathXmlApplicationContext("dataSources.xml");
	 
	        DocumentsDAO2 docsDAO = new DocumentsDAO2( (NamedParameterJdbcTemplate) context.getBean("appJdbcTemplate"));
	        UsersDAO usersDAO = new UsersDAO( (NamedParameterJdbcTemplate) context.getBean("appJdbcTemplate"));
	        
	        for(User user : UserDatabase.getUsers()) {
	        	usersDAO.insertUser(user);
	        }
	        
	        
	        
	        List<Tag> tags = new ArrayList<Tag>();
	        tags.add(new Tag("Aa"));
	        tags.add(new Tag("Bb"));
	        
	        DocumentRecord docRec = new DocumentRecord("title", 134, "app/pdf");
	        docRec.setTags(tags);
	        docRec.setDocPath("path");
	        
	        docsDAO.insertDocumentRecord(docRec);
	        
	        docsDAO.insertUserDocAssociation(7656, 24543, DOCUSER_ASSOC_TYPE.OWNS);
	        docsDAO.insertUserDocAssociation(1826, 13543, DOCUSER_ASSOC_TYPE.LIKES);
	        
	        //docsDAO.updateUserTagScore(3826, 34, 2.4f);
	        //docsDAO.updateUserTagScore(3826, 34, -0.2f);
	        
	        
	        
	        
		
	}
}
