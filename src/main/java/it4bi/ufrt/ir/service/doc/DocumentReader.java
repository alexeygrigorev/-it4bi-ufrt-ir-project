package it4bi.ufrt.ir.service.doc;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

public class DocumentReader {

	public static String readDoc(String filePath) {
		String text = null;
		
	    try {
	    	InputStream input = new FileInputStream(filePath);
		    BodyContentHandler handler = new BodyContentHandler(Integer.MAX_VALUE);
		    Metadata metadata = new Metadata();
			new PDFParser().parse(input, handler, metadata, new ParseContext());
			text = handler.toString();
		} catch (IOException | SAXException | TikaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    System.out.println(text);
		
		
		return text;
	}
}