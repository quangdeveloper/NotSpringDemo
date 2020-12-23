package vn.vnpay.notspringdemo.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
public class DBConfig {

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
    public DataSource dataSource() {

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

        /**
         * useServerPrepStmts - Sử dụng các câu lệnh chuẩn bị từ phía máy chủ nếu máy chủ hỗ trợ chúng?
         * cachePrepStmts - Trình điều khiển có nên lưu vào bộ nhớ cache giai đoạn phân tích cú pháp của
         * PreparedStatements của các câu lệnh được chuẩn bị từ phía máy khách, "kiểm tra" tính phù hợp của các
         * câu lệnh được chuẩn bị từ phía máy chủ
         */
//        hikariConfig.addDataSourceProperty("dataSource.cachePrepStmts", cachePrepStmts);
//        hikariConfig.addDataSourceProperty("dataSource.prepStmtCacheSize", prepStmtCacheSize);
//        hikariConfig.addDataSourceProperty("dataSource.prepStmtCacheSqlLimit", prepStmtCacheSqlLimit);
//        hikariConfig.addDataSourceProperty("dataSource.useServerPrepStmts", userServerPrepStmts);

        return new HikariDataSource(hikariConfig);
    }




    @Bean
    @Primary
    DataSourceTransactionManager dataSourceTransactionManager() {

        DataSourceTransactionManager manager = new DataSourceTransactionManager();
        manager.setDataSource(dataSource());
        manager.setRollbackOnCommitFailure(true);
        return manager;
    }

    @Bean
    JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

}
