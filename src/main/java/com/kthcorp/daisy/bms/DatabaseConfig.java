package com.kthcorp.daisy.bms;

import com.kthcorp.daisy.bms.properties.BmsDatabaseProperties;
import com.kthcorp.daisy.bms.properties.DatabaseProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * Created by devjackie on 2018. 5. 3..
 */
@Slf4j
public abstract class DatabaseConfig {

    abstract void initialize(org.apache.tomcat.jdbc.pool.DataSource dataSource);

    protected void configureDataSource(org.apache.tomcat.jdbc.pool.DataSource dataSource, DatabaseProperties databaseProperties) {
        log.debug("databaseProperties.getDriverClassName() : {}", databaseProperties.getDriverClassName());
        log.debug("databaseProperties.getUrl() : {}", databaseProperties.getUrl());
        log.debug("databaseProperties.getUserName() : {}", databaseProperties.getUserName());
        dataSource.setDriverClassName(databaseProperties.getDriverClassName());
        dataSource.setUrl(databaseProperties.getUrl());
        dataSource.setUsername(databaseProperties.getUserName());
        dataSource.setPassword(databaseProperties.getPassword());
        dataSource.setMaxActive(databaseProperties.getMaxActive());
        dataSource.setMaxIdle(databaseProperties.getMaxIdle());
        dataSource.setMinIdle(databaseProperties.getMinIdle());
        dataSource.setMaxWait(databaseProperties.getMaxWait());
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);

        if(databaseProperties.isInitialize())
            initialize(dataSource);
    }
}

@Configuration
@EnableConfigurationProperties(BmsDatabaseProperties.class)
class BmsDatabaseConfig extends DatabaseConfig {

    @Autowired
    private BmsDatabaseProperties bmsDatabaseProperties;

    @Override
    protected void initialize(org.apache.tomcat.jdbc.pool.DataSource dataSource) {
//        PathMatchingResourcePatternResolver pathResolver = new PathMatchingResourcePatternResolver();
////        Resource schema = pathResolver.getResource("classpath:script/bms-user-schema.sql");
//        Resource data = pathResolver.getResource("classpath:script/bms-user-data.sql");
////        DatabasePopulator databasePopulator = new ResourceDatabasePopulator(schema, data);
//        DatabasePopulator databasePopulator = new ResourceDatabasePopulator(data);
//        DatabasePopulatorUtils.execute(databasePopulator, dataSource);
    }

    @Bean(name = "bmsDataSource", destroyMethod = "close")
    @Primary
    public DataSource dataSource() {
        org.apache.tomcat.jdbc.pool.DataSource bmsDataSource = new org.apache.tomcat.jdbc.pool.DataSource();
        configureDataSource(bmsDataSource, bmsDatabaseProperties);
        return bmsDataSource;
    }

    @Bean(name = "bmsTransactionManager")
    @Primary
    public PlatformTransactionManager bmsTransactionManager(@Qualifier("bmsDataSource") DataSource bmsDataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(bmsDataSource);
        transactionManager.setGlobalRollbackOnParticipationFailure(false);
        return transactionManager;
    }
}
