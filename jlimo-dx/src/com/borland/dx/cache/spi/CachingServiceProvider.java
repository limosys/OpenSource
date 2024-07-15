package com.borland.dx.cache.spi;

import com.borland.dx.cache.CachingService;

public interface CachingServiceProvider {

	public CachingService create();

}
