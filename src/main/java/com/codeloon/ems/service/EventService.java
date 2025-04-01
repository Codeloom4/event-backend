package com.codeloon.ems.service;


import com.codeloon.ems.model.EventBean;
import com.codeloon.ems.util.ResponseBean;

import java.util.List;

public interface EventService {
    List<EventBean> getAllEvents();
    ResponseBean findEventById(String eventType);
    ResponseBean createEvent(EventBean eventBean);
    ResponseBean updateEvent(String eventType, EventBean eventBean);
    ResponseBean deleteEvent(String eventType);
}

