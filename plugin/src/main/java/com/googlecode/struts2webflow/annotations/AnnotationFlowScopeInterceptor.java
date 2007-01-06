package com.googlecode.struts2webflow.annotations;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.googlecode.struts2webflow.AbstractFlowScopeInterceptor;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.AnnotationUtils;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * AnnotationFlowScopeInterceptor implements the FlowIn and FlowOut annotation
 * behavior. Add this interceptor to your interceptor stack to use these
 * annonations.
 */
public class AnnotationFlowScopeInterceptor extends
		AbstractFlowScopeInterceptor {

	public void init() {
	}

	public String intercept(ActionInvocation invocation) throws Exception {
		final Object action = invocation.getAction();
		final ValueStack stack = invocation.getStack();
		final Map flowScopeMap = getFlowScopeMap();

		invocation.addPreResultListener(this);
		List<Field> fields = new ArrayList<Field>();
		AnnotationUtils.addAllFields(FlowIn.class, action.getClass(), fields);
		for (Field f : fields) {
			String fieldName = f.getName();
			stack.setValue(fieldName, flowScopeMap.get(fieldName));
		}

		return invocation.invoke();
	}

	public void beforeResult(ActionInvocation invocation, String arg1) {
		final Object action = invocation.getAction();
		final ValueStack stack = invocation.getStack();
		final Map flowScopeMap = getFlowScopeMap();

		List<Field> fields = new ArrayList<Field>();
		AnnotationUtils.addAllFields(FlowOut.class, action.getClass(), fields);
		for (Field f : fields) {
			String fieldName = f.getName();
			flowScopeMap.put(fieldName, stack.findValue(fieldName));
		}
	}

	public void destroy() {
	}
}
