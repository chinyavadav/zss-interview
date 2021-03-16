package zw.co.zss.interview.category;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zw.co.zss.interview.book.Book;
import zw.co.zss.interview.book.dto.BookDTO;
import zw.co.zss.interview.category.dto.CategoryDTO;
import zw.co.zss.interview.common.ResponseTemplate;

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


    public ResponseTemplate<Category> createBook(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        Category savedCategory = saveCategory(category);
        return new ResponseTemplate<>("success", "Category successfully added!", savedCategory);
    }
}
