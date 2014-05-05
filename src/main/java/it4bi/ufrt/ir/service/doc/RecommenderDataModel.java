package it4bi.ufrt.ir.service.doc;
/*
import java.util.Collection;

import javax.sql.DataSource;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.JDBCDataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.springframework.stereotype.Component;
/*
@Component
public class RecommenderDataModel implements JDBCDataModel {

	private DocumentsDAO2 docsDao;
	
	public RecommenderDataModel(DocumentsDAO2 docsDao) {
		this.docsDao = docsDao;
	}
	
	@Override
	public LongPrimitiveIterator getUserIDs() throws TasteException {
		
		docsDao.getAllUserIDs();
		
		return null;
	}

	@Override
	public PreferenceArray getPreferencesFromUser(long userID) throws TasteException {
		
		docsDao.getTagScoresByUserID();
		
		return null;
	}

	@Override
	public FastIDSet getItemIDsFromUser(long userID) throws TasteException {
		
		docsDao.getRankedTagsByUserID();
		return null;
	}

	@Override
	public LongPrimitiveIterator getItemIDs() throws TasteException {
		
		docsDao.getAllTagIDs();
		return null;
	}

	@Override
	public PreferenceArray getPreferencesForItem(long itemID) throws TasteException {
		
		docsDao.getAllTagScoresByTagID();
		return null;
	}

	@Override
	public Float getPreferenceValue(long userID, long itemID)
			throws TasteException {
		
		return docsDao.getUserTagScore((int) userID,(int) itemID);
	}

	@Override
	public Long getPreferenceTime(long userID, long itemID)
			throws TasteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumItems() throws TasteException {
		
		docsDao.getTagCount();
		return 0;
	}

	@Override
	public int getNumUsers() throws TasteException {
		
		docsDao.getUserCount();
		return 0;
	}

	@Override
	public int getNumUsersWithPreferenceFor(long itemID) throws TasteException {
		
		docsDao.getNumUsersRankedACertainTag(itemID);
		return 0;
	}

	@Override
	public int getNumUsersWithPreferenceFor(long itemID1, long itemID2)
			throws TasteException {
		docsDao.getNumUsersRankedACertainTagPair(itemID1, itemID2);
		return 0;
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
		return maxScore;
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

}*/
