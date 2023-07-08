package ru.practicum.transfer;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageSort {
    public static Pageable getPageable(Integer from, Integer size, Sort sort) {
        if (from > 0 && size > 0) from = from / size;
        return PageRequest.of(from, size, sort);
    }
}
