package vn.vnpay.notspringdemo.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
public class DBConfig {

    @Bean
    public DataSource dataSource() {

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("oracle.jdbc.OracleDriver");
        hikariConfig.setJdbcUrl("jdbc:oracle:thin:@10.22.7.63:1521/GTGTTEST");
        hikariConfig.setUsername("mms");
        hikariConfig.setPassword("mms");
        hikariConfig.setPoolName("hikariPoolTest");
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setAllowPoolSuspension(true);
        hikariConfig.setMinimumIdle(5);
        hikariConfig.setIdleTimeout(30000);
        hikariConfig.setConnectionTimeout(30000);
        hikariConfig.setMaxLifetime(200000);
        hikariConfig.setAutoCommit(false);

//        hikariConfig.addDataSourceProperty("dataSource.cachePrepStmts", "true");
//        hikariConfig.addDataSourceProperty("dataSource.prepStmtCacheSize", "250");
//        hikariConfig.addDataSourceProperty("dataSource.prepStmtCacheSqlLimit", "2048");
//        hikariConfig.addDataSourceProperty("dataSource.useServerPrepStmts", "true");

        return new HikariDataSource(hikariConfig);
    }

    /**
     * useServerPrepStmts - Sử dụng các câu lệnh chuẩn bị từ phía máy chủ nếu máy chủ hỗ trợ chúng?
     * <p>
     * cachePrepStmts - Trình điều khiển có nên lưu vào bộ nhớ cache giai đoạn phân tích cú pháp của PreparedStatements
     * của các câu lệnh được chuẩn bị từ phía máy khách, "kiểm tra" tính phù hợp của các câu lệnh được chuẩn bị từ phía
     * máy chủ
     */


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
