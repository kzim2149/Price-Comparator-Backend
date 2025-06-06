package com.codingchallenge.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "processed_files")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessedFile {
    @Id
    private String id;
    private String fileName;
    private LocalDate processedDate;
}
