# AI Development Policy

## AI 보조 커밋 메시지 규칙
- AI가 작성하거나 AI 보조로 만든 커밋은 아래 형식을 따른다.
- 제목 형식: `[ai-assisted] <type>(<scope>): <summary>`
- 제목은 한 줄로 유지하고, 변경 의도가 바로 드러나야 한다.
- `<type>`은 `fix`, `feat`, `refactor`, `test`, `docs`, `chore` 중 변경 성격에 맞게 선택한다.
- `<scope>`는 가능한 한 작업 대상 시스템이나 모듈명을 명시한다. 예: `studio-api`, `document`, `security`

## 커밋 본문 규칙
- 본문은 아래 3개 섹션을 유지한다.
- `Why:` 변경 이유와 배경
- `What:` 실제 변경 내용
- `Validation:` 수행한 검증 명령과 결과

## 예시
```text
[ai-assisted] fix(studio-api): harden document management security paths

Why:
- prevent block-level IDOR in document block mutation endpoints

What:
- bind block mutations to requested document ids
- add validation and regression tests

Validation:
- ./gradlew test
```
