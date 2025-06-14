//package com.blooddonation.blood_donation_support_system.service.serviceImplement;
//
//import com.blooddonation.blood_donation_support_system.dto.AccountDto;
//import com.blooddonation.blood_donation_support_system.dto.BlogDto;
//import com.blooddonation.blood_donation_support_system.entity.Account;
//import com.blooddonation.blood_donation_support_system.entity.Blog;
//import com.blooddonation.blood_donation_support_system.mapper.BlogMapper;
//import com.blooddonation.blood_donation_support_system.repository.AccountRepository;
//import com.blooddonation.blood_donation_support_system.repository.BlogRepository;
//import com.blooddonation.blood_donation_support_system.service.BlogService;
//import com.blooddonation.blood_donation_support_system.validator.UserValidator;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardCopyOption;
//import java.util.UUID;
//
//@Service
//public class BlogServiceImpl implements BlogService {
//    @Value("${upload.dir}")
//    private String uploadDir;
//
//    @Autowired
//    private BlogRepository blogRepository;
//    @Autowired
//    private AccountRepository accountRepository;
//    @Autowired
//    private UserValidator validator;
//
//    @Override
//    public BlogDto create(BlogDto blogDto, AccountDto accountDto, MultipartFile image) {
//        Account account = validator.getUserOrThrow(accountDto.getId());
//
//        try {
//            if (image != null && !image.isEmpty()) {
//                String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();
//                Path filePath = Paths.get(uploadDir, filename);
//                Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
//                blogDto.setImagePath(filename);
//            }
//
//            Blog blog = BlogMapper.toEntity(blogDto, account);
//            Blog savedBlog = blogRepository.save(blog);
//            return BlogMapper.toDto(savedBlog);
//
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to save image file", e);
//        }
//    }
//}
