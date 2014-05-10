package it4bi.ufrt.ir.service.doc;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveArrayIterator;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.model.JDBCDataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;


public class RecommenderDataModel implements JDBCDataModel {

	private DocumentsDao docsDao;
	
	public RecommenderDataModel(DocumentsDao docsDao) {
		this.docsDao = docsDao;
	}
	
	public DOCUSER_ASSOC_TYPE getUserDocAssociation(long itemID, long userID) {
		return docsDao.getUserDocAssociation((int) itemID, (int) userID);
	}
	
	public float getUserDocAffinity(long userID, long docID) {
		return docsDao.getUserDocAffinity((int)userID, (int)docID);
	}
	
	public LongPrimitiveIterator getAssociatedDocIDs(long userID) throws TasteException {
		
		List<Long> associatedDocIDs = docsDao.getAssociateddocsIDs((int) userID);
		
		long[] array = ArrayUtils.toPrimitive((Long[]) associatedDocIDs.toArray(new Long[associatedDocIDs.size()]));
		
		LongPrimitiveIterator longPrimitiveIt = new LongPrimitiveArrayIterator(array);
		
		return longPrimitiveIt;
	}
	
	@Override
	public LongPrimitiveIterator getUserIDs() throws TasteException {
		
		List<Long> userIDs = docsDao.getAllUserIDs();
		
		long[] array = ArrayUtils.toPrimitive((Long[]) userIDs.toArray(new Long[userIDs.size()]));
		
		LongPrimitiveIterator longPrimitiveIt = new LongPrimitiveArrayIterator(array);
		
		return longPrimitiveIt;
	}

	@Override
	public PreferenceArray getPreferencesFromUser(long userID) throws TasteException {
		
		List<ImmutablePair<Integer, Float>> tagScoresByUser = docsDao.getTagScoresByUserID((int) userID);
		PreferenceArray preferenceArray = new GenericUserPreferenceArray(tagScoresByUser.size());
		
		int ctr = 0;
		for(ImmutablePair<Integer, Float> tagScore : tagScoresByUser) {
			
			preferenceArray.setUserID(ctr, userID);
			preferenceArray.setItemID(ctr, tagScore.left);
			preferenceArray.setValue(ctr, tagScore.right);
			
			ctr++;
		}
		
		return preferenceArray;
	}

	@Override
	public FastIDSet getItemIDsFromUser(long userID) throws TasteException {
		
		FastIDSet fastIDSet = new FastIDSet();
		
		for(Preference pref : getPreferencesFromUser(userID)) {
			if(pref.getValue() == 0f) continue;
			else fastIDSet.add(pref.getItemID());
		}
		
		return fastIDSet;
	}

	@Override
	public LongPrimitiveIterator getItemIDs() throws TasteException {
		
		List<Long> tagIDs = docsDao.getAllTagIDs();
		
		long[] array = ArrayUtils.toPrimitive((Long[]) tagIDs.toArray(new Long[tagIDs.size()]));
		
		LongPrimitiveIterator longPrimitiveIt = new LongPrimitiveArrayIterator(array);
		
		return longPrimitiveIt; 
	}

	@Override
	public PreferenceArray getPreferencesForItem(long itemID) throws TasteException {
		
		List<ImmutablePair<Integer, Float>> tagScoresByItem = docsDao.getAllTagScoresByTagID((int) itemID);
		PreferenceArray preferenceArray = new GenericUserPreferenceArray(tagScoresByItem.size());
		
		int ctr = 0;
		for(ImmutablePair<Integer, Float> tagScore : tagScoresByItem) {
			preferenceArray.setItemID(ctr, itemID);
			preferenceArray.setUserID(ctr, tagScore.left);
			preferenceArray.setValue(ctr, tagScore.right);
			ctr++;
		}
		
		return preferenceArray;
	}

	@Override
	public Float getPreferenceValue(long userID, long itemID) throws TasteException {
		
		return docsDao.getUserTagScore((int) userID,(int) itemID);
	}

	@Override
	public Long getPreferenceTime(long userID, long itemID) throws TasteException {
		return System.currentTimeMillis();
	}

	@Override
	public int getNumItems() throws TasteException {
		return docsDao.getTagCount();
	}

	@Override
	public int getNumUsers() throws TasteException {
		return docsDao.getUserCount();
	}

	@Override
	public int getNumUsersWithPreferenceFor(long itemID) throws TasteException {
		return docsDao.getNumUsersRankedACertainTag((int) itemID);
	}

	@Override
	public int getNumUsersWithPreferenceFor(long itemID1, long itemID2) throws TasteException {
		return docsDao.getNumUsersRankedACertainTagPair((int) itemID1, (int) itemID2);
	}

	@Override
	public void setPreference(long userID, long itemID, float value) throws TasteException {
		docsDao.updateUserTagScore((int) userID, (int) itemID, value);
		
	}

	@Override
	public void removePreference(long userID, long itemID) throws TasteException {
		docsDao.removeUserTagScore((int) userID,(int) itemID);
		
	}

	@Override
	public boolean hasPreferenceValues() {
		return true;
	}

	@Override
	public float getMaxPreference() {
		return 5f;  // this should be coming from the properties. will fix it later..
	}

	@Override
	public float getMinPreference() {
		return 0f;
	}

	@Override
	public void refresh(Collection<Refreshable> alreadyRefreshed) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DataSource getDataSource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FastByIDMap<PreferenceArray> exportWithPrefs() throws TasteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FastByIDMap<FastIDSet> exportWithIDsOnly() throws TasteException {
		// TODO Auto-generated method stub
		return null;
	}


}
