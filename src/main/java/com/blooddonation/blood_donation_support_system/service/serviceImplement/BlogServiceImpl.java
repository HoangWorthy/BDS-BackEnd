package com.blooddonation.blood_donation_support_system.service.serviceImplement;

import com.blooddonation.blood_donation_support_system.dto.AccountDto;
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
import java.time.LocalDate;
import java.util.UUID;

@Service
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

    public BlogDto createBlog(BlogDto blogDto, MultipartFile thumbnail, String authorEmail) {
        // Get author account from email
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
            Blog saved = blogRepository.save(blog);
            return BlogMapper.toDto(saved);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

