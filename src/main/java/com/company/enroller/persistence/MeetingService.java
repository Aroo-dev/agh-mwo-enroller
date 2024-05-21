package com.company.enroller.persistence;

import com.company.enroller.dto.MeetingDTO;
import com.company.enroller.dto.ParticipantDTO;
import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component("meetingService")
public class MeetingService {

	private final Session session;

	public MeetingService() {
		this.session = DatabaseConnector.getInstance().getSession();
	}

	public Collection<MeetingDTO> getAll() {
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Meeting> criteria = builder.createQuery(Meeting.class);
		Root<Meeting> root = criteria.from(Meeting.class);
		criteria.select(root);
		List<Meeting> meetings = session.createQuery(criteria).getResultList();
		return meetings.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	public Meeting findByID(long id) {
		return session.get(Meeting.class, id);
	}

	public MeetingDTO add(Meeting meeting) {
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			session.save(meeting);
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		}
		return convertToDTO(meeting);
	}

	public void update(Meeting meeting) {
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			session.update(meeting);
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		}
	}

	public void delete(Meeting meeting) {
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			session.delete(meeting);
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		}
	}

	public MeetingDTO convertToDTO(Meeting meeting) {
		MeetingDTO dto = new MeetingDTO();
		dto.setId(meeting.getId());
		dto.setTitle(meeting.getTitle());
		dto.setDescription(meeting.getDescription());
		dto.setDate(meeting.getDate());
		dto.setParticipants(meeting.getParticipants().stream().map(this::convertToDTO).collect(Collectors.toSet()));
		return dto;
	}

	private ParticipantDTO convertToDTO(Participant participant) {
		ParticipantDTO dto = new ParticipantDTO();
		dto.setLogin(participant.getLogin());
		return dto;
	}
}
