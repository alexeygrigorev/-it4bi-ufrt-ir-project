package it4bi.ufrt.ir.service.web;

import java.util.List;

public class TestSocialSearch {

	public static void main(String[] args) {
		
		try{
		SocialSearchService serv = new SocialSearchService();
		List<SocialSearchRecord> results = serv.search("usa", SocialSearchType.FACEBOOK, true,1);
		System.out.println( results.size()+ " Retrued");
		
		for(SocialSearchRecord rec: results)
			rec.toString();
		
		for(int i=0;i<results.size();i++){
			System.out.println(results.get(i).toString());
		}
		
		System.out.println("Done");
		
		}catch (Exception e){
			e.printStackTrace();
		}
	}

}
