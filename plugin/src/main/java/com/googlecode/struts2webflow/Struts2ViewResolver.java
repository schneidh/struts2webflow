package com.googlecode.struts2webflow;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.View;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.mvc.view.FlowViewResolver;


/**
 * @author Anatoli Peretoltchine
 */
public class Struts2ViewResolver implements FlowViewResolver {

	private static final Log log = LogFactory.getLog(Struts2ViewResolver.class);
	public static final String FLOW_VIEW_NAME_REQUEST_ATTR = "Flow:viewId";
	/**
	 * @see org.springframework.webflow.mvc.view.FlowViewResolver#getViewIdByConvention(java.lang.String)
	 */
	public String getViewIdByConvention(String viewStateId) {
		log.debug("getViewIdByConvention(): returning null");
		return null;
	}

	/**
	 * @see org.springframework.webflow.mvc.view.FlowViewResolver#resolveView(java.lang.String, org.springframework.webflow.execution.RequestContext)
	 */
	public View resolveView(final String viewId, final RequestContext context) {
		
		log.debug("resolveView(): returning empty view");
		
		return new View(){

			public String getContentType() {
				log.debug("getContentType(): returning null");
				return null;
			}

			public void render(Map model, HttpServletRequest request, HttpServletResponse response)
					throws Exception {
				
				request.setAttribute(FLOW_VIEW_NAME_REQUEST_ATTR, viewId);
				log.debug("render(): setting request attr '" + FLOW_VIEW_NAME_REQUEST_ATTR + "'=" + viewId);
			}
			
		};
	}



}
