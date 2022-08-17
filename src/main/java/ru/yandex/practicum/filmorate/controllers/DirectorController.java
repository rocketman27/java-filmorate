package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.models.Director;
import ru.yandex.practicum.filmorate.services.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping("/{id}")
    public Director getById(@PathVariable(name = "id") long directorId) {
        return directorService.getById(directorId);
    }

    @GetMapping
    public List<Director> getAll() {
        return directorService.getAll();
    }

    @PostMapping
    public Director add(@Valid @RequestBody Director director) {
        return directorService.add(director);
    }

    @PutMapping
    public Director update(@Valid @RequestBody Director director) {
        return directorService.update(director);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable(name = "id") long directorId) {
        directorService.deleteById(directorId);
    }
}
