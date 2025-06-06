package com.codingchallenge.repository;

import com.codingchallenge.model.DiscountEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DiscountEntryRepository extends MongoRepository<DiscountEntry, String> {
    List<DiscountEntry> findByProductId(String productId);

    List<DiscountEntry> findByProductId(String productId, Sort sort);

    List<DiscountEntry> findByDateAfter(LocalDate localDate);

    List<DiscountEntry> findByDateAfter(LocalDate localDate, Sort sort);

    List<DiscountEntry> findByDateAfterAndProductId(LocalDate dayAgo, String productId, Sort sort);
}
