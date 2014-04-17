package it4bi.ufrt.ir.service.web;



import java.io.*;
import java.net.*;

public class Sandbox {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try{
		
		URL url = new URL("http://api2.socialmention.com/search?q=france&f=xml&t=microblogs&lang=en");
        String query = "";

        //make connection
        URLConnection urlc = url.openConnection();

        //use post mode
        urlc.setDoOutput(true);
        urlc.setAllowUserInteraction(false);

        //send query
        PrintStream ps = new PrintStream(urlc.getOutputStream());
        ps.print(query);
        ps.close();

        //get result
        BufferedReader br = new BufferedReader(new InputStreamReader(urlc
            .getInputStream()));
        String l = null;
        while ((l=br.readLine())!=null) {
            System.out.println(l);
        }
        br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
