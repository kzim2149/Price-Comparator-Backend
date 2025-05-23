package com.codingchallenge.service;

import com.codingchallenge.exception.NotFoundException;
import com.codingchallenge.model.DiscountEntry;
import com.codingchallenge.repository.DiscountEntryRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DiscountEntryService {
    private final DiscountEntryRepository discountEntryRepository;

    public DiscountEntryService(DiscountEntryRepository discountEntryRepository) {
        this.discountEntryRepository = discountEntryRepository;
    }

    private Sort getSort(boolean orderByDiscount) {
        return orderByDiscount ? Sort.by(Sort.Direction.DESC, "percentageOfDiscount") : Sort.unsorted();
    }

    private List<DiscountEntry> getDiscountEntriesForProduct(String productId, Sort sort) {
        return discountEntryRepository.findByProductId(productId, sort);
    }

    public List<DiscountEntry> getDiscountEntries(String productId, Boolean newDiscounts, Boolean orderByDiscountPercentageDesc) {
        boolean isNewest = Boolean.TRUE.equals(newDiscounts);
        boolean orderByDiscount = Boolean.TRUE.equals(orderByDiscountPercentageDesc);

        Sort sort = getSort(orderByDiscount);

        if (isNewest) {
            return getNewtDiscountEntries(productId, sort);
        }

        if (productId != null) {
            return getDiscountEntriesForProduct(productId, sort);
        }

        return getAllDiscountEntries(sort);
    }

    private List<DiscountEntry> getAllDiscountEntries(Sort sort) {
        return discountEntryRepository.findAll(sort);

    }

    private List<DiscountEntry> getNewtDiscountEntries(String productId, Sort sort) {
        LocalDate dayAgo = LocalDate.now().minusDays(1);
        return productId != null
                ? discountEntryRepository.findByDateAfterAndProductId(dayAgo, productId, sort)
                : discountEntryRepository.findByDateAfter(dayAgo, sort);
    }

    public DiscountEntry getDiscountEntryById(String id) {
        return discountEntryRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("DiscountEntry not found with id: " + id));
    }

    public DiscountEntry createDiscountEntry(DiscountEntry discountEntry) {
        return discountEntryRepository.save(discountEntry);
    }

    public DiscountEntry updateDiscountEntry(DiscountEntry dbDiscountEntry, DiscountEntry incomingDiscountEntry) {
        dbDiscountEntry.setProductId(incomingDiscountEntry.getProductId());
        dbDiscountEntry.setFromDate(incomingDiscountEntry.getFromDate());
        dbDiscountEntry.setToDate(incomingDiscountEntry.getToDate());
        dbDiscountEntry.setPercentageOfDiscount(incomingDiscountEntry.getPercentageOfDiscount());
        dbDiscountEntry.setStoreName(incomingDiscountEntry.getStoreName());
        dbDiscountEntry.setDate(incomingDiscountEntry.getDate());
        return discountEntryRepository.save(dbDiscountEntry);
    }

    public void deleteDiscountEntry( String id) {
        Optional<DiscountEntry> discountEntry = discountEntryRepository.findById(id);
        discountEntry.ifPresent(discountEntryRepository::delete);
    }
}
