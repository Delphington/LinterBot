# RED

Rate
```
sum by(uri, method) (
  rate(http_server_requests_seconds_count{job="Scrapper"}[1m])
)
```
Errors
```
sum by(uri, status) (
  rate(http_server_requests_seconds_count{job="Scrapper", status=~"4..|5.."}[1m])
)
```
Duration
```
sum by(uri) (
rate(http_server_requests_seconds_sum{job="Scrapper"}[1m])
)
```

# Custom

Active Links
```
scrapper_links_processed_stackoverflow 

scrapper_links_processed_github
```

GitHub Percentile
```
scrapper_scrape_time_seconds{type="github",quantile="0.5"}
scrapper_scrape_time_seconds{type="github",quantile="0.95"}
scrapper_scrape_time_seconds{type="github",quantile="0.99"}
```

StackOverFlow Percentile
```
scrapper_scrape_time_seconds{type="stackoverflow",quantile="0.5"}
scrapper_scrape_time_seconds{type="stackoverflow",quantile="0.95"}
scrapper_scrape_time_seconds{type="stackoverflow",quantile="0.99"}
```
