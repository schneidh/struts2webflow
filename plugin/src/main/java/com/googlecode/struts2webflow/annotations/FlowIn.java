package com.googlecode.struts2webflow.annotations;

import java.lang.annotation.*;

/**
 * This annotation is used on a action property to indicate that the property
 * should be inject from flow scope.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.FIELD })
public @interface FlowIn {
}
