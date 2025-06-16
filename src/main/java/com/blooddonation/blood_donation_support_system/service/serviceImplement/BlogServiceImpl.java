package com.blooddonation.blood_donation_support_system.service.serviceImplement;

import com.blooddonation.blood_donation_support_system.dto.BlogDto;
import com.blooddonation.blood_donation_support_system.entity.Account;
import com.blooddonation.blood_donation_support_system.entity.Blog;
import com.blooddonation.blood_donation_support_system.mapper.BlogMapper;
import com.blooddonation.blood_donation_support_system.repository.AccountRepository;
import com.blooddonation.blood_donation_support_system.repository.BlogRepository;
import com.blooddonation.blood_donation_support_system.service.BlogService;
import com.blooddonation.blood_donation_support_system.validator.UserValidator;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import java.util.regex.Matcher;


@Service
@Slf4j
public class BlogServiceImpl implements BlogService {

    @Value("${upload.dir}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @Autowired
    private BlogRepository blogRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserValidator validator;

    public String createBlog(BlogDto blogDto, MultipartFile thumbnail, String authorEmail) {
        Account author = validator.getEmailOrThrow(authorEmail);

        // Save thumbnail
        try {
            String thumbnailName = UUID.randomUUID() + "_" + thumbnail.getOriginalFilename();
            Path thumbnailPath = Paths.get(uploadDir, thumbnailName);
            Files.copy(thumbnail.getInputStream(), thumbnailPath, StandardCopyOption.REPLACE_EXISTING);

            blogDto.setThumbnail("/uploads/" + thumbnailName);

            // Map to entity
            Blog blog = BlogMapper.toEntity(blogDto, author);

            // Save and return DTO
            blogRepository.save(blog);
            return "Blog created successfully";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String updateBlog(Long blogId, BlogDto blogDto, MultipartFile thumbnail) {
        Blog blog = validator.getBlogOrThrow(blogId);
        try {
            // Handle thumbnail update
            String oldThumbnail = blog.getThumbnail();
            if (thumbnail != null && !thumbnail.isEmpty()) {
                String thumbnailName = UUID.randomUUID() + "_" + thumbnail.getOriginalFilename();
                Path thumbnailPath = Paths.get(uploadDir, thumbnailName);
                Files.copy(thumbnail.getInputStream(), thumbnailPath, StandardCopyOption.REPLACE_EXISTING);
                blogDto.setThumbnail("/uploads/" + thumbnailName);

                if (oldThumbnail != null) {
                    Path oldThumbnailPath = Paths.get(uploadDir, oldThumbnail.replace("/uploads/", ""));
                    Files.deleteIfExists(oldThumbnailPath);
                }
            }

            // Handle content images
            Set<String> oldImages = extractImageUrls(blog.getContent());
            Set<String> newImages = extractImageUrls(blogDto.getContent());

            // Find and delete images that are no longer used
            oldImages.stream()
                    .filter(img -> !newImages.contains(img))
                    .forEach(img -> {
                        try {
                            Path imagePath = Paths.get(uploadDir, img.replace("/uploads/", ""));
                            Files.deleteIfExists(imagePath);
                        } catch (IOException e) {
                            log.warn("Failed to delete old image: " + img);
                        }
                    });

            BlogMapper.update(blogDto, blog);
            blogRepository.save(blog);
            return "Blog updated successfully";
        } catch (IOException e) {
            throw new RuntimeException("Failed to update blog: " + e.getMessage());
        }
    }

    private Set<String> extractImageUrls(String content) {
        if (content == null) return new HashSet<>();

        Set<String> urls = new HashSet<>();
        Pattern pattern = Pattern.compile("/uploads/[^\\s\"']+\\.(jpg|jpeg|png|gif)");
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            urls.add(matcher.group());
        }
        return urls;
    }
}

