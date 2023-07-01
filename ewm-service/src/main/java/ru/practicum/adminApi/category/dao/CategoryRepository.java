package ru.practicum.adminApi.category.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.adminApi.category.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
