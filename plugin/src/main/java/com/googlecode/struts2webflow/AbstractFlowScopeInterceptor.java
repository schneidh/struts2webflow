package com.googlecode.struts2webflow;

import java.util.Map;

import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.executor.FlowExecutor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;

public abstract class AbstractFlowScopeInterceptor implements Interceptor,
		PreResultListener {
	private FlowExecutor flowExecutor;
	
	public FlowExecutor getFlowExecutor() {
		return flowExecutor;
	}

	public void setFlowExecutor(FlowExecutor flowExecutor) {
		this.flowExecutor = flowExecutor;
	}

	/**
	 * @return true if we are are executing within the workflow.
	 */
	protected boolean inWorkflow() {
		Map contextMap = ActionContext.getContext().getContextMap();
		RequestContext webflowContext = (RequestContext) contextMap
				.get(Struts2FlowAdapter.REQUEST_CONTEXT);
		return webflowContext != null;
	}
	
	/**
	 * @return flow scope from the webflow if found, otherwise the readonly copy
	 *         from the session, or null if not found in either location.
	 */
	protected Map getFlowScopeMap() {
		if (inWorkflow()) {
			Map contextMap = ActionContext.getContext().getContextMap();
			RequestContext webflowContext = (RequestContext) contextMap
					.get(Struts2FlowAdapter.REQUEST_CONTEXT);
			return webflowContext.getFlowScope().asMap();
		} else {
			return ExternalFlowAccessHelper.getFlowScope(flowExecutor);
		}
	}

	public void init() {
	}

	public void destroy() {
	}
}
