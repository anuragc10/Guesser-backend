// package com.guesser.demo.config;
//
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.servlet.config.annotation.CorsRegistry;
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
// /**
//  * Global CORS configuration to ensure credentialed requests work from allowed origins.
//  */
// @Configuration
// public class CorsConfig {
//
//     @Bean
//     public WebMvcConfigurer corsConfigurer() {
//         return new WebMvcConfigurer() {
//             @Override
//             public void addCorsMappings(CorsRegistry registry) {
//                 registry.addMapping("/**")
//                         .allowedOrigins(
//                                 "https://number-guesser-8ysi1tj6z-anurags-projects-e0f6082e.vercel.app'",
//                                 "https://ana-snuffier-henry.ngrok-free.dev"
//                         )
//                         .allowedOriginPatterns(
//                                 "https://*.ngrok-free.dev",
//                                 "https://*.vercel.app",
//                                 "http://localhost:*",
//                                 "http://127.0.0.1:*"
//                         )
//                         .allowedMethods("*")
//                         .allowedHeaders("*")
//                         .allowCredentials(true);
//             }
//         };
//     }
// }
//
