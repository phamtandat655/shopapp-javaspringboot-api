package com.project.shopapp.configurations;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class LanguageConfig {
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n.messages"); // tên cơ sở của các tệp tài liệu ngôn ngữ
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}

// dùng để chuyển đổi đa ngôn ngữ vd 1 client cần tiếng anh, 1 client cần tiếng việt
// muốn dùng thì ở client khi gửi kèm thêm 1 header là "Accept-language"
// vd : header: {
//  'Content-Type' : 'appliacation/json',
//  'Accept-Language' : 'vi'
// }