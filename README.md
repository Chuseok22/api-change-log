# API 변경 이력 관리 (API Change Log)

[![Maven Central](https://img.shields.io/badge/Maven%20Central-1.0.1-blue.svg)](https://nexus.chuseok22.com/repository/maven-releases/)  
[![Snapshot](https://img.shields.io/badge/Snapshot-0.0.6--SNAPSHOT-orange.svg)](https://nexus.chuseok22.com/repository/maven-snapshots/)

API 변경 이력을 Swagger 문서에 자동으로 표시해주는 간편한 라이브러리입니다.  
API의 변경 사항을 추적하고 문서화하여 개발자와 클라이언트에게 투명한 API 변경 관리를 제공합니다.

---

## 주요 기능

- API 메서드에 변경 이력 어노테이션 추가
- 변경 이력을 테이블 형태로 Swagger UI에 자동 표시
- 날짜, 작성자, 설명, 이슈 URL 등의 정보 제공
- Spring Boot 자동 설정으로 간편한 통합

---

## 설치 방법

### Gradle

```groovy
repositories {
    mavenCentral()
    maven {
        url "https://nexus.chuseok22.com/repository/maven-releases/"
        // 스냅샷 버전을 쓰려면:
        // url "https://nexus.chuseok22.com/repository/maven-snapshots/"
    }
}

dependencies {
    // 안정화 버전
    implementation 'com.chuseok22:api-change-log:1.0.0'
    // 스냅샷 버전
    // implementation 'com.chuseok22:api-change-log:0.0.5-SNAPSHOT'

    // SpringDoc UI 의존성
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.3'
}
```

### Maven

```xml
<repositories>
  <repository>
    <id>chuseok22-releases</id>
    <url>https://nexus.chuseok22.com/repository/maven-releases/</url>
  </repository>
  <!-- 스냅샷 버전 사용 시 -->
  <!--
  <repository>
    <id>chuseok22-snapshots</id>
    <url>https://nexus.chuseok22.com/repository/maven-snapshots/</url>
  </repository>
  -->
</repositories>
```
```xml
<dependencies>
  <dependency>
    <groupId>com.chuseok22</groupId>
    <artifactId>api-change-log</artifactId>
    <version>1.0.0</version>
    <!-- 스냅샷 버전: <version>0.0.5-SNAPSHOT</version> -->
  </dependency>
  <dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.8.3</version>
  </dependency>
</dependencies>
```

---

## 설정 방법

### 1. 기본 설정 (application.yml / application.properties)

```yaml
# application.yml
chuseok22:
  api-change-log:
    enabled: true               # 기능 활성화 (기본값: true)
    entries-to-show: 10         # 표시할 최대 변경 이력 개수 (기본값: 10)
    date-format: "yyyy-MM-dd"   # 날짜 표시 형식 (기본값: "yyyy-MM-dd")
```

```properties
# application.properties
chuseok22.api-change-log.enabled=true
chuseok22.api-change-log.entries-to-show=10
chuseok22.api-change-log.date-format=yyyy-MM-dd
```

### 2. 테이블 헤더 커스터마이징 (선택)

```yaml
chuseok22:
  api-change-log:
    table-headers:
      date: "날짜"           # 기본값: "날짜"
      author: "작성자"       # 기본값: "작성자"
      description: "변경 내용" # 기본값: "변경 내용"
      issue-url: "이슈 URL"   # 기본값: "이슈 URL"
```

---

## 사용 방법

### 1. 단일 변경 이력 추가

```java
import com.chuseok22.apichangelog.annotation.ApiChangeLog;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @ApiChangeLog(
        date       = "2023-06-15",
        author     = "홍길동",
        description= "사용자 이름 필드 추가",
        issueUrl   = "https://github.com/organization/project/issues/123"
    )
    @Operation(summary = "사용자 정보 조회")
    @GetMapping("/users/{id}")
    public UserResponse getUser(@PathVariable Long id) {
        // ...
    }
}
```

### 2. 여러 변경 이력 추가

```java
import com.chuseok22.apichangelog.annotation.ApiChangeLog;
import com.chuseok22.apichangelog.annotation.ApiChangeLogs;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {

    @ApiChangeLogs({
        @ApiChangeLog(
            date       = "2023-06-20",
            author     = "김철수",
            description= "상품 카테고리 필터링 기능 추가"
        ),
        @ApiChangeLog(
            date       = "2023-05-15",
            author     = "이영희",
            description= "상품 정렬 기능 추가",
            issueUrl   = "https://github.com/organization/project/issues/456"
        )
    })
    @Operation(summary = "상품 목록 조회")
    @GetMapping("/products")
    public List<ProductResponse> getProducts() {
        // ...
    }
}
```

### 3. 인터페이스 패턴 사용

```java
// 인터페이스에 문서 정의
public interface ProductControllerDocs {
    @ApiChangeLogs({
        @ApiChangeLog(
            date       = "2023-06-20",
            author     = "김철수",
            description= "상품 카테고리 필터링 기능 추가"
        ),
        @ApiChangeLog(
            date       = "2023-05-15",
            author     = "이영희",
            description= "상품 정렬 기능 추가",
            issueUrl   = "https://github.com/organization/project/issues/456"
        )
    })
    @Operation(summary = "상품 목록 조회")
    ResponseEntity<List<ProductResponse>> getProducts(ProductFilterRequest request);
}

// 컨트롤러 구현
@RestController
@RequestMapping("/api/products")
public class ProductController implements ProductControllerDocs {

    private final ProductService productService;

    @Override
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getProducts(
            @Valid @ModelAttribute ProductFilterRequest request) {
        return ResponseEntity.ok(productService.getProducts(request));
    }
}
```

---

## 예시 결과

Swagger UI에 다음과 같이 표시됩니다:

| 날짜        | 작성자 | 변경 내용                       | 이슈 URL                                       |
| ----------- | ------ | ------------------------------ | ----------------------------------------------- |
| 2023-06-20  | 김철수 | 상품 카테고리 필터링 기능 추가  |                                                 |
| 2023-05-15  | 이영희 | 상품 정렬 기능 추가             | https://github.com/organization/project/issues/456 |

---

## 고급 설정

### 1. 커스텀 빈 등록

```java
@Configuration
public class ApiChangeLogConfig {

    @Bean
    public ChangeLogProperties changeLogProperties() {
        ChangeLogProperties props = new ChangeLogProperties();
        props.setEnabled(true);
        props.setEntriesToShow(5);
        props.setDateFormat("yyyy/MM/dd");

        TableHeaders headers = new TableHeaders();
        headers.setDate("Date");
        headers.setAuthor("Author");
        headers.setDescription("Changes");
        headers.setIssueUrl("Issue");
        props.setTableHeaders(headers);

        return props;
    }

    @Bean
    public ChangeLogOperationCustomizer changeLogOperationCustomizer(
            ChangeLogProperties properties) {
        return new ChangeLogOperationCustomizer(properties);
    }
}
```

### 2. 특정 프로필에서만 활성화

```yaml
# application-dev.yml
chuseok22:
  api-change-log:
    enabled: true
```
```yaml
# application-prod.yml
chuseok22:
  api-change-log:
    enabled: false
```

---

## 문제 해결

1. SpringDoc 의존성이 추가되었는지 확인
   ```groovy
   implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.3'
   ```
2. 설정이 올바른지 확인
   ```yaml
   chuseok22:
     api-change-log:
       enabled: true
   ```
3. 어노테이션 위치 확인
    - 인터페이스 패턴 사용 시, 선언부 인터페이스에도 어노테이션을 붙여야 합니다.
4. 애플리케이션 로그에서 `ChangeLogOperationCustomizer bean registered` 메시지 확인
5. 로그 레벨 DEBUG 설정
   ```yaml
   logging:
     level:
       com.chuseok22.apichangelog: DEBUG
   ```

---

## 기여 방법

1. **이슈 등록**: 버그나 기능 요청은 GitHub 이슈로!
2. **Pull Request**: 코드 개선 및 새로운 기능 PR 환영
3. **코드 스타일**: 기존 스타일을 유지해주세요

---

## 라이센스

MIT License

---

## 연락처

문의사항은 [이메일](mailto:bjh59629@naver.com) 또는 GitHub 이슈를 통해 알려주세요.
