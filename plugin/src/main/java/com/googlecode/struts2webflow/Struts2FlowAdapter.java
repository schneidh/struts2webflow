package com.googlecode.struts2webflow;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.dispatcher.Dispatcher;
import org.springframework.util.StringUtils;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ActionProxyFactory;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * Allows webwork actions to be executed as Spring Webflow actions.
 */
public class Struts2FlowAdapter extends AbstractAction {
    private static final Log log = LogFactory
        .getLog(Struts2FlowAdapter.class);

    /**
     * Constant for the action's parameters.
     */
    public static final String REQUEST_CONTEXT = "com.opensymphony.xwork.ActionContext.context";

    /**
     * Adapt attributes
     * @{
     */
    public static final String ATTRIBUTE_ADAPT_ACTION = "action";
    public static final String ATTRIBUTE_ADAPT_METHOD = "method";
    public static final String ATTRIBUTE_ADAPT_NAMESPACE = "namespace";

    /** @} */

    /**
     * @see org.springframework.webflow.action.AbstractAction#doExecute(org.springframework.webflow.RequestContext)
     */
    @SuppressWarnings("unchecked")
    protected Event doExecute(RequestContext context) throws Exception {
        Struts2ExternalContext webworkContext = (Struts2ExternalContext) context
            .getExternalContext();
        ActionInvocation invocation = webworkContext.getActionInvocation();
        ActionProxy proxy = invocation.getProxy();

        // Check attributes for the name of the adapted action
        String actionName = (String) context.getAttributes().get(
            ATTRIBUTE_ADAPT_ACTION);
        if(!StringUtils.hasText(actionName)) {
            // Assume the current state id is the name of the webwork action defined
            // in the xwork.xml
            actionName = context.getCurrentState().getId();
        }

        // Assume the method to call
        String method = (String) context.getAttributes().get(
            ATTRIBUTE_ADAPT_METHOD);

        // Assume the current namespace
        String namespace = (String) context.getAttributes().get(
            ATTRIBUTE_ADAPT_NAMESPACE);
        if(!StringUtils.hasText(namespace)) {
            namespace = invocation.getProxy().getNamespace();
        }

        ValueStack stack = ActionContext.getContext().getValueStack();
        String finalNamespace = TextParseUtil.translateVariables(namespace,
            stack);
        String finalActionName = TextParseUtil.translateVariables(actionName,
            stack);
        String finalMethodName = StringUtils.hasText(method) ? TextParseUtil
            .translateVariables(method, stack) : null;

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.VALUE_STACK, ActionContext
            .getContext().getValueStack());
        extraContext.put(ActionContext.PARAMETERS, ActionContext.getContext()
            .getParameters());
        extraContext.put(REQUEST_CONTEXT, context);

        if(log.isDebugEnabled()) {
            log.debug("Chaining to action " + finalActionName);
        }

        Dispatcher du = Dispatcher.getInstance();

        Configuration config = du.getConfigurationManager()
            .getConfiguration();
        ActionProxyFactory factory = config.getContainer().getInstance(
            ActionProxyFactory.class);
        proxy = factory.createActionProxy(finalNamespace, finalActionName,
            extraContext, false, true);
        if(null != finalMethodName) {
        	proxy.setMethod(finalMethodName);
        }
        proxy.execute();
        String resultCode = proxy.getInvocation().getResultCode();
        return result(resultCode);
    }
}
