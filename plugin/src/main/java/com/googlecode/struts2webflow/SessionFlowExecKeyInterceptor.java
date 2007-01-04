package com.googlecode.struts2webflow;

import java.util.Map;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * A helper intercepter that maintains the flow execution key in the session
 * rather than pushing it out to the client.
 */
public class SessionFlowExecKeyInterceptor implements Interceptor {
	public static final String DEFAULT_SESSION_KEY = "SessionFlowExecKeyInterceptor.SESSION_KEY";

	/**
	 * Location in the session where the flow exec key is stored between flow
	 * requests.
	 */
	private String sessionKey = DEFAULT_SESSION_KEY;

	public void init() {
	}

	public String intercept(ActionInvocation invocation) throws Exception {
		Map session = invocation.getInvocationContext()
				.getSession();
		ValueStack stack = invocation.getStack();

		String flowExecKey = (String) session.get(sessionKey);
		if (flowExecKey != null) {
			stack.setValue("flowExecutionKey", flowExecKey);
		}
		String result = invocation.invoke();
		flowExecKey = (String) stack.findValue("flowExecutionKey");
		session.put(sessionKey, flowExecKey);
		return result;
	}

	public void destroy() {
	}

	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}
}
