# RED

Rate
```
sum by(uri, method) (
  rate(http_server_requests_seconds_count{job="Bot"}[1m])
)
```
Errors
```
sum by(uri, status) (
  rate(http_server_requests_seconds_count{job="Bot", status=~"4..|5.."}[1m])
)
```
Duration
```
sum by(uri) (
rate(http_server_requests_seconds_sum{job="Bot"}[1m])
)
```

# Custom
