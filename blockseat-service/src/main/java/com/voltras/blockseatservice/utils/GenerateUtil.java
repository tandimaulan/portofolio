package com.voltras.blockseatservice.utils;

import java.io.InputStream;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import com.voltras.blockseat.api.models.Contact;
import com.voltras.blockseatservice.entities.ContactData;

@Component
public class GenerateUtil {
	private static final String whiteImage = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8Xw8AAoMBgDTD2qgAAAAASUVORK5CYII=";

	public static Contact getAgentContact(ContactData contactData) {
		return new Contact(contactData.getAgentFirstName(), contactData.getAgentPhone(), contactData.getAgentEmail());

	}

	public static Contact getCustomerContact(ContactData contactData) {
		return new Contact(contactData.getCustomerName(), contactData.getCustomerPhoneNumber(),
				contactData.getCustomerEmail());

	}

	public static String getBase64EncodedImage(String imageURL) {
		try {
			URL url = new URL(imageURL);
			InputStream is = url.openStream();
			byte[] bytes = org.apache.commons.io.IOUtils.toByteArray(is);
			return Base64.encodeBase64String(bytes);
		} catch (Exception e) {
			return whiteImage;
		}
	}
}