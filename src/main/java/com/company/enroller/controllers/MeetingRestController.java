package com.company.enroller.controllers;

import com.company.enroller.dto.MeetingDTO;
import com.company.enroller.dto.ParticipantDTO;
import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/meetings")
public class MeetingRestController {

    private final MeetingService meetingService;
    private final ParticipantService participantService;

    @Autowired
    public MeetingRestController(MeetingService meetingService, ParticipantService participantService) {
        this.meetingService = meetingService;
        this.participantService = participantService;
    }

    @GetMapping("")
    public ResponseEntity<Collection<MeetingDTO>> getAllMeetings() {
        Collection<MeetingDTO> allMeetings = meetingService.getAll();
        return ResponseEntity.ok(allMeetings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeetingDTO> getMeetingById(@PathVariable("id") long id) {
        Meeting meeting = meetingService.findByID(id);
        if (meeting == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(meetingService.convertToDTO(meeting), HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<?> addMeeting(@RequestBody Meeting meeting) {
        if (meetingService.findByID(meeting.getId()) != null) {
            return new ResponseEntity<>("The meeting with id " + meeting.getId() + " already exists.", HttpStatus.CONFLICT);
        }
        MeetingDTO createdMeeting = meetingService.add(meeting);
        return new ResponseEntity<>(createdMeeting, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMeeting(@PathVariable("id") long id, @RequestBody Meeting updatedMeeting) {
        Meeting meeting = meetingService.findByID(id);
        if (meeting == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        meeting.setTitle(updatedMeeting.getTitle());
        meeting.setDescription(updatedMeeting.getDescription());
        meeting.setDate(updatedMeeting.getDate());
        meetingService.update(meeting);
        return new ResponseEntity<>(meetingService.convertToDTO(meeting), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMeeting(@PathVariable("id") long id) {
        Meeting meeting = meetingService.findByID(id);
        if (meeting == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        meetingService.delete(meeting);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<Collection<ParticipantDTO>> getParticipantsByMeetingId(@PathVariable("id") long id) {
        Meeting meeting = meetingService.findByID(id);
        if (meeting == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Collection<ParticipantDTO> participants = meeting.getParticipants().stream()
                .map(participantService::convertToDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(participants, HttpStatus.OK);
    }

    @PostMapping("/{id}/participants")
    public ResponseEntity<?> addParticipantsToMeeting(@PathVariable("id") long id, @RequestBody List<String> logins) {
        Meeting meeting = meetingService.findByID(id);
        if (meeting == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        for (String login : logins) {
            Participant participant = participantService.findByLogin(login);
            if (participant == null) {
                return new ResponseEntity<>("Participant with login " + login + " not found.", HttpStatus.NOT_FOUND);
            }
            meeting.addParticipant(participant);
        }
        meetingService.update(meeting);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}/participants/{login}")
    public ResponseEntity<?> removeParticipantFromMeeting(@PathVariable("id") long id, @PathVariable("login") String login) {
        Meeting meeting = meetingService.findByID(id);
        if (meeting == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Participant participant = participantService.findByLogin(login);
        if (participant == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        meeting.removeParticipant(participant);
        meetingService.update(meeting);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
