# Snowflake Server

This server generates a snowflake id.

## Testing

### Request

```shell
grpcurl --plaintext localhost:9090  io.wks.snowflake.api.SnowflakeService/newId
```

### Response

```json
{
"id": "1639352316781990912"
}
```
