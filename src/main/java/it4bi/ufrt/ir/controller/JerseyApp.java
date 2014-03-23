package it4bi.ufrt.ir.controller;

import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

public class JerseyApp extends ResourceConfig {

	/**
	 * Register JAX-RS application components.
	 */
	public JerseyApp() {			
		//super(UploadController.class, MultiPartFieldInjectedResource.class, MultiPartFeature.class);
		register(RequestContextFilter.class);
		register(SearchController.class);
		register(InfoController.class);
		register(MultiPartFeature.class);
		register(UploadController.class);
		
				
		// http://localhost:8080/it4bi-ufrt-ir-project 
		// http://localhost:8080/it4bi-ufrt-ir-project/rest/search/doc?q=123&u=5
		// http://localhost:8080/it4bi-ufrt-ir-project/rest/info/users
	}
}