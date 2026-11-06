package br.com.scriptum.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "documents")
public class Document extends PanacheEntity {

    @Column(nullable = false)
    public String title;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "document_paragraphs", joinColumns = @JoinColumn(name = "document_id"))
    @Column(name = "paragraph", columnDefinition = "TEXT")
    public List<String> content = new ArrayList<>();

    @Column(nullable = false)
    public String status; 

    public Double reliability;

    @ManyToOne
    @JoinColumn(name = "category_id")
    public Category category;

    public LocalDateTime createdAt;

    public Document() {
        this.createdAt = LocalDateTime.now();
    }
}