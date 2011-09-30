package org.zebra.search.crawler.plugin.dbstorage;

import org.hibernate.cfg.AnnotationConfiguration;

public class HibernateTestUtil {
    public static AnnotationConfiguration createMySqlAnnotationConfiguration() {
        return  (AnnotationConfiguration) new AnnotationConfiguration()
            .setProperty("hibernate.dialect",
                    "org.hibernate.dialect.MySQL5Dialect")
            .setProperty("hibernate.connection.driver_class",
                    "com.mysql.jdbc.Driver")
            .setProperty("hibernate.connection.url",
                    "jdbc:mysql://localhost/financeDB?useUnicode=true&characterEncoding=gb2312")
            .setProperty("hibernate.connection.username", "root")
            .setProperty("hibernate.connection.password", "")
            .setProperty("hibernate.connection.pool_size", "5")
            .setProperty("hibernate.connection.autocommit", "true")
            .setProperty("hibernate.show_sql", "true")
            .setProperty("format_sql", "false")
            .setProperty("hibernate.cache.provider_class",
                    "org.hibernate.cache.HashtableCacheProvider")
            .setProperty("hibernate.hbm2ddl.auto", "create")
            .setProperty("hibernate.jdbc.batch_size", "10")
            .setProperty("hibernate.current_session_context_class", "thread");
    }

    public static AnnotationConfiguration createInMemoryAnnotationConfiguration() {
        return  (AnnotationConfiguration) new AnnotationConfiguration()
            .setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect")
            .setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver")
            .setProperty("hibernate.connection.url", "jdbc:hsqldb:mem:test")
            .setProperty("hibernate.connection.pool_size", "5")
            .setProperty("hibernate.connection.autocommit", "true")
            .setProperty("hibernate.show_sql", "false")
            .setProperty("format_sql", "false")
            .setProperty("hibernate.cache.provider_class", "org.hibernate.cache.HashtableCacheProvider")
            .setProperty("hibernate.hbm2ddl.auto", "create-drop")
            .setProperty("hibernate.current_session_context_class", "thread");
    }
}