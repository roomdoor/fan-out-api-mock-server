FROM eclipse-temurin:17-jre

WORKDIR /app

RUN apt-get update \
  && apt-get install -y --no-install-recommends curl \
  && rm -rf /var/lib/apt/lists/*

COPY build/install/loan-limit-mock-server/ /app/

EXPOSE 18080

HEALTHCHECK --interval=5s --timeout=3s --start-period=10s --retries=3 \
  CMD curl -f http://localhost:18080/health || exit 1

ENTRYPOINT ["./bin/loan-limit-mock-server"]
