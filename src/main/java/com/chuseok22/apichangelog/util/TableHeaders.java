package com.chuseok22.apichangelog.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TableHeaders {

  private String date = "날짜";
  private String author = "작성자";
  private String description = "변경 내용";
  private String issueUrl = "이슈 URL";
}
