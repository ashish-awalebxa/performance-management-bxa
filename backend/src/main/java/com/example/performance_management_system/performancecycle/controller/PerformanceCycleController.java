package com.example.performance_management_system.performancecycle.controller;

import com.example.performance_management_system.common.exception.BusinessException;
import com.example.performance_management_system.performancecycle.dto.CreatePerformanceCycleRequest;
import com.example.performance_management_system.performancecycle.model.PerformanceCycle;
import com.example.performance_management_system.performancecycle.service.PerformanceCycleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/performance-cycles")
public class PerformanceCycleController {

    private final PerformanceCycleService service;

    public PerformanceCycleController(PerformanceCycleService service) {
        this.service = service;
    }

    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    @PostMapping
    public PerformanceCycle create(@RequestBody CreatePerformanceCycleRequest req) {

        PerformanceCycle cycle = new PerformanceCycle();
        cycle.setName(req.name);
        cycle.setCycleType(req.cycleType);
        cycle.setStartDate(req.startDate);
        cycle.setEndDate(req.endDate);
        cycle.setCreatedBy("HR_ADMIN"); // later from security context

        return service.createCycle(cycle);
    }

    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    @GetMapping
    public List<PerformanceCycle> list() {
        return service.getAllCycles();
    }

    @GetMapping("/active-cycle")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('MANAGER')")
    public ResponseEntity<PerformanceCycle> getActiveCycle() {
        try {
            return ResponseEntity.ok(service.getActiveCycle());
        } catch (BusinessException ex) {
            return ResponseEntity.noContent().build(); // ✅ 204
        }
    }

    @PreAuthorize("hasRole('HR')")
    @PostMapping("/{id}/start")
    public PerformanceCycle start(@PathVariable Long id) {
        return service.startCycle(id);
    }

    @PreAuthorize("hasRole('HR')")
    @PostMapping("/{id}/close")
    public PerformanceCycle close(@PathVariable Long id) {
        return service.closeCycle(id);
    }
}
