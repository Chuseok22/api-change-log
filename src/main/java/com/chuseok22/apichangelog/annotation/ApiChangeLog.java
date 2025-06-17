package com.chuseok22.apichangelog.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ApiChangeLog {

  /**
   * 변경 날짜 (yyyy-MM-dd 형식)
   */
  String date();

  /**
   * 변경자 이름
   */
  String author();

  /**
   * 변경 내용
   */
  String description();

  /**
   * 이슈 URL
   */
  String issueUrl() default "";
}
