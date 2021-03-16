package zw.co.zss.interview.category;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import zw.co.zss.interview.book.Book;
import zw.co.zss.interview.category.dto.CategoryDTO;
import zw.co.zss.interview.common.ResponseTemplate;

import java.util.List;

@RestController
@RequestMapping("/category")
@CrossOrigin(origins = "*")
@Api(tags = "category")
@Validated
public class CategoryController {
    @Autowired
    private CategoryServiceImpl categoryService;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Fetches all Categories", response = ResponseTemplate.class)
    public ResponseTemplate<List<Category>> getAllCategories() {
        return categoryService.getCategories();
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Create new Category", response = ResponseTemplate.class)
    public ResponseTemplate<Category> createCategory(@ApiParam("CategoryDTO") @RequestBody CategoryDTO categoryDTO) {
        return categoryService.createCategory(categoryDTO);
    }

    @PutMapping(path = "/{categoryId}", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Updates new Category", response = ResponseTemplate.class)
    public ResponseTemplate<Category> updateAccount(@ApiParam("UpdateAccountDTO") @RequestBody CategoryDTO categoryDTO, @ApiParam("categoryId") @PathVariable long categoryId) {
        return categoryService.updateCategory(categoryId, categoryDTO);
    }
}
