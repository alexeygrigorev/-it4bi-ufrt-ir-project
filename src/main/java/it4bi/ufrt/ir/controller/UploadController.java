package it4bi.ufrt.ir.controller;

import it4bi.ufrt.ir.service.doc.DocumentRecord;
import it4bi.ufrt.ir.service.doc.DocumentsDAO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FilenameUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Path("/upload")
// TODO: needs renaming
public class UploadController {
	private static final int NOT_FOUND_STATUS = 404;

	private static final Logger LOGGER = LoggerFactory.getLogger(InfoController.class);

	@Autowired
	private DocumentsDAO docsDAO;
	
	@Value("${documents.upload.folder}")
	private String uploadLocation;

	@GET
	@Path("/get/{file}")
	public Response getFile(@PathParam("file") String file) {
		LOGGER.debug("file download requerst for {}", file);
		File fileToSend = new File(uploadLocation, file);
		if (fileToSend.exists()) {
			return Response.ok(fileToSend, MediaType.APPLICATION_OCTET_STREAM).build();
		} else {
			return Response.status(NOT_FOUND_STATUS).build();
		}
	}
	

	@POST
	@Path("/doc")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(@FormDataParam("file") InputStream fileStream,
			@FormDataParam("file") FormDataContentDisposition fileInfo,
			@FormDataParam("docTitle") String documentTitle,
			@FormDataParam("userID") int userID) {

		LOGGER.debug("uploading file. UserID {}; Doc Title: {}", userID, documentTitle);
		createDirectory(uploadLocation);
		
		String clientFilePath = fileInfo.getFileName();
		String serverFilePath = createServerFilePath(clientFilePath);
		saveFile(fileStream, serverFilePath);

		// TODO: Alexey, is there any way to start indexing after returning the response?
		
		try {
			DocumentRecord documentRecord = new DocumentRecord(documentTitle, serverFilePath,userID);
			docsDAO.insertDocumentRecord(documentRecord);
			documentRecord.index();
		} catch (Exception e) {
			e.printStackTrace();
		}
		

		String output = "File saved to location: " + serverFilePath;
		return Response.status(200).entity(output).build();
	}

	private void createDirectory(String directory) {
		File folder = new File(directory);

		// if the directory does not exist, create it
		if (!folder.exists()) {
			LOGGER.debug("creating directory: {}", directory);
			
			boolean result = folder.mkdir();
			if (result) {
				LOGGER.debug("directory is created");
			} else {
				LOGGER.debug("directory creation failed");
			}
		}
	}

	private String createServerFilePath(String clientFilePath){	
		String clientFileName = FilenameUtils.getName(clientFilePath);
		String serverFileName = clientFileName.replace(" ", "_").replace(":", "_");
		String extension = FilenameUtils.getExtension(serverFileName);
		String baseName = FilenameUtils.getBaseName(serverFileName);		
		
		File document = new File(uploadLocation, serverFileName);
		if (document.exists()) {
			int i = 0;
			do {
				i++;
				document = new File(uploadLocation, baseName + "_" + i + "." + extension);
			} while (document.exists());			
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
	
}
