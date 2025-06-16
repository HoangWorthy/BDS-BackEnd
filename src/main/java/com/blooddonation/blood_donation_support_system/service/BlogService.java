package com.blooddonation.blood_donation_support_system.service;

import com.blooddonation.blood_donation_support_system.dto.AccountDto;
import com.blooddonation.blood_donation_support_system.dto.BlogDto;
import org.springframework.web.multipart.MultipartFile;

public interface BlogService {
    String createBlog(BlogDto blogDto, MultipartFile thumbnail, String authorEmail);
    String updateBlog(Long blogId, BlogDto blogDto, MultipartFile thumbnail);
}
