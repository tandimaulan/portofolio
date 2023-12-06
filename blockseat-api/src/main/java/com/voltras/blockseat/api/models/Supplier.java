package com.voltras.blockseat.api.models;

import java.util.UUID;

public record Supplier(UUID supplierId, String supplierName, String supplierEmail) {

}
/**
*@param supplierId			unique id for supplier
*@param supplierName		name of supplier
*@param supplierEmail		email of supplier
*/