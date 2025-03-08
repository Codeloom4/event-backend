package com.codeloon.ems.model;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private String name;
    private List<Participant> participants = new ArrayList<>();

    public Group(String name) {
        this.name = name;
    }

    public void addParticipant(Participant participant) {
        participants.add(participant);
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }
}