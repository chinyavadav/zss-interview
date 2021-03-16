package zw.co.zss.interview.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl {
    @Autowired
    private CategoryRepository categoryRepository;

    // Create & Update
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    // Read
    public Category findCategoryById(long categoryId) {
        return categoryRepository.findById(categoryId).orElse(null);
    }

    // Delete
    public void deleteCategory(Category category) {
        categoryRepository.delete(category);
    }
}
