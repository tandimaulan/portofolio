package com.voltras.blockseatservice.entities;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.voltras.blockseat.api.enums.PersonType;
import com.voltras.blockseat.api.enums.Title;
import com.voltras.core.common.api.enums.BookingStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(indexes = { @Index(name = "passenger_data_idx", columnList = "id") })
public class PassengerData {

	@Id
	@GeneratedValue
	private UUID id;

	private String firstName;
	private String lastName;
	private LocalDate dob;
	private String bookingCode;
	private Double totalPaid;
	private Double outstanding;
	private String ticketNum;

	@Enumerated(EnumType.STRING)
	private Title title;
	@Enumerated(EnumType.STRING)
	private PersonType personType;
	@Enumerated(EnumType.STRING)
	private BookingStatus bookingStatus;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "identityid", referencedColumnName = "id")
	private IdentityData identity;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "attachmentsid", referencedColumnName = "id")
	private List<PassengerAttachment> attachments;

	@OneToOne
	@JoinColumn(name = "statements", referencedColumnName = "id")
	private StatementData statement;

	public PassengerData(String firstName, String lastName, LocalDate dob, String bookingCode, Title title, PersonType personType,
			IdentityData identity, List<PassengerAttachment> attachments) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.dob = dob;
		this.bookingCode = bookingCode;
		this.title = title;
		this.personType = personType;
		this.identity = identity;
		this.attachments = attachments;
	}

}
