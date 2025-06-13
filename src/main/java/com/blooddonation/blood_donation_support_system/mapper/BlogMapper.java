package com.blooddonation.blood_donation_support_system.mapper;

import com.blooddonation.blood_donation_support_system.dto.BlogDto;
import com.blooddonation.blood_donation_support_system.entity.Account;
import com.blooddonation.blood_donation_support_system.entity.Blog;
import org.springframework.stereotype.Component;

@Component
public class BlogMapper {
    public static Blog toEntity(BlogDto blogDto, Account account) {
        if (blogDto == null) return null;

        return Blog.builder()
                .id(blogDto.getId())
                .author(account)
                .title(blogDto.getTitle())
                .content(blogDto.getContent())
                .imagePath(blogDto.getImagePath())
                .build();
    }

    public static BlogDto toDto(Blog blog) {
        if (blog == null) return null;

        return BlogDto.builder()
                .id(blog.getId())
                .authorId(blog.getAuthor().getId())
                .title(blog.getTitle())
                .content(blog.getContent())
                .imagePath(blog.getImagePath())
                .status(blog.getStatus())
                .creationDate(blog.getCreationDate())
                .lastModifiedDate(blog.getLastModifiedDate())
                .build();
    }

}
