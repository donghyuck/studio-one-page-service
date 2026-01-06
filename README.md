# StudioOne Page Service

Studio One platform 기반의 문서/페이지 서비스 모듈이다. 이 모듈은 노션 스타일의 문서, 버전, 블록 구조를 제공하며 JDBC/JPA 두 가지 저장소 구현을 지원한다.

## Scope
- 문서(Document) 생성, 버전 생성, 메타 업데이트, 삭제
- 블록(Block) 생성/수정/이동/삭제 및 버전 스냅샷
- 문서 검색/페이지네이션
- 낙관적 잠금(If-Match) 기반 충돌 감지

## Domain Model
- Document: 문서 메타(이름, 패턴, 상위 문서, 정렬)
- DocumentVersion: 문서 버전 메타
- DocumentBody/BodyVersion: 본문 스냅샷
- DocumentBlock/BlockVersion: 블록 스냅샷(버전별)
- Properties: 문서 버전별 메타 속성

## Persistence
- JDBC: `document-sqlset.xml` 기반 SQL + `DocumentDaoJdbc`
- JPA: `DocumentDaoJpa` + Repository
- 삭제는 하위 테이블을 명시적으로 삭제한 뒤 문서를 삭제한다.

## Concurrency (ETag/If-Match)
- 문서 메타 업데이트/삭제, 블록 수정/이동/삭제는 `If-Match`를 사용한다.
- 서버는 `ETag`를 응답에 포함하며, 클라이언트는 최신 `ETag`를 `If-Match`로 전달해야 한다.
- 충돌 시 409 응답(`document_conflict`, `block_conflict`).

## API Summary
- `POST /api/documents`
- `POST /api/documents/{documentId}/versions`
- `GET /api/documents/{documentId}` (ETag)
- `GET /api/documents/{documentId}/versions/{versionId}` (ETag)
- `PUT /api/documents/{documentId}/meta` (If-Match)
- `DELETE /api/documents/{documentId}` (If-Match)
- `POST /api/documents/{documentId}/blocks`
- `PUT /api/documents/{documentId}/blocks/{blockId}` (If-Match)
- `PATCH /api/documents/{documentId}/blocks/{blockId}/move` (If-Match)
- `DELETE /api/documents/{documentId}/blocks/{blockId}` (If-Match)
- `GET /api/documents/{documentId}/blocks` (ETag)
- `GET /api/documents/{documentId}/versions/{versionId}/blocks?includeDeleted=&parentBlockId=`
- `GET /api/documents/{documentId}/blocks/tree?versionId=&includeDeleted=`

## Local Development
- Build: `./gradlew build`
- Test: `./gradlew test`
- SQL: `src/main/resources/sql/document-sqlset.xml`
- DDL: `src/main/resources/schema/postgres` / `src/main/resources/schema/mysql`

## Starter Module
- 모듈 위치: `starter`
- artifactId: `studio-application-starter-document`
- 자동 설정: `DocumentAutoConfiguration`, `DocumentJpaAutoConfiguration`
- 사용 예시:
  - `implementation("studio.one.starter:studio-application-starter-document:${VERSION}")`

## Vue (Vuetify 3) Client Guide

### Setup
- API Base: `/api`
- HTTP Client: axios 또는 fetch
- 상태 관리: pinia 권장

### ETag/If-Match 처리
1) 문서/블록 조회 응답에서 `ETag` 저장
2) 수정/삭제 요청 시 동일한 `ETag`를 `If-Match`로 전달
3) 409 발생 시 최신 데이터 재조회 및 재시도 UX 제공

```ts
// axios 예시
const res = await api.get(`/documents/${documentId}`);
const etag = res.headers["etag"];

await api.put(
  `/documents/${documentId}/meta`,
  { name, pattern },
  { headers: { "If-Match": etag } }
);
```

### 문서/블록 렌더링 팁
- 블록 목록은 `sortOrder` 기준으로 정렬된 상태로 제공되지만,
  트리 렌더링 시 부모-자식 관계에 따라 재정렬이 필요할 수 있다.
- `GET /blocks/tree`는 트리 구조를 그대로 제공한다.

### 페이지네이션
- 문서 목록 조회는 `Pageable` 기반으로 제공된다.
- 클라이언트는 `page`, `size`, `sort` 쿼리를 맞춰 호출한다.

### 오류 처리
- 400: 잘못된 `If-Match` 포맷 등 요청 문제
- 401/403: 인증/인가 실패
- 409: 동시성 충돌 (ETag 재조회 후 재시도)
