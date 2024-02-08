package com.voltras.ppob.gateway.api.requests.pdam;

public record PDAMPaymentRequest(
String clientId, String transactionDate,
String credential, String modul,
String command,String transactionId, String customerId, String responseType, String detail, String biller)
{

}
