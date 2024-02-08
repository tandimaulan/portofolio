package com.voltras.ppob.utils;

import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.voltras.kismis.request.Request;
import com.voltras.kismis.response.ExceptionResponse;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class GenerateUtil {
	public static final JSONDeserializer<Object> jsonDeserializer = new JSONDeserializer<>();
	public static final JSONSerializer jsonSerializer = new JSONSerializer();
	private static final NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));

	private static final String whiteImage = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8Xw8AAoMBgDTD2qgAAAAASUVORK5CYII=";

	public static String getIdrPrice(Double price) {
		return numberFormat.format(price).replace("Rp", "IDR ");
	}

	public static MultiValueMap<String, String> createFormData(Request request, String token) {
		String requestString = createRequestString(request);
		MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
		form.add("requeststring", requestString);

		if (token != null) {
			String requestKey = createRequestKey(requestString, token);
			form.add("requestkey", requestKey);
		}

		return form;
	}

	public static String createRequestString(Request request) {
		String requestString = jsonSerializer.deepSerialize(request);
		requestString = requestString.replace("\"class\"", "\"@type\"");

		return requestString;
	}

	public static String createRequestKey(String requestString, String token) {
		return SHA1(requestString + token);
	}

	public static String SHA1(String text) {
		try {
			MessageDigest md;
			md = MessageDigest.getInstance("SHA-1"); //$NON-NLS-1$
			byte[] sha1hash = new byte[40];
			md.update(text.getBytes("iso-8859-1"), 0, text.length()); //$NON-NLS-1$
			sha1hash = md.digest();
			return convertToHex(sha1hash);
		} catch (Exception e) {
			return ""; //$NON-NLS-1$
		}
	}

	private static String convertToHex(byte[] data) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}

	public static Pageable generatePageable(Integer page, Integer size, Map<String, Boolean> sortMapping) {
		var newSize = size == null ? 10 : size;
		var newPage = page == null ? 0 : page;
		var sort = Sort.unsorted();
		if (sortMapping != null && !sortMapping.isEmpty()) {
			sortMapping = sortMapping.entrySet().stream().filter(sortType -> sortType.getValue() != null)
					.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
			if (!sortMapping.isEmpty()) {
				sort = Sort.by(sortMapping.entrySet().stream()
						.map(sortType -> sortType.getValue().equals(true) ? Order.asc(sortType.getKey())
								: Order.desc(sortType.getKey()))
						.toList());
			}
		}
		return PageRequest.of(newPage, newSize, sort);
	}

	@SuppressWarnings("unchecked")
	public static <T> T deserializeFromVan(String text, Class<T> expectedType) {
		text = text.replace("\"@type\"", "\"class\"");
		JSONDeserializer<?> newJsonDeserializer = jsonDeserializer;
		if (expectedType.equals(ExceptionResponse.class) || expectedType.isInstance(ExceptionResponse.class)) {
			newJsonDeserializer = newJsonDeserializer.use("params.values", String.class);
		}
		return (T) newJsonDeserializer.deserialize(text, expectedType);
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
