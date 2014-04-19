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
  private URL url;
  String params;
 
  public TextalyticsAPI (String api) throws MalformedURLException{
    url = new URL(api);
    params="";
  }
  
  public void addParameter (String name, String value) throws UnsupportedEncodingException{
    if (params.length()>0)
      params += "&" + URLEncoder.encode(name, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
    else
      params += URLEncoder.encode(name, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
  }
 
  public String getResponse() throws IOException {
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

  public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
      // We define the variables needed to call the API
      String api = "http://textalytics.com/core/sentiment-1.1";
      String key = "a986d2dc15fecc28472712cd06207a5b";
      String txt = "UFRT is a good school";
      String model = "en-general"; // es-general/en-general/fr-general
      
      TextalyticsAPI post = new TextalyticsAPI (api);
      post.addParameter("key", key);
      post.addParameter("txt", txt);
      post.addParameter("model", model);
      post.addParameter("of", "xml");
      String response = post.getResponse();
      
      // Show response
      System.out.println("Response");
      System.out.println("============");
      System.out.println(response);
      
      // Prints the specific fields in the response (sentiment)
      DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
      Document doc = docBuilder.parse(new ByteArrayInputStream(response.getBytes()));
      doc.getDocumentElement().normalize();
      Element response_node = doc.getDocumentElement();
      System.out.println("\nSentiment:");
      System.out.println("=============");
      try {
        NodeList status_list = response_node.getElementsByTagName("status");
        Node status = status_list.item(0);
        NamedNodeMap attributes = status.getAttributes();
        Node code = attributes.item(0);
        if(!code.getTextContent().equals("0")) {
          System.out.println("Not found");
        } else {    
          NodeList score_tags = response_node.getElementsByTagName("score_tag");
          NodeList sd_tags = response_node.getElementsByTagName("sd_tag");
          NodeList subjectivities = response_node.getElementsByTagName("subjectivity");
          NodeList ironies = response_node.getElementsByTagName("irony");

          String output = "";
          Node score_tag = null;
          Node sd_tag = null;
          Node subjectivity = null;
          Node irony = null;
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
          if(output.isEmpty())
            System.out.println("Not found");
          else
            System.out.println(output);
        }
      } catch (Exception e) {
        System.out.println("Not found");
      }
   }

  
  

}

