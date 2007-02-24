package com.googlecode.struts2webflow;

import org.springframework.webflow.core.FlowException;

/**
 * Exception thrown when the flow exec key cannot be found.
 */
public class FlowExecKeyNotFoundException extends FlowException {

    public FlowExecKeyNotFoundException(String message) {
        super(message);
    }
}
