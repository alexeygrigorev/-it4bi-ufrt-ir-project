package it4bi.ufrt.ir.controller;

import it4bi.ufrt.ir.service.doc.DOCUSER_ASSOC;
import it4bi.ufrt.ir.service.doc.DocumentRecord;
import it4bi.ufrt.ir.service.doc.DocumentsDAO;
import it4bi.ufrt.ir.service.doc.Tag;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xml.sax.ContentHandler;


import org.xml.sax.SAXException;
@Component
@Path("/upload")
// TODO: needs renaming
public class UploadController {
	private static final int NOT_FOUND_STATUS = 404;

	private static final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);

	@Autowired
	private DocumentsDAO docsDAO;
	
	@Value("${documents.upload.folder}")
	private String uploadLocation;
	
	@Value("${documents.index}")
	private String indexLocation;

	@Value("${documents.tagsPerDoc}")
	private int tagsPerDoc;
	
	@Value("${documents.score.ownerScore}")
	private float ownerScore;
	
	@Value("${documents.score.likeScore}")
	private float likeScore;
	
	@GET
	@Path("/get/{file}")
	public Response getFile(@PathParam("docID") int docID) {
		
		LOGGER.debug("file download requerst for {}", docID);
		
		File fileToSend = new File(docsDAO.getDocByDocId(docID).getDocPath());
		if (fileToSend.exists()) {
			return Response.ok(fileToSend, MediaType.APPLICATION_OCTET_STREAM).build();
		} else {
			return Response.status(NOT_FOUND_STATUS).build();
		}
	}	
		
	@GET
	@Path("/like")
	@Produces("application/json; charset=UTF-8")
	public Response likeDocument(@QueryParam("docID") int docID, @QueryParam("userID") int userID) {				

		LOGGER.debug("like file. UserID {}; DocID: {}", userID, docID);
		
		docsDAO.insertUserDocsAssociation(docID, userID, DOCUSER_ASSOC.LIKES);
		
		DocumentRecord docRec = docsDAO.getDocByDocId(docID);
		docsDAO.updateTagScores(userID, docRec.getTags(), likeScore);
		
		
		//TODO: ANIL
		String output = "File successfully liked";
		return Response.status(200).entity(output).build();
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
		
		InputStream is = null;
		File f = new File(serverFilePath);
		try {
			is = new FileInputStream(f);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		ContentHandler contenthandler = new BodyContentHandler();
		Metadata metadata = new Metadata();
	    metadata.set(Metadata.RESOURCE_NAME_KEY, f.getName());
	    Parser parser = new AutoDetectParser();
	    // OOXMLParser parser = new OOXMLParser();
	    
		try {
			parser.parse(is, contenthandler, metadata, new ParseContext());
		} catch (IOException | SAXException | TikaException e2) {
			e2.printStackTrace();
		}
		
	    
	    String mime = metadata.get(Metadata.CONTENT_TYPE);
		
		try {
			DocumentRecord documentRecord = new DocumentRecord(documentTitle, serverFilePath, userID, mime);
			
			
			documentRecord.index(indexLocation);
			
			MMapDirectory indexDir = null;
			try {
				indexDir = new MMapDirectory(new File(indexLocation));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			
			// Update Tags
			List<String> tagTexts = extractTags(documentRecord, indexLocation);
			List<Tag> tags = new ArrayList<Tag>();
			// Obtain tags from the doc
			
			for(String tagText : tagTexts) {
				tags.add(new Tag(tagText));
			}
			
			
			
			documentRecord.setTags(tags);
			
			docsDAO.insertDocumentRecord(documentRecord);
			docsDAO.insertUserDocsAssociation(documentRecord.getDocId(),userID,DOCUSER_ASSOC.OWNS);
			docsDAO.updateTags(tags);
			docsDAO.updateTagScores(userID, tags, ownerScore);
			
		} catch (Exception e) {
			e.printStackTrace();
		}		

		String output = "File saved to location: " + serverFilePath;
		return Response.status(200).entity(output).build();
	}

	private void analyseFile(File f) {
		
		
		
	}

	public void getTF(IndexReader reader, int docID, List<Pair<String, Integer>> termFreqs) throws IOException
	{
	    Fields f = reader.getTermVectors(docID);
	    
	    Iterator<String> it = f.iterator();
	    
	    while(it.hasNext()) {
	    	Terms t = f.terms("content");
	    	TermsEnum it2 = t.iterator(null);
	    	
	    	while(it2.next() != null) {
	    		
	    		//System.out.println(it2.term().utf8ToString() + " " + it2.totalTermFreq());
	    		termFreqs.add(new ImmutablePair<String, Integer>(it2.term().utf8ToString(), (int) it2.totalTermFreq()));
	    	}
	    	
	    	break;
	    }
	    
	}
	
	private List<String> extractTags(DocumentRecord docRecord, String indexLocation) {
		
		List<Pair<String, Integer>> termFreqs = new ArrayList<Pair<String,Integer>>();
		List<String> tags = new ArrayList<String>();
		
		try {
			getTF(DirectoryReader.open(MMapDirectory.open(new File(indexLocation))), docRecord.getDocId(), termFreqs);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Collections.sort(termFreqs,  new Comparator<Pair<String, Integer>>() {
			public int compare(Pair<String, Integer> p1, Pair<String, Integer> p2) {
	        	if (p2.getRight() == p1.getRight()) return 0;
	        	else return (p2.getRight() < p1.getRight()) ? -1 : 1; 	        	
	        }
		});

		int ctr = 0;
		for(Pair<String, Integer> tfs : termFreqs) {
			String temp = tfs.getLeft();
			if(isNumerical(temp) == false) {
				ctr++;
				tags.add(temp);
			}
			if(ctr == tagsPerDoc) break;
		}
		
		//TFIDFSimilarity tfidfSIM = new DefaultSimilarity();
		//Map<String, Float> tf_Idf_Weights = new HashMap<>();
		//Map<String, Float> termFrequencies = new HashMap<>();
		
		return tags;
		
	}
	
	public static boolean isNumerical (String input)  
	{  
	   try  
	   {  
	      Integer.parseInt(input);
	      Double.parseDouble(input);
	      Date.parse(input);
	      return true;  
	   }  
	   catch(Exception e)  
	   {  
	      return false;  
	   }  
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
