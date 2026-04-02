package com.akshansh.taskmanagementplatform.security;

import org.springframework.security.test.context.support.WithSecurityContext;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockUserPrincipalFactory.class)  // points to Step 2
public @interface WithMockUserPrincipal {
    long id() default 1L;
    String name() default "John Doe";
    String email() default "johndoe1234@gmail.com";
    String role() default "MEMBER";
}