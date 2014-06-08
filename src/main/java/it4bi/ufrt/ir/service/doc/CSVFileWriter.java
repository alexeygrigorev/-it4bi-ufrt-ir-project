package it4bi.ufrt.ir.service.doc;


import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class CSVFileWriter {
	
	private String filepath;
	private PrintWriter pw;
	private String [] headers;
	private String elementSeperator;
	private String rowSeperator;
	private boolean isFirstCall;
	private String filePath;
	
	public CSVFileWriter(String filePath, String [] headers) throws FileNotFoundException{
		
		this.filepath = filePath;
		pw = new PrintWriter(this.filepath);
		this.headers = headers;
		isFirstCall = true;
		this.elementSeperator = ",";
		this.rowSeperator = System.getProperty("line.separator");
		
		
	}
	
public CSVFileWriter(String filePath, String [] headers, String elementSeparator) throws FileNotFoundException{
		
	    this(filePath,headers);
		this.elementSeperator= elementSeparator;
		
	}
	
	
	public void appendRow (String [] values ){
		
		if(isFirstCall){
			isFirstCall = false;
			appendRow(headers);
		}
		
		for(int i=0; i< headers.length; i++){
			pw.write(values[i]);
			if(i< headers.length-1)
				pw.write(elementSeperator);
		}
		
		pw.write(rowSeperator);
		pw.flush();
		
	}
	
	
	public void close(){
		pw.flush();
		pw.close();
	}
	
	
	
	

	public static void main(String[] args) throws FileNotFoundException {
		
		String [] headers = new String [] {"a", "b", "c"};
		String fileP = "./test";
		
		CSVFileWriter w = new CSVFileWriter(fileP, headers);
		w.appendRow( new String [] {"karim", "Heidi", "1.0"});
		w.appendRow( new String [] {"ka", "Hei", "2.33"});
		w.appendRow( new String [] {"kaim", "Hedi", "1.33333333"});
		w.close();

	}

}