package it4bi.ufrt.ir.controller;

import java.io.File;

import it4bi.ufrt.ir.service.doc.DocumentRecord;
import it4bi.ufrt.ir.service.doc.DocumentsDAO;
import it4bi.ufrt.ir.service.doc.DocumentsService;
import it4bi.ufrt.ir.service.users.UsersService;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.ws.spi.http.HttpContext;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

@Component
@Path("/upload")
public class UploadController {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(InfoController.class);
	private static final String UPLOAD_LOCATION = "C://IRDocs/";

	@POST
	@Path("/doc")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(@FormDataParam("file") InputStream fileStream,
			@FormDataParam("file") FormDataContentDisposition fileInfo,
			@FormDataParam("docTitle") String documentTitle,
			@FormDataParam("userID") int userID) {

		LOGGER.debug("uploading file. UserID {}; Doc Title: {}", userID, documentTitle);
		createDirectory(UPLOAD_LOCATION);
		
		String clientFilePath = fileInfo.getFileName();
		String serverFilePath = createServerFilePath(clientFilePath);
		saveFile(fileStream, serverFilePath);

		// TODO: Alexey, is there any way to start indexing after returning the response?
		indexFile(userID, documentTitle, serverFilePath);

		String output = "File saved to location: " + serverFilePath;
		return Response.status(200).entity(output).build();
	}

	private void createDirectory(String directory) {
		File folder = new File(directory);

		// if the directory does not exist, create it
		if (!folder.exists()) {
			LOGGER.debug("creating directory: " + directory);
			
			boolean result = folder.mkdir();
			if (result) {
				LOGGER.debug("directory is created");
			}
			else {
				LOGGER.debug("directory creation failed");
			}
		}
	}

	private String createServerFilePath(String clientFilePath){	
		String clientFileName = FilenameUtils.getName(clientFilePath);
		String serverFileName = clientFileName.replace(" ", "_").replace(":", "_");
		String extension = FilenameUtils.getExtension(serverFileName);
		String baseName = FilenameUtils.getBaseName(serverFileName);		
		
		File document = new File(UPLOAD_LOCATION + serverFileName);
		if (document.exists()){
			int i = 0;
			do
			{				
				document = new File(UPLOAD_LOCATION + baseName + "_" + ++i + "." + extension);
			}
			while (document.exists());			
		}
		return document.getAbsolutePath();		
	}
	
 	private void saveFile(InputStream uploadedInputStream, String serverLocation) {
		try {
			int read = 0;
			byte[] bytes = new byte[1024];

			OutputStream outpuStream = new FileOutputStream(new File(serverLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				outpuStream.write(bytes, 0, read);
			}
			outpuStream.flush();
			outpuStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void indexFile(int userID, String documentTitle, String filePath){
		// TODO: Anil, its your turn.
		
		DocumentRecord documentRecord = new DocumentRecord(documentTitle, filePath);
		DocumentsDAO.insertDocumentRecord(documentRecord);
		
		try {
			// documentRecord.indexDocument();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
}
