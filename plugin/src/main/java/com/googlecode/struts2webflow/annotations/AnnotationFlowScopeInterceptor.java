package com.googlecode.struts2webflow.annotations;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
    private static final Log LOG = LogFactory
        .getLog(AnnotationFlowScopeInterceptor.class);

    public String intercept(ActionInvocation invocation) throws Exception {
        if(inWorkflow()) {
            final Object action = invocation.getAction();
            final ValueStack stack = invocation.getStack();
            final Map flowScopeMap = getFlowScopeMap();

            invocation.addPreResultListener(this);
            List<Field> fields = new ArrayList<Field>();
            AnnotationUtils.addAllFields(FlowIn.class, action.getClass(),
                fields);
            for(Field f : fields) {
                String fieldName = f.getName();
                Object attribute = flowScopeMap.get(fieldName);
                if(attribute != null) {
                    if(LOG.isDebugEnabled()) {
                        LOG.debug("flow scoped variable set " + fieldName
                            + " = " + String.valueOf(attribute));
                    }

                    stack.setValue(fieldName, attribute);
                }
            }
        }
        return invocation.invoke();
    }

    public void beforeResult(ActionInvocation invocation, String arg1) {
        if(inWorkflow()) {
            final Object action = invocation.getAction();
            final ValueStack stack = invocation.getStack();
            final Map flowScopeMap = getFlowScopeMap();

            List<Field> fields = new ArrayList<Field>();
            AnnotationUtils.addAllFields(FlowOut.class, action.getClass(),
                fields);
            for(Field f : fields) {
                String fieldName = f.getName();
                Object value = stack.findValue(fieldName);
                if(LOG.isDebugEnabled()) {
                    LOG.debug("flow scoped variable saved " + fieldName
                        + " = " + String.valueOf(value));
                }

                if(value != null)
                    flowScopeMap.put(fieldName, value);
            }
        }
    }
}
