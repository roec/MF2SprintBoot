# Semantic-Driven RPG → Spring Boot Migration Backend

This project provides a runnable Spring Boot backend for **semantic-driven migration** from IBM i RPG artifacts into Spring Boot/JPA/REST outputs.

## Features

- Semantic IR construction (`ProgramIr`, `DataIr`, `LogicIr`) with source traces.
- Agent-like orchestration pipeline (`SemanticIrBuilder` + `CodeGenerationService`).
- DeepSeek-backed LLM integration via `DeepSeekClient`.
- REST endpoint: `POST /api/migrations/convert`.

## Run

```bash
mvn spring-boot:run
```

## Example Request

```json
{
  "programName": "CUSTINQ",
  "programType": "INQUIRY",
  "artifacts": [
    {"type": "RPG", "name": "CUSTINQ", "content": "CHAIN CUSTPF IF *IN90"},
    {"type": "PF", "name": "CUSTPF", "content": "A CUST_NO 9P 0"}
  ],
  "optionalRules": ["status A means active"]
}
```
