# Mock Server Performance Test Environment Presets

This directory contains environment variable presets for deterministic performance testing.

## Usage

Load environment variables before starting the mock server:

```bash
# For baseline tests (deterministic, fast responses)
source /Users/sihwa/IdeaProjects/loan-limit-mock-server/perf/baseline.env
./gradlew run

# For stress tests (variable latency, some failures)
source /Users/sihwa/IdeaProjects/loan-limit-mock-server/perf/stress.env
./gradlew run
```

## Profiles

### baseline.env
- **Purpose**: Deterministic baseline for comparing fan-out modes
- **Latency**: Fixed 500ms (min=max, no variance)
- **Success Rate**: 100%
- **Slow Banks**: 0
- **Use Case**: Fair comparison between coroutine/async-threadpool/webclient modes

### stress.env
- **Purpose**: Stress testing with realistic conditions
- **Latency**: 3-15s normal, 30-45s for slow banks
- **Success Rate**: 90% (10% failures)
- **Slow Banks**: 2 banks with higher latency
- **Use Case**: Find throughput limits and failure handling

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| MOCK_SERVER_PORT | Server port | 18080 |
| MOCK_BANK_COUNT | Number of banks | 50 |
| MOCK_MIN_LATENCY_MS | Minimum response latency | - |
| MOCK_MAX_LATENCY_MS | Maximum response latency | - |
| MOCK_SLOW_MIN_LATENCY_MS | Minimum latency for slow banks | - |
| MOCK_SLOW_MAX_LATENCY_MS | Maximum latency for slow banks | - |
| MOCK_SLOW_BANK_COUNT | Number of slow banks | - |
| MOCK_SUCCESS_RATE_PERCENT | Success rate percentage | - |

## Docker Fleet Usage

When running 10 shards (ports 18000-18009):

```bash
# Start fleet with baseline profile
export $(cat /Users/sihwa/IdeaProjects/loan-limit-mock-server/perf/baseline.env | xargs)
# Then run docker-compose up
```
