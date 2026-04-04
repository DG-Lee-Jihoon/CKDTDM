package com.parking.controller;

import com.parking.model.SyncEvent;
import com.parking.sync.SyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sync")
public class SyncController {

    private static final Logger log = LoggerFactory.getLogger(SyncController.class);
    private final SyncService syncService;

    public SyncController(SyncService syncService) {
        this.syncService = syncService;
    }

    // Endpoint nhận sync event từ peer server
    @PostMapping("/receive")
    public ResponseEntity<Void> receive(@RequestBody SyncEvent event) {
        log.debug("[SyncController] Nhận event: phase={}, from=Server{}",
                event.getPhase(), event.getFromServerId());
        syncService.receiveSync(event);
        return ResponseEntity.ok().build();
    }
}
