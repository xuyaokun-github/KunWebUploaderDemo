package com.kunghsu.kunwebuploaderdemo;

import com.kunghsu.kunwebuploaderdemo.servlet.FileUploadServlet;
import com.kunghsu.kunwebuploaderdemo.servlet.UploadActionServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class KunwebuploaderdemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(KunwebuploaderdemoApplication.class, args);
	}


	//定义servlet，注册
	@Bean
	public ServletRegistrationBean fileUploadServlet(){
		//指定访问的url
		return new ServletRegistrationBean(new FileUploadServlet(),"/FileUploadServlet");
	}

	@Bean
	public ServletRegistrationBean uploadActionServlet(){
		return new ServletRegistrationBean(new UploadActionServlet(),"/UploadActionServlet");
	}
}
