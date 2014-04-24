package it4bi.ufrt.ir.service.web;

//import it4bi.ufrt.ir.controller.SearchController;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;

import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;


public class SocialMentionAPI {
	
	//private static final Logger LOGGER = LoggerFactory.getLogger(SocialMentionAPI.class);
	
	private final static String url = "http://api2.socialmention.com/search";
	private final static String format = "xml";
	private final static String language = "en";
	
	


	public static SocialSearchRecords search(String searchQuery, SocialSearchType type) throws SocialSearchException {
			
		String params = buildParamsQuert(format, language, searchQuery, type);	
	    		
		String paramURL = url+params;
	//	LOGGER.info ("trying to connect to Social mention "+paramURL);
		String[] result;
		
		try {
			result = HTTPRequestHelper.sendHttpRequest( paramURL , "POST", null);
		} catch (IOException e) {
			
			throw new SocialSearchException("Error in HTTP layer: "+e.getMessage());
		}
		
	//	LOGGER.info ("results recieved from social mentin");
		
		StringBuilder xmlBuilder = new StringBuilder();
		for(String str: result)
			xmlBuilder.append(str);
		
		SocialMentionXMLResult searchResults =  parseSocialMentionXMLResults(xmlBuilder.toString());
		
	//	LOGGER.info(searchResults.getRecordsCount() + " Records returened");
		

			
		
		return searchResults.getRecords();
		
		

	}
	
	private static String buildParamsQuert(String responseType, String language, String keywords, SocialSearchType type ) throws SocialSearchException  {
		
		StringBuffer params = new StringBuffer();
		params.append("?");
		try {
			params.append( URLEncoder.encode("f", "UTF-8") +"="+  URLEncoder.encode(responseType, "UTF-8"));

		params.append("&" + URLEncoder.encode("lang", "UTF-8") +"="+  URLEncoder.encode(language, "UTF-8"));
		params.append("&" + URLEncoder.encode("q", "UTF-8") +"="+  URLEncoder.encode(keywords, "UTF-8"));
		
		String [] src = getSocialMentionSource(type);
		params.append("&" + URLEncoder.encode(src[0], "UTF-8") +"="+  URLEncoder.encode(src[1], "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return params.toString();
		
	}
	
	private static String [] getSocialMentionSource (SocialSearchType type) throws SocialSearchException{
		switch(type){
		case FACEBOOK: return new String [] {"src[]","facebook"};
		case TWITTER: return new String [] {"src[]","twitter"};
		case VIDEOS: return new String [] {"t","videos"};
		case NEWS: return new String [] {"t","news"};
		default: throw new SocialSearchException ("Invalid social search type");
		
		}
	}
	
	
    private static SocialMentionXMLResult parseSocialMentionXMLResults (String xmlData) throws SocialSearchException{
    	
    	SocialMentionXMLResult recs = new SocialMentionXMLResult();
    	try{
    	JAXBContext jaxbContext = JAXBContext.newInstance(SocialMentionXMLResult.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        
        InputStream xmlStream = new ByteArrayInputStream(xmlData.getBytes("UTF-8"));
        
        recs = (SocialMentionXMLResult) jaxbUnmarshaller.unmarshal( xmlStream );
    	}catch (Exception ex){
    		throw new SocialSearchException("Error in parsing Scoialmention results: "+ex.getMessage());
    	}
        return recs;
		
    }

   
		
		
		
}
