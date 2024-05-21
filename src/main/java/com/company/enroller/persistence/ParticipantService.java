package com.company.enroller.persistence;

import com.company.enroller.dto.ParticipantDTO;
import com.company.enroller.model.Participant;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component("participantService")
public class ParticipantService {

	private static final String FROM_PARTICIPANT_WHERE_LOGIN_LIKE_LOGIN = "FROM Participant WHERE login LIKE :login";
	private final DatabaseConnector connector;

	public ParticipantService() {
		connector = DatabaseConnector.getInstance();
	}

	public Collection<ParticipantDTO> getAll() {
		String hql = "FROM Participant";
		Query query = connector.getSession().createQuery(hql);
		List<Participant> participants = query.list();
		return participants.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	public Collection<ParticipantDTO> getAll(String loginValue, String sortBy, String sortOrder) {
		String hql = FROM_PARTICIPANT_WHERE_LOGIN_LIKE_LOGIN;
		if (sortBy.equals("login")) {
			hql += " ORDER BY " + sortBy;
			if (sortOrder.equals("ASC") || sortOrder.equals("DESC")) {
				hql += " " + sortOrder;
			}
		}
		Query query = connector.getSession().createQuery(hql);
		query.setParameter("login", "%" + loginValue + "%");
		List<Participant> participants = query.list();
		return participants.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	public Participant findByLogin(String login) {
		return connector.getSession().get(Participant.class, login);
	}

	public ParticipantDTO add(Participant participant) {
		Transaction transaction = connector.getSession().beginTransaction();
		participant.setPassword(participant.getPassword());
		connector.getSession().save(participant);
		transaction.commit();
		return convertToDTO(participant);
	}

	public void update(Participant participant) {
		Transaction transaction = connector.getSession().beginTransaction();
		participant.setPassword(participant.getPassword());
		connector.getSession().merge(participant);
		transaction.commit();
	}

	public void delete(Participant participant) {
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().delete(participant);
		transaction.commit();
	}

	public ParticipantDTO convertToDTO(Participant participant) {
		ParticipantDTO dto = new ParticipantDTO();
		dto.setLogin(participant.getLogin());
		return dto;
	}
}
