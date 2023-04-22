package com.company.enroller.persistence;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component("meetingService")
public class MeetingService {

	Session session;

	public MeetingService() {
		session = DatabaseConnector.getInstance().getSession();
	}

	public Collection<Meeting> getAll() {
		String hql = "FROM Meeting";
		Query query = this.session.createQuery(hql);
		return query.list();
	}
	public Meeting findById(long id) {
		return this.session.get(Meeting.class, id);
	}

	public void add(Meeting meeting) {
		Transaction transaction = this.session.beginTransaction();
		this.session.save(meeting);
		transaction.commit();
	}

	public void delete(long id) {
		Transaction transaction = this.session.beginTransaction();
		var meeting = this.session.load(Meeting.class, id);
		this.session.delete(meeting);
		transaction.commit();
	}

	public void update(Meeting updatedMeeting) {
		Transaction transaction = this.session.beginTransaction();
		var meeting = this.session.load(Meeting.class, updatedMeeting.getId());

		meeting.setDate(updatedMeeting.getDate());
		meeting.setDescription(updatedMeeting.getDescription());
		meeting.setTitle(updatedMeeting.getTitle());

		this.session.update(meeting);
		transaction.commit();
	}

	public void addParticipant(Long meetingId, String participantLogin) {
		var meeting = this.session.load(Meeting.class, meetingId);
		var participant = this.session.load(Participant.class, participantLogin);

		Transaction transaction = this.session.beginTransaction();
		meeting.addParticipant(participant);

		this.session.update(meeting);
		transaction.commit();
	}

	public void removeParticipant(Long meetingId, String participantLogin) {
		var meeting = this.session.load(Meeting.class, meetingId);
		var participant = this.session.load(Participant.class, participantLogin);

		Transaction transaction = this.session.beginTransaction();
		meeting.removeParticipant(participant);

		this.session.update(meeting);
		transaction.commit();
	}

	public Collection<Participant> getParticipants(Long meetingId) {
		var meeting = this.session.load(Meeting.class, meetingId);
		return meeting.getParticipants();
	}
}
