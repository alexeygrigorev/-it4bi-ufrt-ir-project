package it4bi.ufrt.ir.controller;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;

public class JerseyApp extends ResourceConfig {

	/**
	 * Register JAX-RS application components.
	 */
	public JerseyApp() {
		register(RequestContextFilter.class);
		register(SearchController.class);
	}
}