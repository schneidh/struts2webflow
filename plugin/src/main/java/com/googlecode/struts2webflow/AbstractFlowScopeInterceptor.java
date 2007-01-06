package com.googlecode.struts2webflow;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.context.ExternalContextHolder;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.repository.FlowExecutionKey;
import org.springframework.webflow.execution.repository.FlowExecutionRepository;
import org.springframework.webflow.executor.FlowExecutor;
import org.springframework.webflow.executor.FlowExecutorImpl;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;

/**
 * The base class for all interceptors that need to access flow scope.
 */
public abstract class AbstractFlowScopeInterceptor implements Interceptor,
		PreResultListener {
	/**
	 * Parameter name for the flow execution key. (If submitted as a parameter)
	 */
	public static final String FLOW_EXECUTION_KEY = "flowExecutionKey";

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
	 * Location of the flow exec key if stored in session.
	 */
	private String sessionKey = SessionFlowExecKeyInterceptor.DEFAULT_SESSION_KEY;

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

	/**
	 * Helper method to access flow scope from outside the flow.
	 */
	protected Map getFlowScopeExternal(FlowExecutor flowExecutor) {
		FlowExecutionRepository repo = ((FlowExecutorImpl) flowExecutor)
				.getExecutionRepository();
		final ActionContext actionContext = ActionContext.getContext();
		final ActionInvocation actionInvocation = actionContext
				.getActionInvocation();
		final ServletContext servletContext = ServletActionContext
				.getServletContext();
		final HttpServletRequest request = ServletActionContext.getRequest();
		final HttpServletResponse response = ServletActionContext.getResponse();

		ExternalContext context = new Struts2ExternalContext(actionInvocation,
				servletContext, request, response);
		ExternalContextHolder.setExternalContext(context);

		// first check for flow exec key as parameter
		String flowExecKey = (String) actionInvocation.getInvocationContext()
				.getParameters().get(FLOW_EXECUTION_KEY);
		if (flowExecKey == null) {
			// second look in the session
			Map sessionMap = actionContext.getSession();
			flowExecKey = (String) sessionMap.get(sessionKey);
		}
		FlowExecutionKey key = repo.parseFlowExecutionKey(flowExecKey);
		FlowExecution flowExecution = repo.getFlowExecution(key);
		MutableAttributeMap attrMap = flowExecution.getActiveSession()
				.getScope();
		return attrMap.asMap();
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

	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}
}
