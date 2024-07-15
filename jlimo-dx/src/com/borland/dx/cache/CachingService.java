package com.borland.dx.cache;

import com.borland.dx.dataset.Provider;

public interface CachingService {

	public CachingProvider createCachingProvider(Provider provider);

}
