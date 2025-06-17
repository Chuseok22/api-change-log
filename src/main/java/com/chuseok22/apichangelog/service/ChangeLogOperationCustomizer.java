package com.chuseok22.apichangelog.service;

import com.chuseok22.apichangelog.annotation.ApiChangeLog;
import com.chuseok22.apichangelog.annotation.ApiChangeLogs;
import com.chuseok22.apichangelog.config.ChangeLogProperties;
import io.swagger.v3.oas.models.Operation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

@Component
@RequiredArgsConstructor
public class ChangeLogOperationCustomizer implements OperationCustomizer {

  private final ChangeLogProperties changeLogProperties;

  @Override
  public Operation customize(Operation operation, HandlerMethod handlerMethod) {
    Method method = handlerMethod.getMethod();

    // 구현체 조회
    List<ApiChangeLog> changeLogs = getChangeLogs(method);

    if (!changeLogs.isEmpty()) {
      String currentDescription = operation.getDescription() != null ? operation.getDescription() : "";

      String changeLogTable = generateChangeLogTable(changeLogs);
      operation.setDescription(currentDescription + "\n\n" + changeLogTable);
    }
    return operation;
  }

  private List<ApiChangeLog> getChangeLogs(Method method) {
    // 단일 ApiChangeLog 어노테이션 처리
    ApiChangeLog singleLog = method.getAnnotation(ApiChangeLog.class);

    // 여러 ApiChangeLog 어노테이션 처리
    ApiChangeLogs multiLogs = method.getAnnotation(ApiChangeLogs.class);

    // 인터페이스 메서드에서 어노테이션 검색
    Class<?> declaringClass = method.getDeclaringClass();
    for (Class<?> iface : declaringClass.getInterfaces()) {
      try {
        Method ifaceMethod = iface.getMethod(method.getName(), method.getParameterTypes());
        if (singleLog == null) {
          singleLog = ifaceMethod.getAnnotation(ApiChangeLog.class);
        }
        if (multiLogs == null) {
          multiLogs = ifaceMethod.getAnnotation(ApiChangeLogs.class);
        }
      } catch (NoSuchMethodException ignore) {

      }
    }

    Stream<ApiChangeLog> logStream = Stream.empty();

    if (singleLog != null) {
      logStream = Stream.of(singleLog);
    }

    if (multiLogs != null) {
      logStream = Stream.concat(logStream, Arrays.stream(multiLogs.value()));
    }

    // 최신 변경사항을 먼저 보여주도록 정렬
    return logStream.sorted((log1, log2) ->
            log2.date().compareTo(log1.date()))
        .limit(changeLogProperties.getEntriesToShow())
        .collect(Collectors.toList());
  }

  private String generateChangeLogTable(List<ApiChangeLog> changeLogs) {
    StringBuilder tableBuilder = new StringBuilder();
    tableBuilder.append("### API 변경 이력\n\n");
    tableBuilder.append(String.format("| %s | %s | %s | %s |\n",
        changeLogProperties.getTableHeaders().getDate(),
        changeLogProperties.getTableHeaders().getAuthor(),
        changeLogProperties.getTableHeaders().getDescription(),
        changeLogProperties.getTableHeaders().getIssueUrl()));
    tableBuilder.append("|------|------|--------|----------|\n");

    for (ApiChangeLog log : changeLogs) {
      tableBuilder.append(String.format("| %s | %s | %s | %s |\n",
          log.date(),
          log.author(),
          log.description(),
          log.issueUrl()));
    }

    return tableBuilder.toString();
  }
}
