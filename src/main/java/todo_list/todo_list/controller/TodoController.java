package todo_list.todo_list.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import todo_list.todo_list.entity.Todo;
import todo_list.todo_list.service.TodoService;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService service;

    // CREATE
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Todo create(
            @RequestParam String title,
            @RequestParam String description,
            @RequestPart("file")  MultipartFile file) {

        return service.create(title, description, file);
    }

    // GET ALL
    @GetMapping
    public List<Todo> getAll() {
        return service.getAll();
    }

    // UPDATE
    @PutMapping("/{id}")
    public Todo update(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false) MultipartFile file) {

        return service.update(id, title, description, file);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}