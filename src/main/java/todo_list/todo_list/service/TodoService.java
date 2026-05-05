package todo_list.todo_list.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import todo_list.todo_list.entity.Todo;
import todo_list.todo_list.repository.TodoRepository;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository repo;
    private final CloudinaryService cloudinaryService;

    // CREATE
    public Todo create(String title, String description, MultipartFile file) {

        Map uploadResult = cloudinaryService.uploadFile(file);

        Todo todo = new Todo();
        todo.setTitle(title);
        todo.setDescription(description);
        todo.setImageUrl(uploadResult.get("secure_url").toString());
        todo.setPublicId(uploadResult.get("public_id").toString());
        todo.setCompleted(false);

        return repo.save(todo);
    }

    // GET ALL
    public List<Todo> getAll() {
        return repo.findAll();
    }

    // UPDATE (text + image optional)
    public Todo update(Long id, String title, String description, MultipartFile file) {

        Todo todo = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        todo.setTitle(title);
        todo.setDescription(description);

        // 🔥 IF new image uploaded → replace old image
        if (file != null && !file.isEmpty()) {

            // 1. delete old image from Cloudinary
            if (todo.getPublicId() != null) {
                cloudinaryService.deleteFile(todo.getPublicId());
            }

            // 2. upload new image
            Map result = cloudinaryService.uploadFile(file);

            todo.setImageUrl(result.get("secure_url").toString());
            todo.setPublicId(result.get("public_id").toString());
        }

        return repo.save(todo);
    }

    // DELETE (DB + Cloudinary)
    public void delete(Long id) {

        Todo todo = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        // 1. delete image from Cloudinary
        if (todo.getPublicId() != null) {
            cloudinaryService.deleteFile(todo.getPublicId());
        }

        // 2. delete from PostgreSQL
        repo.deleteById(id);
    }
}