package com.chuseok22.apichangelog.config;

import com.chuseok22.apichangelog.util.TableHeaders;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "chuseok22.api-change-log")
public class ChangeLogProperties {
  /**
   * API 변경 이력 기능 활성화 여부
   */
  private boolean enabled = true;

  /**
   * 표시할 최대 변경 이력 항목 수
   */
  private int entriesToShow = 10;

  /**
   * 날짜 표시 형식
   */
  private String dateFormat = "yyyy-MM-dd";

  private TableHeaders tableHeaders = new TableHeaders("날짜", "작성자", "설명", "이슈URL");
}


