package com.codeloon.ems.controller;


import com.codeloon.ems.dto.EventDto;
import com.codeloon.ems.model.EventBean;
import com.codeloon.ems.service.EventService;
import com.codeloon.ems.util.ResponseBean;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ems/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    //TODO open same endpoint in Home controller.
    @GetMapping
    public ResponseEntity<List<EventBean>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }



    @GetMapping("/{id}")
    public ResponseEntity<ResponseBean> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.findEventById(id));
    }

    @PostMapping
    public ResponseEntity<ResponseBean> createEvent(@RequestBody EventBean eventBean) {
        return ResponseEntity.ok(eventService.createEvent(eventBean));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseBean> updateEvent(@PathVariable Long id, @RequestBody EventBean eventBean) {
        return ResponseEntity.ok(eventService.updateEvent(id, eventBean));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseBean> deleteEvent(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.deleteEvent(id));
    }
}

