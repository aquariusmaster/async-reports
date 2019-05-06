package com.anderb.async;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class MyService {

    @Qualifier("threadTaskExecutor")
    private final ExecutorService executor;

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
        }, executor).thenCompose(str -> {
            log.info("Result: " + str);
            sleep(3000);
            return CompletableFuture.supplyAsync(() -> str + ": end", executor);});
    }

    public CompletableFuture<List<String>> runReports(List<Long> ids) {
        return CompletableFuture.supplyAsync(() -> ids.stream().map(this::runReport).collect(Collectors.toList())
                .stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList()),
                executor);
    }

    private CompletableFuture<String> runReport(long timeToSleep) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Report starting:"+timeToSleep);
            sleep(timeToSleep);
            log.info("Report completed:"+timeToSleep);
            if (timeToSleep == 1000L) {
                throw new RuntimeException("Some Error");
            }
            return "Ok:"+timeToSleep;
        }, executor).handle((response, error) -> {
            if (error != null) return null;
            return response;
        });
    }

    @SneakyThrows
    private void sleep(long time) {
        Thread.sleep(time);
    }
}
