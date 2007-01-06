package com.googlecode.struts2webflow;

import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.executor.FlowExecutor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;

/**
 * The base class for all interceptors that need to access flow scope.
 */
public abstract class AbstractFlowScopeInterceptor implements Interceptor,
		PreResultListener {
	/**
	 * The spring id of the flow executor bean for this action. If this is not
	 * set, it is assumed that the flow executor will be configured with a
	 * spring id of 'flowExecutor'.
	 */
	private String flowExecutorBean;

	/**
	 * The service responsible for launching and signaling webwork-originating
	 * events in flow executions.
	 */
	private FlowExecutor flowExecutor;

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
			return ExternalFlowAccessHelper.getFlowScope(getFlowExecutor());
		}
	}

	public void init() {
	}

	public void destroy() {
	}
	
	/**
	 * Returns the flow executor used by this interceptor.
	 * 
	 * @return the flow executor
	 */
	public FlowExecutor getFlowExecutor() {
		if (flowExecutorBean != null) {
			ServletContext servletContext = ServletActionContext
					.getServletContext();
			WebApplicationContext context = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
			if (context.containsBean(flowExecutorBean)) {
				flowExecutor = (FlowExecutor) context.getBean(flowExecutorBean,
						FlowExecutor.class);
			} else {
				throw new BeanCreationException("Spring bean: '"
						+ flowExecutorBean
						+ "' Not found in WebApplicationContext!");
			}
		}
		if (flowExecutor == null) {
			throw new ConfigurationException("flowExecutor not found,"
					+ " please provide a flowExecutor for this FlowAction");
		}
		return flowExecutor;
	}

	public void setFlowExecutor(FlowExecutor flowExecutor) {
		this.flowExecutor = flowExecutor;
	}

	/**
	 * @return the spring id of the flow executor.
	 */
	public String getFlowExecutorBean() {
		return flowExecutorBean;
	}

	/**
	 * The spring id of the flow executor.
	 * 
	 * @param flowExecutorBean
	 */
	public void setFlowExecutorBean(String flowExecutorBean) {
		this.flowExecutorBean = flowExecutorBean;
	}
}
