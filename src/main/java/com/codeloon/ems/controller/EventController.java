package com.codeloon.ems.controller;


import com.codeloon.ems.dto.EventDto;
import com.codeloon.ems.model.EventBean;
import com.codeloon.ems.service.EventService;
import com.codeloon.ems.util.ResponseBean;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/ems/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;


    @GetMapping
    public ResponseEntity<List<EventBean>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }



    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable Long id) {
        ResponseEntity<?> responseEntity;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ResponseBean responseBean = new ResponseBean();
        try {
            responseBean = eventService.findEventById(id);
            httpStatus = HttpStatus.OK;
        } catch (Exception ex) {
            log.error("Error occurred while getting Event Type by ID.{} ", ex.getMessage());
        } finally {
            responseEntity = new ResponseEntity<>(responseBean, httpStatus);
        }
        return responseEntity;

    }

    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody EventBean eventBean) {
        ResponseEntity<?> responseEntity;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ResponseBean responseBean = new ResponseBean();
                try {
            responseBean = eventService.createEvent(eventBean);
            httpStatus = HttpStatus.CREATED;
        } catch (Exception ex) {
            log.error("Error occurred while saving new Event Type.{} ", ex.getMessage());
        } finally {
            responseEntity = new ResponseEntity<>(responseBean, httpStatus);
        }
        return responseEntity;

    }








    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Long id, @RequestBody EventBean eventBean) {
        ResponseEntity<?> responseEntity;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ResponseBean responseBean = new ResponseBean();
        try {
            responseBean = eventService.updateEvent(id, eventBean);
            httpStatus = HttpStatus.CREATED;
        } catch (Exception ex) {
            log.error("Error occurred while saving new Event Type.{} ", ex.getMessage());
        } finally {
            responseEntity = new ResponseEntity<>(responseBean, httpStatus);
        }
        return responseEntity;

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseBean> deleteEvent(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.deleteEvent(id));
    }
}

