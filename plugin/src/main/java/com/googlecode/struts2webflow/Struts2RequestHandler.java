package com.googlecode.struts2webflow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.core.FlowException;
import org.springframework.webflow.engine.NoMatchingTransitionException;
import org.springframework.webflow.executor.FlowExecutionResult;
import org.springframework.webflow.executor.FlowExecutor;

/** */
public class Struts2RequestHandler {
    /** */
    private static final Log logger = LogFactory
        .getLog(Struts2RequestHandler.class);

    /**
     * The flow executor this helper will coordinate with.
     */
    private FlowExecutor flowExecutor;

    private String flowId;

    private String flowExecutionKey;

    /**
     * Creates a new WebworkRequestHandler.
     */
    public Struts2RequestHandler() {
    }

    /**
     * Handle a request into the Spring Web Flow system from an external system.
     * 
     * @param context
     *            the external context in which the request occured
     * @return the selected view that should be rendered as a response
     * @throws FlowException
     *             if a flow exception happens
     */
    public FlowExecutionResult handleFlowRequest(ExternalContext context)
        throws FlowException {
    	
        if(logger.isDebugEnabled()) {
            logger.debug("Request initiated by " + context);
        }
		        
        FlowExecutionResult response;
        
        if(flowExecutionKey != null) {
        	
        	try {
        		response = flowExecutor.resumeExecution( flowExecutionKey, context);

        		if(logger.isDebugEnabled()) {
        			logger.debug("Returning [resume] " + response);
        		}

        	} catch (NoMatchingTransitionException e) {
        		throw e;
        	}
        
        } else {
        	
        	response = flowExecutor.launchExecution(flowId, context.getRequestMap(), context);
            
        	if(logger.isDebugEnabled()) {
                logger.debug("Returning [launch] " + response);
            }
        }

        return response;
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
     * @return flowExecutor
     */
    public FlowExecutor getFlowExecutor() {
        return flowExecutor;
    }

    /**
     * @param flowExecutor
     */
    public void setFlowExecutor(FlowExecutor flowExecutor) {
        this.flowExecutor = flowExecutor;
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
}
