package com.voltras.ppob.service.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voltras.ppob.api.models.ProductCode;
import com.voltras.ppob.api.models.availability.Product;
import com.voltras.ppob.api.models.availability.ProductDetail;
import com.voltras.ppob.api.services.PpobAvailabilityService;
import com.voltras.ppob.service.entities.Prefix;
import com.voltras.ppob.service.repositories.PrefixRepository;
import com.voltras.ppob.service.repositories.ProductRepository;
import com.voltras.voltrasspring.rpc.services.RpcBasicService;
import com.voltras.voltrasspring.security.Publish;
import com.voltras.voltrasspring.van.configs.VanAdditionalRequestData;

@Service("ppobAvailabilityService")
public class PpobAvailbilityServiceImpl implements PpobAvailabilityService, RpcBasicService {
	@Autowired
	private ProductRepository productRepo;
	@Autowired
	private PrefixRepository prefixRepo;
	@Autowired
	private VanAdditionalRequestData session;

	@Override
	@Publish(allowAll = true)
	public List<Product> getActiveProducts() {
		List<com.voltras.ppob.service.entities.Product> products = productRepo.findAll();
		Map<String, String> expression = session.getOffice().getMapCommissionExpression();
		if (expression.isEmpty()) {
			throw new RuntimeException("An error occurred in the system, please contact the helpdesk!");
		} else {
			return products.stream()
					.filter(data -> expression.containsKey(data.getType()) && data.getIsActive().equals(true))
					.map(data -> new Product(data.getGroup(), data.getType())).distinct().sequential()
					.collect(Collectors.toList());
		}
	}

	@Override
	@Publish(allowAll = true)
	public List<ProductDetail> getProductDetails(@NotNull @Valid Product product, String phoneNumber) {
		var datas = productRepo.findByType(product.type());
		if (!product.group().equals("PULSA")) {
			return datas.stream().filter(data -> data.getIsActive().equals(true))
					.map(data -> new ProductDetail(data.getCode(), stringToEnum(data.getType()), data.getNominal(),
							data.getVoucherName(), data.getCodeDescription(), true))
					.collect(Collectors.toList());

		}
		String provider = null;
		List<Prefix> prefixs = prefixRepo.findAll();
		for (Prefix prefix : prefixs) {
			if (phoneNumber.startsWith(prefix.getPrefixNumber())) {
				provider = prefix.getProvider();
				break;
			}
		}
		return filterProductsByProvider(datas, provider);
	}

	private List<ProductDetail> filterProductsByProvider(List<com.voltras.ppob.service.entities.Product> products,
			String provider) {
		if (provider == null) {
			return new ArrayList<ProductDetail>();
		}

		return products.stream().filter(data -> data.getIsActive().equals(true)).filter(data -> {
			String providerInData = data.getCode();
			if (providerInData.contains("_")) {
				providerInData = data.getCode().split("_")[1];
			}
			providerInData = providerCodeToProvider(providerInData);

			return providerInData != null && (providerInData.contains(provider) || provider.contains(providerInData));
		}).map(data -> new ProductDetail(data.getCode(), stringToEnum(data.getType()), data.getNominal(),
				data.getVoucherName(), data.getCodeDescription(), true)).collect(Collectors.toList());
	}

	private String providerCodeToProvider(String pro) {
		List<com.voltras.ppob.service.entities.Product> product = productRepo.getByCode(pro);
		if (!product.isEmpty()) {
			return product.get(0).getVoucherName();
		} else {
			throw new IllegalArgumentException("Kode penyedia tidak ditemukan: " + pro);
		}
	}

	private ProductCode stringToEnum(String type) {

		ProductCode enumData = switch (type) {
		case "PULSA-PRABAYAR" -> ProductCode.PULSA_PRABAYAR;
		case "PULSA-PAKETDATA" -> ProductCode.PULSA_PAKETDATA;
		case "PLN-PREPAID" -> ProductCode.PLN_PREPAID;
		case "PLN-POSTPAID" -> ProductCode.PLN_POSTPAID;
		case "PDAM-P" -> ProductCode.PDAM_P;
		case "BPJS-KS" -> ProductCode.BPJS_KS;
		case "SAMOLNAS" -> ProductCode.SAMOLNAS;
		case "MULTIFINANCE" -> ProductCode.MULTIFINANCE;
		default -> throw new IllegalArgumentException("Unexpected value: " + type);
		};
		return enumData;
	}

}
