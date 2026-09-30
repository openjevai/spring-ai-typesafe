# OpenJEV Support

This fork adds optional support for [OpenJEV](https://openjev.sh) — a free community
gateway to the same Jev model — alongside the original [TypeSafe](https://typesafe.ai)
integration. TypeSafe remains the default; anyone with a TypeSafe key sees zero behaviour
change.

## What was added

| File | Change |
|------|--------|
| `typesafe-java-sdk/.../OpenJEVConstants.java` | New: OpenJEV endpoint, model id, key env var, provider env var constants |
| `typesafe-java-sdk/.../TypeSafeClient.java` | Builder `build()` provider selection: explicit `JEV_PROVIDER` wins, else TypeSafe if its key is set, else OpenJEV |
| `typesafe-java-sdk/.../RetryPolicy.java` | Added HTTP 503 to `DEFAULT_RETRYABLE_STATUSES` (OpenJEV signals overload with 503) |
| `typesafe-java-sdk/.../api/TypeSafeResponseErrorHandler.java` | Maps HTTP 503 to `TypeSafeOverloadedException` (alongside existing 529) |
| `typesafe-java-sdk/.../exception/TypeSafeOverloadedException.java` | Javadoc updated to mention 503 |
| `spring-ai-starter-typesafe/.../TypeSafeProperties.java` | New `openjev-api-key` and `provider` properties; `resolveUseOpenJEV()`, `resolveApiKey()`, `resolveBaseUrl()`, `resolveModel()` helpers |
| `spring-ai-starter-typesafe/.../TypeSafeAutoConfiguration.java` | Condition accepts either key; uses resolved key/URL/model |
| `spring-ai-starter-typesafe/.../additional-spring-configuration-metadata.json` | New property hints |
| `README.md` | OpenJEV note after intro |
| `examples/src/main/resources/application.properties` | Commented OpenJEV config example |

## Provider selection rule

1. **Explicit choice wins**: `JEV_PROVIDER=openjev` (env) or `spring.ai.typesafe.provider=openjev` (property).
2. **Otherwise, if the TypeSafe key is set** → TypeSafe, exactly as before (default unchanged).
3. **Otherwise, if only `OPENJEV_API_KEY` is set** → OpenJEV.

When OpenJEV is selected:
- Base URL: `https://api.openjev.sh` (unless explicitly overridden)
- Model: `openjev` (unless explicitly overridden)
- Key: `OPENJEV_API_KEY` env var or `spring.ai.typesafe.openjev-api-key` property
- Overload status: HTTP 503 (retried, mapped to `TypeSafeOverloadedException`)

## Configuration

### Plain Java SDK (environment variables)

```bash
# Option 1: OpenJEV only (auto-detected)
export OPENJEV_API_KEY=...

# Option 2: Explicit provider
export JEV_PROVIDER=openjev
export OPENJEV_API_KEY=...
```

```java
TypeSafeClient client = TypeSafeClient.builder().build(); // auto-detects OpenJEV
```

### Spring Boot starter (properties)

```properties
# OpenJEV only (auto-detected)
spring.ai.typesafe.openjev-api-key=${OPENJEV_API_KEY}

# Or explicit:
spring.ai.typesafe.openjev-api-key=${OPENJEV_API_KEY}
spring.ai.typesafe.provider=openjev
```

## How it was verified

- One live `POST https://api.openjev.sh/v1/systemone` request with model `openjev`, state `ping`, one noul question → HTTP 200.
- `grep -r "api.typesafe.ai"` confirms no TypeSafe default was removed or replaced; TypeSafe remains the default endpoint.
- No repo code was executed (no build, no tests, no install scripts).

## Upstream

Original project: https://github.com/spring-ai-community/spring-ai-typesafe by @spring-ai-community
