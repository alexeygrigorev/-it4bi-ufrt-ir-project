package it4bi.ufrt.ir.service.web;


/**
 * Sentiment Analysis 1.1 starting client for Java.
 *
 * In order to run this example, the license key obtained for the Sentiment Analysis API
 * must be included in the key variable. If you don't know your key, check your personal
 * area at Textalytics (https://textalytics.com/personal_area)
 *
 * Once you have the key, edit the parameters and call "javac *.java; java SentimentClient"
 *
 * You can find more information at http://textalytics.com/core/sentiment-1.1-info
 *
 * @author     Textalytics
 * @contact    http://www.textalytics.com (http://www.daedalus.es)
 * @copyright  Copyright (c) 2014, DAEDALUS S.A. All rights reserved.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.HttpURLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;

import org.w3c.dom.*;

/**
 * This class implements POST request to the API
 */ 
public class TextalyticsAPI {
  private static URL url;
  private static String params;
  private static final String api = "http://textalytics.com/core/sentiment-1.1";
  private static final String key = "a986d2dc15fecc28472712cd06207a5b";
 
  
  private static void addParameter (String name, String value) throws UnsupportedEncodingException{
    if (params.length()>0)
      params += "&" + URLEncoder.encode(name, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
    else
      params += URLEncoder.encode(name, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
  }
 
  private static String getResponse() throws IOException {
    String response = ""; 
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setDoOutput(true);
    conn.setDoOutput(true);
    conn.setInstanceFollowRedirects(false);
    conn.setRequestProperty("Accept-Charset", "utf-8");
    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
    conn.setRequestProperty("charset", "utf-8");
    conn.setRequestMethod("POST");
    conn.setUseCaches(false);
    conn.setRequestProperty("Content-Length", "" + Integer.toString(params.getBytes().length));
    try {
      OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
      wr.write(params);
      wr.flush();
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
    }

    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    String line;
    while ((line = rd.readLine()) != null) {
      response += line;
    }
    return response;
   }

 

  /*
   * Retrun a string [] of size 3 with
   * 0:Call Status code
   * 1:Call Status Description
   * 2:Global Sentiment 
   * 
   */
  public static String [] calculateSentiment(String txt) throws SocialSearchException  {
      // We define the variables needed to call the API
   
      DocumentBuilderFactory docBuilderFactory = null;
      DocumentBuilder docBuilder = null;
      Document doc = null;
      Element response_node = null;
      
	  String response = null;
	  NodeList status_list = null;
      Node status =  null;
      NamedNodeMap attributes = null;
      Node code = null;
      
      NodeList score_tags = null;
      
      NodeList sd_tags =null;
      NodeList subjectivities = null;
      NodeList ironies = null;

      String output = "";
      Node score_tag = null;
      Node sd_tag = null;
      Node subjectivity = null;
      Node irony = null;
	  
	  try{
	  url = new URL(api);
	  params = "";

	  
      String model = "en-general"; // es-general/en-general/fr-general
      addParameter("key", key);
      addParameter("txt", txt);
      addParameter("model", model);
      addParameter("of", "xml");
      response = getResponse();
      
      
      // parse the xml result
      docBuilderFactory = DocumentBuilderFactory.newInstance();
       docBuilder = docBuilderFactory.newDocumentBuilder();
      doc = docBuilder.parse(new ByteArrayInputStream(response.getBytes()));
      doc.getDocumentElement().normalize();
      response_node = doc.getDocumentElement();
      
    
        status_list = response_node.getElementsByTagName("status");
         status = status_list.item(0);
         attributes = status.getAttributes();
         code = attributes.item(0);
        if(!code.getTextContent().equals("0")) {
          System.out.println("Not found");
        } else {    
           score_tags = response_node.getElementsByTagName("score_tag");
          
          
           sd_tags = response_node.getElementsByTagName("sd_tag");
          subjectivities = response_node.getElementsByTagName("subjectivity");
           ironies = response_node.getElementsByTagName("irony");

           output = "";
           score_tag = null;
           sd_tag = null;
          subjectivity = null;
           irony = null;
          if(score_tags.getLength()>0)
            score_tag = score_tags.item(0);
          if(sd_tags.getLength()>0)
            sd_tag = sd_tags.item(0);
          if(subjectivities.getLength()>0)
            subjectivity = subjectivities.item(0);
          if(ironies.getLength()>0)
            irony = ironies.item(0);
          if(score_tag != null)
            output += "Global sentiment: " + score_tag.getTextContent();
          if(sd_tag != null)
            output += " (" + sd_tag.getTextContent() + ")";
          output += "\n";
          if(subjectivity != null)
            output += "Subjectivity: " + subjectivity.getTextContent() + "\n";
          if(irony != null)
            output += "Irony: " + irony.getTextContent();
          
          /*
          if(output.isEmpty())
            System.out.println("Not found");
          else
            System.out.println(output);
           */ 
          
          
          
          //aggregate the sentimnt values to Positive,Neutral,Negative
          String [] results = new String [3];
          results[0]= code.getTextContent();
          results [1] = status.getTextContent();
          if(score_tag != null)
        	  results [2] = score_tag.getTextContent();
          else
        	  results [2] = "NEU"; //consider no polarity as neutral 
          
          
          return results;
          
          
        }
      } catch (Exception e) {
        
    	  throw new SocialSearchException("Error is Sentiment Analysis API "+e.getMessage());
      }
      
      return null;
   }

  


}

