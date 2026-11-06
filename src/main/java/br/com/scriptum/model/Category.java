package br.com.scriptum.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories") // Garante o nome correto no banco de dados
public class Category extends PanacheEntity {

    @Column(nullable = false)
    public String title;
    
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore 
    public List<Document> documents = new ArrayList<>();
}