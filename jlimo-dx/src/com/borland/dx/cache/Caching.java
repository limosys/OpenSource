package com.borland.dx.cache;

import java.util.Iterator;
import java.util.ServiceLoader;

import com.borland.dx.cache.spi.CachingServiceProvider;

public class Caching {

	public static CachingService service() {
		ServiceLoader<CachingServiceProvider> loader = ServiceLoader.load(CachingServiceProvider.class);
		Iterator<CachingServiceProvider> it = loader.iterator();
		if (it.hasNext()) {
			CachingService service = it.next().create();
			if (it.hasNext()) { throw new IllegalStateException("Multiple caching service providers found"); }
			return service;
		}
		return null;
	}

}
