package com.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class MysqlCondition implements Condition {

    public MysqlCondition() {
    }

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String jdbcUrl = context.getEnvironment().getProperty("spring.datasource.url");
        String jdbcUsername = context.getEnvironment().getProperty("spring.datasource.username");
        String jdbcPassword = context.getEnvironment().getProperty("spring.datasource.password");
        String jdbcDriver = context.getEnvironment().getProperty("spring.datasource.driver-class-name");
        System.out.println("MysqlCondition jdbcUrl >>>> " + jdbcUrl);
        return StringUtils.isNotBlank(jdbcUrl) && StringUtils.isNotBlank(jdbcUsername) && StringUtils.isNotBlank(jdbcPassword)
                && StringUtils.isNotBlank(jdbcDriver);
    }
}
