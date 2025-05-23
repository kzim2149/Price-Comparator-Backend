package com.codingchallenge.controller;

import com.codingchallenge.dto.incoming.CreatePriceEntryDto;
import com.codingchallenge.dto.outgoing.GetPriceEntryDto;
import com.codingchallenge.dto.outgoing.GetPriceHistoryDto;
import com.codingchallenge.mapper.PriceEntryMapper;
import com.codingchallenge.model.PriceEntry;
import com.codingchallenge.service.PriceEntryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/price-entries")
@Validated
public class PriceEntryController {

    private final PriceEntryService priceEntryService;

    private final PriceEntryMapper priceEntryMapper;

    public PriceEntryController(PriceEntryService priceEntryService,
                                PriceEntryMapper priceEntryMapper
    ) {
        this.priceEntryService = priceEntryService;
        this.priceEntryMapper = priceEntryMapper;
    }

    @GetMapping
    public ResponseEntity<List<GetPriceEntryDto>> getPriceEntries(
        @RequestParam(value = "productId", required = false)
        @Pattern(regexp = "^[A-Z0-9]{1,20}$", message = "Product ID must be alphanumeric and between 1 and 20 characters.")
        String productId,
        @RequestParam(value = "orderByValue", required = false)
        Boolean orderByValue
    ) {
        List<PriceEntry> priceEntries = priceEntryService.getPriceEntries(productId,orderByValue);

        return ResponseEntity.ok(priceEntryMapper.toGetPriceEntryDtos(priceEntries));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetPriceEntryDto> getPriceEntryById(
        @PathVariable
        String id
    ) {
        PriceEntry priceEntry = priceEntryService.getPriceEntryById(id);

        return ResponseEntity.ok(priceEntryMapper.toGetPriceEntryDto(priceEntry));
    }

    @PostMapping
    public ResponseEntity<GetPriceEntryDto> createPriceEntry(
            @RequestBody
            @Valid
            CreatePriceEntryDto createPriceEntryDto
    ) {
        PriceEntry priceEntry = priceEntryMapper.toPriceEntry(createPriceEntryDto);
        PriceEntry createdPriceEntry = priceEntryService.createPriceEntry(priceEntry);

        return ResponseEntity.created(ServletUriComponentsBuilder
                                          .fromCurrentRequest()
                                          .path("/{id}")
                                          .buildAndExpand(createdPriceEntry.getId())
                                          .toUri())
            .body(priceEntryMapper.toGetPriceEntryDto(createdPriceEntry));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GetPriceEntryDto> updatePriceEntry(
            @PathVariable
            String id,
            @RequestBody
            @Valid
            CreatePriceEntryDto createPriceEntryDto
    ) {
        PriceEntry existingPriceEntry = priceEntryService.getPriceEntryById(id);
        PriceEntry incomingPriceEntry = priceEntryMapper.toPriceEntry(createPriceEntryDto);
        PriceEntry updatedPriceEntry = priceEntryService.updatePriceEntry(existingPriceEntry, incomingPriceEntry);

        return ResponseEntity.ok(priceEntryMapper.toGetPriceEntryDto(updatedPriceEntry));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePriceEntry(
            @PathVariable
            String id
    ) {
        priceEntryService.deletePriceEntry(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * Fetches the price history based on the provided filters.
     * If no filters are provided, it returns the entire price history.
     */
    @GetMapping("/history")
    public ResponseEntity<List<GetPriceHistoryDto>> getPriceHistory(
          @RequestParam(value = "productId", required = false)
          @Pattern(regexp = "^[A-Z0-9]{1,20}$", message = "Product ID must be alphanumeric and between 1 and 20 characters.")
          String productId,
          @RequestParam(value = "storeName", required = false)
          @Pattern(regexp = "^[A-Za-z0-9\\s]{1,100}$", message = "Store name must be alphanumeric and between 1 and 100 characters.")
          String storeName,
          @RequestParam(value = "productCategory", required = false)
          @Pattern(regexp = "^[A-Za-z0-9\\s]{1,100}$", message = "Product description must be alphanumeric and between 1 and 100 characters.")
          String productCategory,
          @RequestParam(value = "brand", required = false)
          @Pattern(regexp = "^[A-Za-z0-9\\s]{1,100}$", message = "Brand name must be alphanumeric and between 1 and 100 characters.")
          String brand
    ) {
        List<GetPriceHistoryDto> priceEntries = priceEntryService.getPriceHistory(productId, storeName, productCategory, brand);

        return ResponseEntity.ok(priceEntries);
    }

}
