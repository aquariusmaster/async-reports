package com.anderb.async;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@AllArgsConstructor
@RestController
public class MyController {

    private final MyService service;

    @GetMapping(path = "/si")
    public String simple() {
        log.info("Si before");
        return service.simple();
    }

    @GetMapping(path = "/my")
    public void getString() {
        service.process();
    }

    @GetMapping(path = "/res")
    public String getRes() {
        service.processWithResult().thenAccept(str -> {
            log.info("Result2: " + str);
        });
        log.info("return");
        return "ok";
    }

    @GetMapping(path = "/reports")
    public String getReports() {
        List<Long> ids = new ArrayList<>();
        ids.add(5000L);
        ids.add(3000L);
        ids.add(4000L);
        service.runReports(ids).thenAccept(list -> {
            log.info("Result2: " + list);
        });
        log.info("return");
        return "ok";
    }


}
