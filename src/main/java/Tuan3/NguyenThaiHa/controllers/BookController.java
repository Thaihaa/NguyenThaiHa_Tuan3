package Tuan3.NguyenThaiHa.controllers;

import Tuan3.NguyenThaiHa.entities.Book;
import Tuan3.NguyenThaiHa.services.BookService;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService = null;

    private boolean isBookInfoMissing(Book book) {
        return book.getTitle() == null || book.getAuthor() == null || book.getPrice() == null || book.getCategory() == null;
    }

    @GetMapping
    public String showAllBooks(@NotNull Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        return "book/list";
    }

    @GetMapping("/add")
    public String addBookForm(@NotNull Model model) {
        model.addAttribute("book", new Book());
        return "book/add";
    }

    @PostMapping("/add")
    public String addBook(@ModelAttribute("book") Book book, BindingResult bindingResult) {
        if (bindingResult.hasErrors() || isBookInfoMissing(book)) {
            // Nếu có lỗi về dữ liệu nhập từ form hoặc thông tin sách thiếu, trả về trang form thêm sách để hiển thị thông báo lỗi
            bindingResult.rejectValue("", "error.book", "Vui lòng nhập đủ thông tin của sách");
            return "book/add";
        }

        if (bookService.getBookById(book.getId()).isEmpty()) {
            bookService.addBook(book);
        }
        return "redirect:/books";
    }


    @GetMapping("/edit/{id}")
    public String editBookForm(@NotNull Model model, @PathVariable long id) {
        var book = bookService.getBookById(id).orElse(null);
        model.addAttribute("book", book != null ? book : new Book());
        return "book/edit";
    }

    @PostMapping("/edit")
    public String editBook(@ModelAttribute("book") Book book, BindingResult bindingResult) {
        if (bindingResult.hasErrors() || isBookInfoMissing(book)) {
            // Nếu có lỗi về dữ liệu nhập từ form hoặc thông tin sách thiếu, trả về trang form chỉnh sửa sách để hiển thị thông báo lỗi
            bindingResult.rejectValue("", "error.book", "Vui lòng nhập đủ thông tin của sách");
            return "book/edit";
        }

        bookService.updateBook(book);
        return "redirect:/books";
    }


    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable long id) {
        if (bookService.getBookById(id).isPresent())
            bookService.deleteBookById(id);
        return "redirect:/books";
    }

    @GetMapping("/search")
    public String searchBooks(@RequestParam("keyword") String keyword, Model model) {
        List<Book> searchResults = bookService.searchBooks(keyword);
        model.addAttribute("books", searchResults); // Thiết lập danh sách kết quả tìm kiếm trong model
        return "book/search";
    }
}
