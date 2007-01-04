package com.googlecode.struts2webflow;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.execution.support.ApplicationView;
import org.springframework.webflow.executor.FlowExecutor;
import org.springframework.webflow.executor.ResponseInstruction;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.config.ConfigurationException;

/**
 * Webwork action responsible for executing the spring webflow.
 */
public class FlowAction extends ActionSupport {
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

	/** flowId flow parameter */
	private String flowId;

	/** flowExecutionKey flow parameter */
	private String flowExecutionKey;

	/** eventId flow parameter */
	private String eventId;

	/**
	 * @see com.opensymphony.xwork.ActionSupport#execute()
	 */
	public String execute() throws Exception {
		ServletContext servletContext = ServletActionContext
				.getServletContext();
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();

		ActionInvocation actionInvocation = ActionContext.getContext()
				.getActionInvocation();
		ExternalContext context = new Struts2ExternalContext(actionInvocation,
				servletContext, request, response);
		Struts2RequestHandler handler = createRequestHandler();
		handler.setFlowExecutionKey(flowExecutionKey);
		handler.setEventId(eventId);
		handler.setFlowId(flowId);
		ResponseInstruction responseInstruction = handler
				.handleFlowRequest(context);
		flowExecutionKey = responseInstruction.getFlowExecutionKey();
		return toResult(responseInstruction, context, actionInvocation);
	}

	/**
	 * Factory method that creates a new helper for processing a request into
	 * this flow controller.
	 * 
	 * @return the controller helper
	 */
	protected Struts2RequestHandler createRequestHandler() {
		Struts2RequestHandler handler = new Struts2RequestHandler();
		handler.setFlowExecutor(getFlowExecutor());
		return handler;
	}

	/**
	 * Return the appropriate result based on the response.
	 * 
	 * @param response
	 * @param context
	 * @param actionInvocation
	 * @return String
	 */
	protected String toResult(ResponseInstruction response,
			ExternalContext context, ActionInvocation actionInvocation) {
		if (response.isApplicationView()) {
			// forward to a view as part of an active conversation
			ApplicationView forward = (ApplicationView) response
					.getViewSelection();
			return forward.getViewName();
		} else if (response.isNull()) {
			// no response to issue
			return null;
		} else {
			throw new IllegalArgumentException(
					"Don't know how to handle response instruction " + response);
		}
	}

	/**
	 * @return eventId
	 */
	public String getEventId() {
		return eventId;
	}

	/**
	 * @param eventId
	 */
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	/**
	 * @return flowExecutionKey
	 */
	public String getFlowExecutionKey() {
		return flowExecutionKey;
	}

	/**
	 * @param flowExecutionKey
	 */
	public void setFlowExecutionKey(String flowExecutionKey) {
		this.flowExecutionKey = flowExecutionKey;
	}

	/**
	 * @return flowId
	 */
	public String getFlowId() {
		return flowId;
	}

	/**
	 * @param flowId
	 */
	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

	/**
	 * Returns the flow executor used by this controller.
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

	/**
	 * Configures the flow executor implementation to use.
	 * 
	 * @param flowExecutor
	 */
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