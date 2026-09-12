package ru.practicum.main.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PageRequestUtil {
    public static Pageable of(int from, int size) {
        if (size <= 0) {
            size = 10;
        }
        if (from < 0) {
            from = 0;
        }
        return PageRequest.of(from / size, size);
    }
}