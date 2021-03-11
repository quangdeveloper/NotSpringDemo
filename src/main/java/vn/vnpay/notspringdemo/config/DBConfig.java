package vn.vnpay.notspringdemo.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Configuration
public class DBConfig {

    private final Logger logger = LoggerFactory.getLogger(DBConfig.class);

    @Value("${database.driver-class-name}")
    private String driverClassName;

    @Value("${database.url}")
    private String urlConnect;

    @Value("${database.username}")
    private String username;

    @Value("${database.password}")
    private String password;

    @Value("${database.pool.name}")
    private String poolName;

    @Value("${database.pool.maximun-pool-size}")
    private int maximumPoolSize;

    @Value("${database.pool.allow-pool-suspension}")
    private Boolean allowPoolSuspension;

    @Value("${database.pool.minimun-idle}")
    private int minimunIdle;

    @Value("${database.pool.idle-timeout}")
    private long idleTimeout;

    @Value("${database.pool.connection-timeout}")
    private long connectionTimeout;

    @Value("${database.pool.max-life-time}")
    private long maxLifeTime;

    @Value("${database.pool.auto-commit}")
    private Boolean isAutoCommit;

    @Value("${database.cache-prep-stmts}")
    private String cachePrepStmts;

    @Value("${database.prep-stmt-cache-size}")
    private String prepStmtCacheSize;

    @Value("${database.prep-stmt-cache-sql-limit}")
    private String prepStmtCacheSqlLimit;

    @Value("${database.use-server-prep-stmts}")
    private String userServerPrepStmts;

    @Bean
    public DataSource configDataSource() {

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(driverClassName);
        hikariConfig.setJdbcUrl(urlConnect);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setPoolName(poolName);
        hikariConfig.setMaximumPoolSize(maximumPoolSize);
        hikariConfig.setAllowPoolSuspension(allowPoolSuspension);
        hikariConfig.setMinimumIdle(minimunIdle);
        hikariConfig.setIdleTimeout(idleTimeout);
        hikariConfig.setConnectionTimeout(connectionTimeout);
        hikariConfig.setMaxLifetime(maxLifeTime);
        hikariConfig.setAutoCommit(isAutoCommit);
        hikariConfig.addDataSourceProperty("cachePrepStmts", cachePrepStmts);
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", prepStmtCacheSize);
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", prepStmtCacheSqlLimit);
        hikariConfig.addDataSourceProperty("useServerPrepStmts", userServerPrepStmts);

        return new HikariDataSource(hikariConfig);
    }

    @Bean
    @Primary
    DataSourceTransactionManager configDataSourceTransactionManager() {
        DataSourceTransactionManager manager = new DataSourceTransactionManager();
        manager.setDataSource(configDataSource());
        manager.setRollbackOnCommitFailure(true);
        return manager;
    }
}
