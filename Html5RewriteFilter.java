package com.ws.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Servlet <tt>Filter</tt> that forwards unrecognized requests to the applications root path.
 * An unrecognized request is one that does not start with the appropriate prefix for static or web app
 * content.  The prefixes are passed into the filter through the {@linkplain ALLOWED_PATHS_INIT_PARAM}
 * init param.
 *
 * This is to enable HTML5 mode so that ugly Hashbang URLs do not have to be used.
 *
 * @author sterrasi
 */
public class Html5RewriteFilter implements Filter{

	private static final Logger LOGGER = LoggerFactory.getLogger(Html5RewriteFilter.class);
	private static final String ALLOWED_PATHS_INIT_PARAM = "allowedPaths";
	private static final String FORWARD_TO = "/index.html";
	private List<String> allowedPathPrefixes;

	/**
	 * Populate the paths allowed through the filter.  All others will be redirected to the root.
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

		allowedPathPrefixes = new ArrayList<>();

		// get the 'allowedPaths' init parameter as a comma delimited list of values.
		String allowedPathPrefixesValue = filterConfig.getInitParameter(ALLOWED_PATHS_INIT_PARAM);
		if ( null == allowedPathPrefixesValue ){
			LOGGER.warn("init param '{}' for {} was not provided.", ALLOWED_PATHS_INIT_PARAM
					, Html5RewriteFilter.class.getSimpleName());
		}

		// store the paths in a member list
		for ( String prefix : allowedPathPrefixesValue.split(",")){
			String normalized  = prefix.trim().toLowerCase();
			if ( normalized.isEmpty() ){
				continue;
			}
			allowedPathPrefixes.add(normalized);
		}
		if ( LOGGER.isDebugEnabled() ){
			LOGGER.debug("allowed path prefixes: " + Arrays.toString(allowedPathPrefixes.toArray()));
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest httpReq = (HttpServletRequest)request;
		String path = httpReq.getRequestURI();

		LOGGER.debug("received request: " + path);

		// only check the restrictions if there is a path
		if ( shouldForward(path) ){
			LOGGER.debug("Forwarding request '{}' to '{}'.", path, FORWARD_TO);
			RequestDispatcher dispatcher = httpReq.getRequestDispatcher(FORWARD_TO);
			dispatcher.forward(request, response);
		}
		else {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void destroy() { }


	/**
	 * Checks the path to see if it starts with one of the path prefixes specified in the
	 * {@linkplain #ALLOWED_PATHS_INIT_PARAM }, or if it is the root path.
	 * @param prefix
	 * @return
	 */
	private boolean shouldForward(String prefix){

		if ( null == prefix || prefix.equals("/") || prefix.equals(FORWARD_TO)){
			return false;
		}

		String normalized = prefix.trim().toLowerCase();
		for ( String allowed : allowedPathPrefixes){
			if( normalized.startsWith(allowed)){
				return false;
			}
		}
		return true;
	}

}
