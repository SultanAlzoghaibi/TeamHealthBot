FROM grafana/k6
COPY load-test.js .
ENTRYPOINT ["k6", "run", "load-test.js"]