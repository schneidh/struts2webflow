package com.googlecode.struts2webflow;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.context.ExternalContextHolder;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.repository.FlowExecutionKey;
import org.springframework.webflow.execution.repository.FlowExecutionRepository;
import org.springframework.webflow.executor.FlowExecutor;
import org.springframework.webflow.executor.FlowExecutorImpl;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;

public class ExternalFlowAccessHelper {
	public static Map getFlowScope(FlowExecutor flowExecutor) {
		FlowExecutionRepository repo = ((FlowExecutorImpl) flowExecutor)
				.getExecutionRepository();
		ActionInvocation actionInvocation = ActionContext.getContext()
				.getActionInvocation();
		ServletContext servletContext = ServletActionContext
				.getServletContext();
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();

		ExternalContext context = new Struts2ExternalContext(actionInvocation,
				servletContext, request, response);
		ExternalContextHolder.setExternalContext(context);
		Map sessionMap = ActionContext.getContext().getSession();
		String flowExecKey = (String) sessionMap
				.get(SessionFlowExecKeyInterceptor.DEFAULT_SESSION_KEY);
		FlowExecutionKey key = repo.parseFlowExecutionKey(flowExecKey);
		FlowExecution flowExecution = repo.getFlowExecution(key);
		MutableAttributeMap attrMap = flowExecution.getActiveSession()
				.getScope();
		return attrMap.asMap();
	}
}
