package it4bi.ufrt.ir.service.doc;

import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.model.JDBCDataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;


public class RecommenderDataModel implements JDBCDataModel {

	private DocumentsDAO2 docsDao;
	
	public RecommenderDataModel(DocumentsDAO2 docsDao) {
		this.docsDao = docsDao;
	}
	
	@Override
	public LongPrimitiveIterator getUserIDs() throws TasteException {
		
		List<Long> userIDs = docsDao.getAllUserIDs();
		LongPrimitiveIterator longPrimitiveIt = (LongPrimitiveIterator) userIDs.iterator();
		
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
		
		return null;
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
		
		return (LongPrimitiveIterator) docsDao.getAllTagIDs().iterator();
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
