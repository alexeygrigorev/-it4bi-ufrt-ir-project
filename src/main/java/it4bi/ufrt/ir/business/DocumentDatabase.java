package it4bi.ufrt.ir.business;

import it4bi.ufrt.ir.service.doc.DocumentRecord;


public class DocumentDatabase {
    
    private static final DocumentRecord[] documents = {
    	new DocumentRecord("Streaming k-means approximation", "./docs/3812-streaming-k-means-approximation.pdf"),
    	new DocumentRecord("Music Recommendation by Unified Hypergraph: Combining Social Media Information and Music Content", "./docs/ACM-MM2010-bu.pdf"),
    	new DocumentRecord("MapReduce and PACT - Comparing Data Parallel Programming Models", "./docs/ComparingMapReduceAndPACTs_11.pdf"),
    	new DocumentRecord("MapReduce for Parallel Reinforcement Learning","./docs/ewrl2011_submission_11.pdf"),
    	new DocumentRecord("Comparative Evaluation of Spark and Stratosphere","FULLTEXT01")
    };

	public static DocumentRecord[] getDocuments() {
		return documents;
	}
}

