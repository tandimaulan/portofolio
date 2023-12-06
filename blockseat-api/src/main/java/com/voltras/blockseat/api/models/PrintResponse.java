package com.voltras.blockseat.api.models;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

public record PrintResponse(@NotEmpty List<String> contents, String mimeType) {

}
/**
 * @param contents 		list contents for print
 * @param mimeType		mine type for print
 */
