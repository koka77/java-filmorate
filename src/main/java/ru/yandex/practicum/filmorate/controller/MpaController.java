package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.mpa.MpaService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/mpa")
public class MpaController {

    private final MpaService mpaService;

    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping("{id}")
    public Optional<Mpa> getMpaById(@PathVariable Integer id) {
        if (id < 1) {
            throw new ObjectNotFoundException(String.format("MPA not found with id: %s", id));
        }
        return mpaService.getById(id);
    }

    @GetMapping()
    public List<Mpa> getAll() {
        return mpaService.getAll();
    }
}
