package com.googlecode.struts2webflow;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * FlowScopeInterceptor is responsible for mapping items in flow scope to the
 * properties on the webwork actions executing in the webflow.
 */
public class FlowScopeInterceptor extends AbstractFlowScopeInterceptor {
    private static final Log LOG = LogFactory
        .getLog(FlowScopeInterceptor.class);

    private String[] flowScope = null;

    // list of flow scoped properties
    public void setFlowScope(String s) {
        if(s != null) {
            flowScope = s.split(" *, *");
        }
    }

    public String intercept(ActionInvocation invocation) throws Exception {
        if(inWorkflow()) {
            invocation.addPreResultListener(this);

            final ValueStack stack = ActionContext.getContext()
                .getValueStack();
            Map flowScopeMap = getFlowScopeMap();

            if(flowScope != null) {
                for(int i = 0; i < flowScope.length; i++) {
                    String key = flowScope[i];
                    Object attribute = flowScopeMap.get(key);
                    if(attribute != null) {
                        if(LOG.isDebugEnabled()) {
                            LOG.debug("flow scoped variable set " + key
                                + " = " + String.valueOf(attribute));
                        }

                        stack.setValue(key, attribute);
                    }
                }
            }
        }
        return invocation.invoke();
    }

    /**
     * Bind the data back into flow scope if we're executing within the workflow.
     */
    public void beforeResult(ActionInvocation invocation, String resultCode) {
        if(inWorkflow()) {
            final ValueStack stack = ActionContext.getContext()
                .getValueStack();
            Map flowScopeMap = getFlowScopeMap();

            for(int i = 0; i < flowScope.length; i++) {
                String key = flowScope[i];
                Object value = stack.findValue(key);
                if(LOG.isDebugEnabled()) {
                    LOG.debug("flow scoped variable saved " + key + " = "
                        + String.valueOf(value));
                }

                if(value != null)
                    flowScopeMap.put(key, value);
            }
        }
    }
}
