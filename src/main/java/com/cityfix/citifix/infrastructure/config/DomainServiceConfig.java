package com.cityfix.citifix.infrastructure.config;

import com.cityfix.citifix.domain.service.DefaultImpactRewardPolicy;
import com.cityfix.citifix.domain.service.ImpactRewardPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServiceConfig {

    @Bean
    public ImpactRewardPolicy impactRewardPolicy() {
        return new DefaultImpactRewardPolicy();
    }
}
