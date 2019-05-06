package com.anderb.async;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MyService {

    public String simple() {
        log.info("In simple");
        sleep(2000);
        log.info("Out simple");
        return "ok";
    }

    @Async
    public void process() {
        log.info("process");
        throw new RuntimeException("My exception");
    }

    @Async("threadTaskExecutor")
    public CompletableFuture<String> processWithResult() {
        log.info("process with result");

        return CompletableFuture.supplyAsync(() -> {
            log.info("In Completable future");
            sleep(2000);
            return "Ok";
        }).thenCompose(str -> {
            log.info("Result: " + str);
            sleep(3000);
            return CompletableFuture.supplyAsync(() -> str + ": end");});
    }

    public CompletableFuture<List<String>> runReports(List<Long> ids) {
        return CompletableFuture.supplyAsync(() -> ids.stream().map(this::runReport).collect(Collectors.toList())
                .stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList()));
    }

    private CompletableFuture<String> runReport(long timeToSleep) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Report starting:"+timeToSleep);
            sleep(timeToSleep);
            log.info("Report completed:"+timeToSleep);
            return "Ok:"+timeToSleep;
        });
    }

    @SneakyThrows
    private void sleep(long time) {
        Thread.sleep(time);
    }
}
