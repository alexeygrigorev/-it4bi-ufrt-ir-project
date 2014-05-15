package it4bi.ufrt.ir.service.doc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.common.SamplingLongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.recommender.GenericRecommendedItem;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyUserBasedRecommender extends GenericUserBasedRecommender{

	private static final Logger LOGGER = LoggerFactory.getLogger(MyUserBasedRecommender.class);
	
	private UserSimilarity similarity;
	private float sim_threshold;
	private DataModel dataModel;
	
	public MyUserBasedRecommender(DataModel dataModel,
			UserNeighborhood neighborhood, UserSimilarity similarity, float threshold) {
		super(dataModel, neighborhood, similarity);
		this.sim_threshold = threshold;
		this.similarity = similarity;
		this.dataModel = dataModel;
	}
	
	public List<DocumentRecord> recommend_custom(long userID) throws TasteException {
		
		FastIDSet userDocs = dataModel.getItemIDsFromUser(userID);
		
		List<RecommendedItem> recommendedDocs = new ArrayList<RecommendedItem>();
		List<DocumentRecord> recommendedDocRecs = new ArrayList<DocumentRecord>();
		List<Long> neighbourIDs = new ArrayList<Long>();
		List<Double> neighbourDistances = new ArrayList<Double>();
		Map<Long, Double> candidateDocs = new HashMap<Long, Double>();
		
		setNeighbours(userID, neighbourIDs, neighbourDistances);
		
		int ctr = 0;
		for(Long curNeighbour : neighbourIDs) {
			LongPrimitiveIterator iter = ((RecommenderDataModel) this.dataModel).getAssociatedDocIDs(curNeighbour);
			//List<DocumentRecord> iter = ((RecommenderDataModel) this.dataModel).getAssociatedDocs(curNeighbour)
			
			
			double userSimilarity = neighbourDistances.get(ctr++);
			while(iter.hasNext()) {
				long key = iter.nextLong();
				if(((RecommenderDataModel) this.dataModel).getUserDocAssociation(key, userID) != null) continue;
				double value = userSimilarity*((RecommenderDataModel) this.dataModel).getUserDocAffinity(curNeighbour, key);
				if(!candidateDocs.containsKey(key)) candidateDocs.put(key, value);
				else {
					double current_value = candidateDocs.get(key); 
					candidateDocs.put(key, value + current_value);
				}
			}
		}
			
		Iterator<Entry<Long,Double>> it = candidateDocs.entrySet().iterator();
		
		while(it.hasNext()) {
			Entry<Long,Double> pair = it.next();
			RecommendedItem recommendedDoc = new GenericRecommendedItem(pair.getKey(), pair.getValue().floatValue());
			recommendedDocs.add(recommendedDoc);
		}
		
		Collections.sort(recommendedDocs, new Comparator<RecommendedItem>() {

			@Override
			public int compare(RecommendedItem o1, RecommendedItem o2) {
				
				if(o1.getValue() == o2.getValue()) return 0;
				
				return o2.getValue() > o1.getValue() ? 1 : -1;
				
			}
		});
		
		for(RecommendedItem rec_doc : recommendedDocs) {
			
			LOGGER.debug("Recommender: Doc wth docID " + rec_doc.getItemID() + " is recommended with " + rec_doc.getValue()*100f + "% confidence");
			
			DocumentRecord docRec = ((RecommenderDataModel) this.dataModel).getDocumentByID(rec_doc.getItemID());
			
			recommendedDocRecs.add(docRec);
		}
		
		
		return recommendedDocRecs;
	}
	
	public void setNeighbours(long userID, List<Long> neighbourIDs, List<Double> neighbourDistances) throws TasteException {
		 
		DataModel dataModel = getDataModel();
		LongPrimitiveIterator usersIterable = SamplingLongPrimitiveIterator.maybeWrapIterator(dataModel.getUserIDs(), 1.0);
		UserSimilarity userSimilarityImpl = similarity;
		    
		while (usersIterable.hasNext()) {
			long otherUserID = usersIterable.next();
			if (userID != otherUserID) {
		    double theSimilarity = userSimilarityImpl.userSimilarity(userID, otherUserID);
		        if (!Double.isNaN(theSimilarity) && theSimilarity >= sim_threshold) {
		        	neighbourIDs.add(otherUserID);
		        	neighbourDistances.add(theSimilarity);
		        }
		      }
		    }	
	}

}
