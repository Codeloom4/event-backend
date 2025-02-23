package com.codeloon.ems.service;

import com.codeloon.ems.entity.Event;
import com.codeloon.ems.model.EventBean;
import com.codeloon.ems.repository.EventRepository;
import com.codeloon.ems.util.ResponseBean;
import com.codeloon.ems.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    public List<EventBean> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(this::convertToBean)
                .collect(Collectors.toList());
    }

    @Override
    public ResponseBean findEventById(String eventType) {
        ResponseBean responseBean = new ResponseBean();
        try {
            Optional<Event> eventOptional = eventRepository.findById(eventType);
            if (eventOptional.isPresent()) {
                EventBean eventBean = convertToBean(eventOptional.get());
                responseBean.setResponseCode(ResponseCode.RSP_SUCCESS);
                responseBean.setResponseMsg("Event retrieved successfully.");
                responseBean.setContent(eventBean);
            } else {
                responseBean.setResponseCode(ResponseCode.RSP_ERROR);
                responseBean.setResponseMsg("Event not found.");
            }
        } catch (Exception ex) {
            log.error("Error occurred while retrieving event", ex);
            responseBean.setResponseCode(ResponseCode.RSP_ERROR);
            responseBean.setResponseMsg("Error occurred while retrieving event.");
        }
        return responseBean;
    }

    @Override
    public ResponseBean createEvent(EventBean eventBean) {
        ResponseBean responseBean = new ResponseBean();
        try {
            Event event = convertToEntity(eventBean);
            event.setCreatedAt(LocalDateTime.now());
            Event savedEvent = eventRepository.save(event);
            responseBean.setResponseCode(ResponseCode.RSP_SUCCESS);
            responseBean.setResponseMsg("Event created successfully.");
            responseBean.setContent(convertToBean(savedEvent));
        } catch (Exception ex) {
            log.error("Error occurred while creating event", ex);
            responseBean.setResponseCode(ResponseCode.RSP_ERROR);
            responseBean.setResponseMsg("Error occurred while creating event.");
        }
        return responseBean;
    }

    @Override
    public ResponseBean updateEvent(String eventType, EventBean eventBean) {
        ResponseBean responseBean = new ResponseBean();
        try {
            Optional<Event> eventOptional = eventRepository.findById(eventType);
            if (eventOptional.isPresent()) {
                Event event = eventOptional.get();
                BeanUtils.copyProperties(eventBean, event, "eventType", "createdAt");
                Event updatedEvent = eventRepository.save(event);
                responseBean.setResponseCode(ResponseCode.RSP_SUCCESS);
                responseBean.setResponseMsg("Event updated successfully.");
                responseBean.setContent(convertToBean(updatedEvent));
            } else {
                responseBean.setResponseCode(ResponseCode.RSP_ERROR);
                responseBean.setResponseMsg("Event not found.");
            }
        } catch (Exception ex) {
            log.error("Error occurred while updating event", ex);
            responseBean.setResponseCode(ResponseCode.RSP_ERROR);
            responseBean.setResponseMsg("Error occurred while updating event.");
        }
        return responseBean;
    }

    @Override
    public ResponseBean deleteEvent(String eventType) {
        ResponseBean responseBean = new ResponseBean();
        try {
            if (eventRepository.existsById(eventType)) {
                eventRepository.deleteById(eventType);
                responseBean.setResponseCode(ResponseCode.RSP_SUCCESS);
                responseBean.setResponseMsg("Event deleted successfully.");
            } else {
                responseBean.setResponseCode(ResponseCode.RSP_ERROR);
                responseBean.setResponseMsg("Event not found.");
            }
        } catch (Exception ex) {
            log.error("Error occurred while deleting event", ex);
            responseBean.setResponseCode(ResponseCode.RSP_ERROR);
            responseBean.setResponseMsg("Error occurred while deleting event.");
        }
        return responseBean;
    }

    private EventBean convertToBean(Event event) {
        EventBean eventBean = new EventBean();
        BeanUtils.copyProperties(event, eventBean);
        return eventBean;
    }

    private Event convertToEntity(EventBean eventBean) {
        Event event = new Event();
        BeanUtils.copyProperties(eventBean, event);
        return event;
    }
}