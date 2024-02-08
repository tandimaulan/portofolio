package com.voltras.ppob.service.components;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.voltras.ppob.service.entities.Product;
import com.voltras.ppob.service.repositories.ProductRepository;

@Service
public class ProductScraper {
	@Autowired
	private ProductRepository productRepo;
	@Autowired
	private LogHelper logger;
	private final String className = this.getClass().getCanonicalName();

	@Scheduled(cron = "0 10 0,12 * * *")
	public void scraper() {
		String url = "sensor";
		var productDatas = productRepo.findByGroup("PULSA");

		try {
			Document document = Jsoup.connect(url).get();
			Element tbody = document.select("tbody").first();
			Elements rows = tbody.select("tr");

			List<Product> data = new ArrayList<>();

			for (Element row : rows) {
				Elements columns = row.select("td");

				if (!columns.isEmpty()) {
					String code = columns.get(0).text();
					String product = columns.get(1).text();

					// Check if the code exists in productDatas and is of group "PULSA"
					Optional<Product> existingProduct = productDatas.stream()
							.filter(datas -> datas.getCode().equals(code) && datas.getGroup().equals("PULSA"))
							.findFirst();

					if (existingProduct.isPresent()) {
						// If the product with the given code exists, update isActive if needed
						if (!existingProduct.get().getIsActive()) {
							existingProduct.get().setIsActive(true); // Set it to true if it was inactive
							logger.info("Existing product with code {} marked as active.", code);
						}
					} else {
						// If the product with the given code doesn't exist, add it to the data list
						Product productData = new Product(code, product, true);
						data.add(productData);
					}
				}
			}

			// Update isActive for products in the database that are not present in the new
			// data
			productDatas.forEach(datas -> {
				String code = datas.getCode();
				Boolean existsInNewData = data.stream().anyMatch(newData -> newData.getCode().equals(code));

				if (!existsInNewData) {
					datas.setIsActive(false);
					logger.info("Existing product with code {} marked as inactive.", code);
				}
			});

			// Save the new data and update existing data
			productRepo.saveAllAndFlush(data);
			logger.info("[{}.updateProductData] Update Data Success {}", className, data.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
