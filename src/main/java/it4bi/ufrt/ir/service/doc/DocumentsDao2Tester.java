package it4bi.ufrt.ir.service.doc;

import it4bi.ufrt.ir.service.users.UsersDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DocumentsDao2Tester {

	public static void main(String[] args) throws SQLException {

		ApplicationContext context = new ClassPathXmlApplicationContext("dataSources.xml");

		DocumentsDao docsDAO = new DocumentsDao(
				(NamedParameterJdbcTemplate) context.getBean("appJdbcTemplate"));
		UsersDAO usersDAO = new UsersDAO((NamedParameterJdbcTemplate) context.getBean("appJdbcTemplate"));

		List<Tag> tags = new ArrayList<Tag>();
		tags.add(new Tag("Aa"));
		tags.add(new Tag("Bb"));

		DocumentRecord docRec = new DocumentRecord("title", 134, "app/pdf");
		docRec.setTags(tags);
		docRec.setDocPath("path");

		docsDAO.insertDocumentRecord(docRec);

		docsDAO.insertUserDocAssociation(7656, 24543, DOCUSER_ASSOC_TYPE.OWNS);
		docsDAO.insertUserDocAssociation(1826, 13543, DOCUSER_ASSOC_TYPE.LIKES);

	}
}
