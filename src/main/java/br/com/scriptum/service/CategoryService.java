package br.com.scriptum.service;

import br.com.scriptum.model.Category;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class CategoryService {

    @Transactional
    public Category createCategory(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty.");
        }
        
        Category category = new Category();
        category.title = title;
        category.persist();
        
        return category;
    }

    public List<Category> listCategories() {
        return Category.listAll();
    }
}