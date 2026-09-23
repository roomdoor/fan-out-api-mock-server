# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

외부 금융사 대출한도 API 를 흉내 내는 Ktor(Netty) mock 서버. `fan-out-call` 부하 테스트의 호출 대상이다. Gradle 프로젝트명은 `loan-limit-mock-server`(디렉터리명과 다름).
엔드포인트·환경변수는 `README.md`, 부하용 프리셋·10 샤드 compose 는 `perf/` 에 있다.

@AGENTS.md

## 함정

- **은행 호출 실패도 HTTP 200 이다.** 실패는 응답 본문(`httpStatus: 503`, `responseCode: "E503"`)으로만 나타난다. 부하 스크립트에서 실패를 셀 때는 본문을 본다.
- 느린 은행은 `bankCode` 가 정확히 `BANK-<n>` 형식일 때만 걸린다. 형식이 다르면 n=0 으로 처리돼 느린 은행이 하나도 없는 채로 돈다.
- 환경변수는 이름 오타나 숫자 파싱 실패 모두 에러 없이 코드 기본값으로 대체된다.
- 이미지는 installDist 산출물을 COPY 만 한다. `docker build`·`perf/docker-compose.yml` 모두 먼저 `./gradlew installDist` 가 필요하다.
  프로젝트명을 바꾸면 `Dockerfile` 과 `.dockerignore` 의 `build/install/loan-limit-mock-server` 경로도 같이 바꾼다.
- 벤치 인프라는 x86 전용이다. CI 가 GHCR 에 올리는 이미지는 amd64 단일이므로 멀티아치·arm64 를 추가하지 말 것 — 에뮬레이션이 측정값을 오염시킨다.
  `perf/docker-compose.yml` 은 로컬에서 빌드하므로 호스트 아키텍처를 따른다.
