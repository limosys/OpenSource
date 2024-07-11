package com.borland.dx.cache;

import com.borland.dx.dataset.Provider;
import com.borland.dx.dataset.StorageDataSet;

public interface CachingProvider {

	public DataLoad provideData(StorageDataSet dataSet, boolean toOpen, Provider delegate);

	public void clearCache(Provider delegate);

	public void dataLoaded(StorageDataSet dataSet, Provider delegate);

}
