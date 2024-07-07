package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "news")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class News {

    @Id
    private String id;
    private String title;
    private String summary;
    private List<String> categories;
}