package com.ttms.Config;

import com.ttms.Filter.ChargerOpeProductFilter;
import com.ttms.Filter.DistributorAuthInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Bean
    public ChargerOpeProductFilter createChargerOpeProductFilter(){
        return new ChargerOpeProductFilter();
    }

    @Bean
    public DistributorAuthInterceptor createDistributorAuthInterceptor(){return new  DistributorAuthInterceptor();}

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        //只让负责人对产品相关一切进行操作
        registry.addInterceptor(this.createChargerOpeProductFilter()).addPathPatterns("/producemanage/product/productlist/privilege/**");

        //分销商操作需认证
        registry.addInterceptor(this.createDistributorAuthInterceptor()).addPathPatterns("/distributorEntry/auth/**");
    }
}
