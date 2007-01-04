package com.googlecode.struts2webflow;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.webflow.context.servlet.ServletExternalContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;

/**
 * External context that stores the current WebWork
 * <code>ActionInvocation</code>.
 * 
 * Original Author: Aaron Dressin
 */
public class Struts2ExternalContext extends ServletExternalContext {

	private ActionInvocation actionInvocation;

	/**
	 * Constructs a new WebworkExternalContext
	 * 
	 * @param actionInvocation
	 * @param servletContext
	 * @param request
	 * @param response
	 */
	public Struts2ExternalContext(ActionInvocation actionInvocation,
			ServletContext servletContext, HttpServletRequest request,
			HttpServletResponse response) {
		super(servletContext, request, response);
		this.actionInvocation = actionInvocation;
	}

	/**
	 * @return ActionInvocation
	 */
	public ActionInvocation getActionInvocation() {
		return actionInvocation;
	}

	/**
	 * @return ActionContext
	 */
	public ActionContext getActionContext() {
		return ActionContext.getContext();
	}
}
