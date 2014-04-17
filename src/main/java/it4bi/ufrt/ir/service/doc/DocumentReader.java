package it4bi.ufrt.ir.service.doc;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.microsoft.OfficeParser;
import org.apache.tika.parser.odf.OpenDocumentParser;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

public class DocumentReader {

	public static String readDoc(String filePath, String mime) {
		String text = null;
		
	    try {
	    	InputStream input = new FileInputStream(filePath);
		    BodyContentHandler handler = new BodyContentHandler(Integer.MAX_VALUE);
		    Metadata metadata = new Metadata();
		    Parser autoDetectParser = new AutoDetectParser();
		    
		    autoDetectParser.parse(input, handler, metadata, new ParseContext());
		    
		    /*if(mime.equals("application/pdf")) {
		    	new PDFParser().parse(input, handler, metadata, new ParseContext());
		    }
		    else if(mime.contains("application/vnd.openxmlformats-officedocument") || mime.equals("application/vnd.ms-excel")) {
		    	new OfficeParser().parse(input, handler, metadata, new ParseContext());
		    }
		    else if(mime.contains("application/vnd.oasis.opendocument")) {
		    	new OpenDocumentParser().parse(input, handler, metadata, new ParseContext());
		    }*/
			
			text = handler.toString();
		} catch (IOException | SAXException | TikaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    //System.out.println(text);
		
		
		return text;
	}
}