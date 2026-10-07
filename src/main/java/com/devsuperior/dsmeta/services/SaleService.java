package com.devsuperior.dsmeta.services;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import com.devsuperior.dsmeta.dto.SaleReportDTO;
import com.devsuperior.dsmeta.dto.SaleSummaryDTO;
import com.devsuperior.dsmeta.projections.SaleReportProjection;
import com.devsuperior.dsmeta.projections.SaleSummaryProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.devsuperior.dsmeta.dto.SaleMinDTO;
import com.devsuperior.dsmeta.entities.Sale;
import com.devsuperior.dsmeta.repositories.SaleRepository;

@Service
public class SaleService {

	@Autowired
	private SaleRepository repository;
	
	public SaleMinDTO findById(Long id) {
		Optional<Sale> result = repository.findById(id);
		Sale entity = result.get();
		return new SaleMinDTO(entity);
	}

	public Page<SaleReportDTO> getReport(String minDate, String maxDate, String name, Pageable pageable) {
		LocalDate max = parseMaxDate(maxDate);
		LocalDate min = parseMinDate(minDate, max);
		Page<SaleReportProjection> result = repository.searchReport(min, max, name, pageable);
		return result.map(x -> new SaleReportDTO(x.getId(), x.getDate(), x.getAmount(), x.getSellerName()));
	}

	public List<SaleSummaryDTO> getSummary(String minDate, String maxDate) {
		LocalDate max = parseMaxDate(maxDate);
		LocalDate min = parseMinDate(minDate, max);
		List<SaleSummaryProjection> result = repository.searchSummary(min, max);
		return result.stream().map(x -> new SaleSummaryDTO(x.getSellerName(), x.getTotal())).toList();
	}

	private LocalDate parseMaxDate(String maxDate) {
		if (maxDate == null || maxDate.isBlank()) {
			return LocalDate.ofInstant(Instant.now(), ZoneId.systemDefault());
		}
		return LocalDate.parse(maxDate);
	}

	private LocalDate parseMinDate(String minDate, LocalDate max) {
		if (minDate == null || minDate.isBlank()) {
			return max.minusYears(1L);
		}
		return LocalDate.parse(minDate);
	}

}
