package ru.practicum.main.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PageRequestUtil {
    public static Pageable of(int from, int size) {
        return PageRequest.of(from / size, size);
    }
}