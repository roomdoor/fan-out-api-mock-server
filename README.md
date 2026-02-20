# Loan Limit Mock Server

외부 금융사 API를 대체하는 경량 mock 서버입니다.

## Stack

- Kotlin
- Ktor (Netty)
- kotlinx.serialization

## Endpoint

- `POST /api/v1/mock-external/banks/{bankCode}/loan-limit`
- `GET /health`

요청 예시:

```json
{
  "borrowerId": "USER-1001",
  "annualIncome": 70000000,
  "requestedAmount": 30000000
}
```

## Run

```bash
./gradlew run
```

기본 포트: `18080`

## Env Configuration

- `MOCK_SERVER_PORT` (default: `18080`)
- `MOCK_BANK_COUNT` (default: `50`)
- `MOCK_MIN_LATENCY_MS` (default: `3000`)
- `MOCK_MAX_LATENCY_MS` (default: `15000`)
- `MOCK_SLOW_MIN_LATENCY_MS` (default: `30000`)
- `MOCK_SLOW_MAX_LATENCY_MS` (default: `45000`)
- `MOCK_SLOW_BANK_COUNT` (default: `2`)
- `MOCK_SUCCESS_RATE_PERCENT` (default: `85`)

지연 시뮬레이션 기본값:
- 대부분 금융사: 3~15초
- 일부 금융사(기본 2개): 30~45초
