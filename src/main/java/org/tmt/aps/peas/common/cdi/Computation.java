/**
 * @author Scott Michaels
 * @version 1.0
 * Copyright IBM Corporation 2013 - All Rights Reserved.
 */
package org.tmt.aps.peas.common.cdi;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jakarta.interceptor.InterceptorBinding;

/**
 * Methods annotated with this Annotation are intercepted with the {@link ComputationInterceptor}
 * @author smichaels
 *
 */
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Inherited
@Documented
public @interface Computation {

}
