package com.borland.dx.sql.dataset;

import com.borland.dx.cache.Caching;
import com.borland.dx.cache.CachingService;

public class ProviderFactory {

	public static ProcedureProvider createProcedureProvider() {
		ProcedureProvider provider = null;
		CachingService cachingService = Caching.service();
		if (cachingService != null) {
			provider = cachingService.createProcedureProvider();
		}
		if (provider == null) {
			provider = new ProcedureProvider();
		}
		return provider;
	}

}
