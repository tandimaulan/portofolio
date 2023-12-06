package com.voltras.blockseat.admin.api.models;

import java.util.UUID;

import com.voltras.blockseat.admin.api.enums.TimelimitCondition;

public record Timelimit(
		UUID timelimitId,
		Integer dayFrom,
		Integer dayTo,
		Integer duration,
		TimelimitCondition condition) {
}
/**
 * @param timelimitId           unique flag of a timelimit
 * @param dayFrom             	value from indicate start-line of date timelimit
 * @param dayTo            		value to indicate end-line of date timelimit
 * @param duration      		duration for timelimit
 * @param condition     		condition for timelimit
 * 
 */