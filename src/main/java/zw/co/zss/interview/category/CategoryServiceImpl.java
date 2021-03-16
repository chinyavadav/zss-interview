package zw.co.zss.interview.category;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import zw.co.zss.interview.category.dto.CategoryDTO;
import zw.co.zss.interview.common.ResponseTemplate;
import zw.co.zss.interview.exception.CustomException;

import java.util.List;

@Service
public class CategoryServiceImpl {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

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

    public ResponseTemplate<Category> createCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        Category savedCategory = saveCategory(category);
        return new ResponseTemplate<>("success", "Category successfully added!", savedCategory);
    }

    public ResponseTemplate<Category> updateCategory(long categoryId, CategoryDTO categoryDTO) {
        Category category = findCategoryById(categoryId);
        if (category != null) {
            category = modelMapper.map(categoryDTO, Category.class);
            Category savedCategory = saveCategory(category);
            return new ResponseTemplate<>("success", "Category successfully updated!", savedCategory);
        }
        throw new CustomException("Category does not exist!", HttpStatus.NOT_FOUND);
    }

    // Fetches all Books
    public ResponseTemplate<List<Category>> getCategories() {
        List<Category> categories = categoryRepository.findAll();
        return new ResponseTemplate<>("success", "Categories Found!", categories);
    }
}
