#!/usr/bin/env python3
"""Generates the SOLGASES Postman collection (v2.1) and environment from the OpenAPI specification.

The specification is exported by OpenApiDocumentationTest to infrastructure/target/openapi/openapi.json:

    mvn -B -pl infrastructure -am test -Dtest=OpenApiDocumentationTest -Dsurefire.failIfNoSpecifiedTests=false
    python3 docs/postman/generate_postman_collection.py

Only the Python standard library is used. No credentials are written: the base URLs for dev, qa and prd are
left empty on purpose and must be filled in locally by whoever uses the environment. The access token variable
is always empty; the token obtained from POST /api/auth/token must be set locally and never committed.
"""

import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
SPEC = ROOT / "infrastructure" / "target" / "openapi" / "openapi.json"
OUTPUT = ROOT / "docs" / "postman"
METHODS = ("get", "post", "put", "patch", "delete")
LOCAL_BASE_URL = "http://localhost:8080"


def resolve(schema, components):
    if "$ref" in schema:
        return components[schema["$ref"].split("/")[-1]]
    return schema


def example_for(schema, components):
    schema = resolve(schema, components)
    if "example" in schema:
        return schema["example"]
    if "enum" in schema:
        return schema["enum"][0]
    kind = schema.get("type")
    if kind == "object" or "properties" in schema:
        return {name: example_for(prop, components) for name, prop in schema.get("properties", {}).items()}
    if kind == "array":
        return [example_for(schema.get("items", {}), components)]
    return {"integer": 1, "number": 1, "boolean": True}.get(kind, "")


def describe(operation):
    lines = [operation.get("description", "")] if operation.get("description") else []
    lines.append("Responses:")
    for code, response in operation.get("responses", {}).items():
        lines.append(f"- {code}: {response.get('description', '')}")
    return "\n".join(lines)


def build_request(path, method, operation, components):
    parameters = operation.get("parameters", [])
    path_params = [p for p in parameters if p["in"] == "path"]
    query_params = [p for p in parameters if p["in"] == "query"]
    postman_path = path
    for param in path_params:
        postman_path = postman_path.replace("{" + param["name"] + "}", ":" + param["name"])

    url = {
        "raw": "{{baseUrl}}" + postman_path,
        "host": ["{{baseUrl}}"],
        "path": [segment for segment in postman_path.split("/") if segment],
    }
    if path_params:
        url["variable"] = [{
            "key": p["name"],
            "value": str(p.get("example", p.get("schema", {}).get("example", 1))),
            "description": p.get("description", ""),
        } for p in path_params]
    if query_params:
        url["query"] = [{
            "key": p["name"],
            "value": str(example_for(p.get("schema", {}), components)).lower()
            if p.get("schema", {}).get("type") == "boolean" else str(example_for(p.get("schema", {}), components)),
            "description": p.get("description", ""),
            # Optional filters are disabled by default, so they are not part of the raw URL
            "disabled": True,
        } for p in query_params]

    headers = [{"key": "Accept", "value": "application/json"}]
    request = {"method": method.upper(), "header": headers, "url": url, "description": describe(operation)}
    # Operations with an explicit empty security list (the token operation) are public
    if operation.get("security") == []:
        request["auth"] = {"type": "noauth"}
    body = operation.get("requestBody", {}).get("content", {}).get("application/json")
    if body:
        headers.append({"key": "Content-Type", "value": "application/json"})
        request["body"] = {
            "mode": "raw",
            "raw": json.dumps(example_for(body["schema"], components), indent=2, ensure_ascii=False),
            "options": {"raw": {"language": "json"}},
        }
    return {"name": operation.get("summary", f"{method.upper()} {path}"), "request": request}


def build_collection(spec):
    components = spec.get("components", {}).get("schemas", {})
    folders = {}
    for path in sorted(spec["paths"]):
        for method in METHODS:
            operation = spec["paths"][path].get(method)
            if operation:
                tag = (operation.get("tags") or ["Other"])[0]
                folders.setdefault(tag, []).append(build_request(path, method, operation, components))
    info = spec.get("info", {})
    return {
        "info": {
            "name": info.get("title", "SOLGASES API"),
            "description": info.get("description", "") + "\n\nGenerated from the OpenAPI specification by "
                           "docs/postman/generate_postman_collection.py. Do not edit by hand.",
            "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json",
        },
        "item": [{"name": tag, "item": folders[tag]} for tag in sorted(folders)],
        # Every protected request sends the bearer token stored in the accessToken variable (empty by default)
        "auth": {"type": "bearer", "bearer": [{"key": "token", "value": "{{accessToken}}", "type": "string"}]},
        "variable": [{"key": "baseUrl", "value": LOCAL_BASE_URL}],
    }


def build_environment():
    return {
        "name": "SOLGASES",
        "values": [
            {"key": "baseUrl", "value": "{{baseUrlLocal}}", "type": "default", "enabled": True},
            {"key": "baseUrlLocal", "value": LOCAL_BASE_URL, "type": "default", "enabled": True},
            {"key": "baseUrlDev", "value": "", "type": "default", "enabled": True},
            {"key": "baseUrlQa", "value": "", "type": "default", "enabled": True},
            {"key": "baseUrlPrd", "value": "", "type": "default", "enabled": True},
            # Paste a token obtained from POST /api/auth/token locally; never commit a real value
            {"key": "accessToken", "value": "", "type": "secret", "enabled": True},
        ],
        "_postman_variable_scope": "environment",
    }


def write(name, content):
    (OUTPUT / name).write_text(json.dumps(content, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")


def main():
    if not SPEC.exists():
        sys.exit(f"OpenAPI specification not found: {SPEC}. Run OpenApiDocumentationTest first.")
    spec = json.loads(SPEC.read_text(encoding="utf-8"))
    write("solgases.postman_collection.json", build_collection(spec))
    write("solgases.postman_environment.json", build_environment())


if __name__ == "__main__":
    main()
