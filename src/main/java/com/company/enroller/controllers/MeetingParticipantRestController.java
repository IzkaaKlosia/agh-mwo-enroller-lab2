package com.company.enroller.controllers;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;


@RestController
@RequestMapping("/meetingParticipants")

public class MeetingParticipantRestController {

    @Autowired
    MeetingService meetingService;
    @Autowired
    ParticipantService participantService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetingParticipants(@PathVariable("id") Long id) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity<String>(
                    "Unable to get meeting participants. A meeting with id " + id + " not exist.",
                    HttpStatus.BAD_REQUEST);
        }

        var participants = meetingService.getParticipants(id);

        return new ResponseEntity<Collection<Participant>>(participants, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> addParticipant(@RequestParam(value = "meetingId", defaultValue = "") Long meetingId,
                                            @RequestParam(value = "participantLogin", defaultValue = "") String participantLogin) {
        var meeting = meetingService.findById(meetingId);
        if (meeting == null) {
            return new ResponseEntity<String>(
                    "Unable to add. A meeting with id " + meetingId + " not exist.",
                    HttpStatus.BAD_REQUEST);
        }
        var participant = participantService.findByLogin(participantLogin);
        if (participant == null) {
            return new ResponseEntity<String>(
                    "Unable to add. A participant with login " + participantLogin + " not exist.",
                    HttpStatus.BAD_REQUEST);
        }
        if(checkIfParticipantInMeeting(meeting, participant)){
            return new ResponseEntity<String>(
                    "Unable to add. User with login " + participantLogin + " already added to meeting with id " + meetingId,
                    HttpStatus.CONFLICT);
        }
        meetingService.addParticipant(meetingId, participantLogin);
        return new ResponseEntity<Meeting>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public ResponseEntity<?> removeParticipantFromMeeting(@RequestParam(value = "meetingId", defaultValue = "") Long meetingId,
                                                          @RequestParam(value = "participantLogin", defaultValue = "") String participantLogin) {

        var meeting = meetingService.findById(meetingId);
        if (meeting == null) {
            return new ResponseEntity<String>(
                    "Unable to remove. A meeting with id " + meetingId + " not exist.",
                    HttpStatus.BAD_REQUEST);
        }
        var participant = participantService.findByLogin(participantLogin);
        if (participant == null) {
            return new ResponseEntity<String>(
                    "Unable to remove. A participant with login " + participantLogin + " not exist.",
                    HttpStatus.BAD_REQUEST);
        }

        if (!checkIfParticipantInMeeting(meeting, participant)) {
            return new ResponseEntity<String>(
                    "Unable to remove. User with login " + participantLogin + " wasn't added to meeting with id " + meetingId,
                    HttpStatus.CONFLICT);
        }
        meetingService.removeParticipant(meetingId, participantLogin);
        return new ResponseEntity<Meeting>(HttpStatus.OK);
    }

    private boolean checkIfParticipantInMeeting(Meeting meeting, Participant participant){
        return meeting.getParticipants().contains(participant);
    }
}
