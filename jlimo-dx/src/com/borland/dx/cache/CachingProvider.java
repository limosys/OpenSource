package com.borland.dx.cache;

import com.borland.dx.dataset.Provider;
import com.borland.dx.dataset.StorageDataSet;

public interface CachingProvider {

	public boolean provideData(StorageDataSet dataSet, boolean toOpen, Provider delegate);

}
