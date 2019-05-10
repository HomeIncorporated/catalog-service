package eu.nimble.service.catalogue.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;

@Configuration
@EnableConfigurationProperties
@PropertySource("classpath:bootstrap.yml")
//@EnableJpaRepositories(
//        entityManagerFactoryRef = "ubldbEntityManagerFactory",
//        transactionManagerRef = "ubldbTransactionManager",
//        basePackages = {"eu.nimble.service.catalogue.persistence", "eu.nimble.utility.persistence.resource"}
//)
@ComponentScan(basePackages = {"eu.nimble.service.catalogue.persistence"})
class UBLDBConfig {

    @Autowired
    private DataSourceFactory dataSourceFactory;

    @Bean(name = "ubldbDataSource")
    @Primary
//    @ConfigurationProperties(prefix = "persistence.orm.ubl.hibernate.connection")
    public DataSource getDataSource() {
        return dataSourceFactory.createDatasource("ubldb");
    }

    @Primary
    @Bean(name = "ubldbEmfBean")
    public LocalContainerEntityManagerFactoryBean ubldbEntityManagerFactoryBean(
            EntityManagerFactoryBuilder builder,
            @Qualifier("ubldbHibernateConfigs") Map hibernateConfigs,
            @Qualifier("ubldbDataSource") DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean emfBean = builder
                .dataSource(dataSource)
                .persistenceUnit(eu.nimble.utility.Configuration.UBL_PERSISTENCE_UNIT_NAME)
                .packages("eu.nimble.service.model.ubl")
                .build();

        Properties hibernateProperties = new Properties();
        hibernateProperties.putAll(hibernateConfigs);
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        emfBean.setJpaVendorAdapter(vendorAdapter);
        emfBean.setJpaProperties(hibernateProperties);
        return emfBean;
    }

    @Bean(name = "ubldbLazyDisabledEmfBean")
    public LocalContainerEntityManagerFactoryBean ubldbLazyDisabledEntityManagerFactoryBean(
            EntityManagerFactoryBuilder builder,
            @Qualifier("ubldbHibernateConfigs") Map hibernateConfigs,
            @Qualifier("ubldbDataSource") DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean emfBean = builder
                .dataSource(dataSource)
                .persistenceUnit(eu.nimble.utility.Configuration.UBL_PERSISTENCE_UNIT_NAME)
                .packages("eu.nimble.service.model.ubl")
                .build();

        Properties hibernateProperties = new Properties();
        hibernateProperties.putAll(hibernateConfigs);
        // enable hibernate.enable_lazy_load_no_trans property
        hibernateProperties.put("hibernate.enable_lazy_load_no_trans",true);
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        emfBean.setJpaVendorAdapter(vendorAdapter);
        emfBean.setJpaProperties(hibernateProperties);
        return emfBean;
    }

    @Bean(name = "ubldbEntityManagerFactory")
    public EntityManagerFactory ubldbEntityManagerFactory(@Qualifier("ubldbEmfBean") LocalContainerEntityManagerFactoryBean emfBean) {
        return emfBean.getObject();
    }

    @Bean(name = "ubldbLazyDisabledEntityManagerFactory")
    public EntityManagerFactory ubldbLazyDisabledEntityManagerFactory(@Qualifier("ubldbLazyDisabledEmfBean") LocalContainerEntityManagerFactoryBean emfBean) {
        return emfBean.getObject();
    }
//
//    @Bean(name = "ubldbTransactionManager")
//    PlatformTransactionManager ubldbTransactionManager(
//            @Qualifier("ubldbEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
//        return new JpaTransactionManager(entityManagerFactory);
//    }
}