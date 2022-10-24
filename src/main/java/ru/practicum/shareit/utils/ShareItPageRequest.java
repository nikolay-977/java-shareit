package ru.practicum.shareit.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class ShareItPageRequest extends PageRequest {
    private final int offset;

    public ShareItPageRequest(int offset, int size, Sort sort) {
        super(offset / size, size, sort);
        this.offset = offset;
    }

    public static ShareItPageRequest of(int offset, int size) {
        return new ShareItPageRequest(offset, size, Sort.unsorted());
    }

    @Override
    public long getOffset() {
        return offset;
    }
}
