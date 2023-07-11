package ru.practicum.transfer;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageSort {
    public static Pageable getPageable(Integer from, Integer size, Sort sort) {
        return PageRequest.of(from / size, size, sort);
    }
}
