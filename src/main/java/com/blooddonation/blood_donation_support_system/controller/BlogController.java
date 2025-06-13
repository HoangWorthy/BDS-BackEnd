//package com.blooddonation.blood_donation_support_system.controller;
//
//import com.blooddonation.blood_donation_support_system.dto.AccountDto;
//import com.blooddonation.blood_donation_support_system.dto.BlogDto;
//import com.blooddonation.blood_donation_support_system.service.BlogService;
//import com.blooddonation.blood_donation_support_system.util.JwtUtil;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//@RestController
//@RequestMapping("/api/blog")
//public class BlogController {
//    @Autowired
//    private BlogService blogService;
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<?> createBlog(
//            @RequestPart("blog") @Valid BlogDto blogDto,
//            @RequestPart(value = "image", required = false) MultipartFile image,
//            @CookieValue("jwt-token") String jwtToken) {
//        try {
//            AccountDto accountDto = jwtUtil.extractUser(jwtToken);
//            BlogDto result = blogService.create(blogDto, accountDto, image);
//            return ResponseEntity.status(HttpStatus.CREATED).body(result);
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("An unexpected error occurred while creating the blog");
//        }
//    }
//
//}
