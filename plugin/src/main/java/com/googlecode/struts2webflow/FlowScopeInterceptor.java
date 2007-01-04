package com.googlecode.struts2webflow;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.executor.FlowExecutor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * FlowScopeInterceptor is responsible for mapping items in flow scope to the
 * properties on the webwork actions executing in the webflow.
 */
public class FlowScopeInterceptor implements Interceptor, PreResultListener {
	private static final Log LOG = LogFactory
			.getLog(FlowScopeInterceptor.class);

	private String[] flowScope = null;
	
	private FlowExecutor flowExecutor;
	
	// list of flow scoped properties
	public void setFlowScope(String s) {
		if (s != null) {
			flowScope = s.split(" *, *");
		}
	}

	public FlowExecutor getFlowExecutor() {
		return flowExecutor;
	}

	public void setFlowExecutor(FlowExecutor flowExecutor) {
		this.flowExecutor = flowExecutor;
	}

	public void init() {
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

	public String intercept(ActionInvocation invocation) throws Exception {
		invocation.addPreResultListener(this);

		final ValueStack stack = ActionContext.getContext().getValueStack();
		Map flowScopeMap = getFlowScopeMap();

		if (flowScope != null)
			for (int i = 0; i < flowScope.length; i++) {
				String key = flowScope[i];
				Object attribute = flowScopeMap.get(key);
				if (attribute != null) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("flow scoped variable set " + key + " = "
								+ String.valueOf(attribute));
					}

					stack.setValue(key, attribute);
				}
			}
		String result = invocation.invoke();
		return result;
	}

	/**
	 * Bind the data back into flow scope if we're executing within the workflow.
	 */
	public void beforeResult(ActionInvocation invocation, String resultCode) {
		if (inWorkflow()) {
			final ValueStack stack = ActionContext.getContext().getValueStack();
			Map flowScopeMap = getFlowScopeMap();

			for (int i = 0; i < flowScope.length; i++) {
				String key = flowScope[i];
				Object value = stack.findValue(key);
				if (LOG.isDebugEnabled()) {
					LOG.debug("flow scoped variable saved " + key + " = "
							+ String.valueOf(value));
				}

				if (value != null)
					flowScopeMap.put(key, value);
			}
		}
	}

	public void destroy() {
	}
}
