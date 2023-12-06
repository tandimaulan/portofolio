package com.voltras.blockseat.api.models;

import java.time.LocalDate;

import com.voltras.blockseat.api.enums.PersonType;
import com.voltras.blockseat.api.enums.Title;

public record Person(Title title, String firstName, String lastName, PersonType personType, LocalDate dob,
		Identity identity) {
}
/**
 *@param title			title of person
 *@param firstName		first name of person
 *@param lastName		last name of person
 *@param personType		type of person
 *@param dob			date of birth person
 *@param identity		identity data of person 
 */
