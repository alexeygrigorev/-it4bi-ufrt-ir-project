package it4bi.ufrt.ir.controller;

import it4bi.ufrt.ir.service.doc.DocumentRecord;
import it4bi.ufrt.ir.service.doc.DocumentsDAO;
import it4bi.ufrt.ir.service.doc.DocumentsService;
import it4bi.ufrt.ir.service.users.UsersService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/upload")

public class UploadController {
	private static final Logger LOGGER = LoggerFactory.getLogger(InfoController.class);

	@Autowired
	private DocumentsService documents;
		
	@GET
	@Path("/doc")
	@Produces("application/json; charset=UTF-8")
	public void upload(@QueryParam("u") int userID, @QueryParam("title") String docTitle) {
		
		//Implement upload logic here. Once file is uploaded somewhere in server, path to the file should be known.
		
		String filepath = null;
		
		DocumentRecord documentRecord = new DocumentRecord(docTitle, filepath);
		
		DocumentsDAO.insertDocumentRecord(documentRecord);
		try {
			documentRecord.indexDocument();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
}
