@Configuration
@EnableConfigurationProperties({PrimaryDataSourceProperties.class})
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef="entityManagerFactory",
        basePackages={"com.primary.repo"}
)
public class PrimaryDataSource {

    @Autowired
    PrimaryDataSourceProperties dataSourceProp;

    @Primary
    @Bean(name="dataSource")
    public DataSource primaryDataSource(){
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setUserName(dataSourceProp.getUsername());
        hikariConfig.setPassword(dataSourceProp.getPassword());
        hikariConfig.setJdbcUrl(dataSourceProp.getUrl());
        hikariConfig.setDriverClassName(dataSourceProp.getDriverClassName());
        hikariConfig.setMaximumPoolSize(dataSourceProp.getMaxPoolSize());
        hikariConfig.setMinimumIdle(dataSourceProp.getMinimumIdle());
        hikariConfig.setConnectionTimeout(dataSourceProp.getConnectionTimeOut());
        hikariConfig.setIdleTimeout(dataSourceProp.getIdleTimeOut());
        hikariConfig.setPoolName(dataSourceProp.getPoolName());
        return new HikariDataSource(hikariConfig);
    }

    @Primary
    @Bean(name="entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                       @Qualifier("dataSource") DataSource datasource){
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(datasource);
        em.setPackageToScan(new String[]{"com.primary.entity"});
        HibernateJpaVendorAdaptor vendorAdaptor = new HibernateJpaVendorAdaptor();
        em.setJpaVendorAdaptor(vendorAdaptor);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.OracleDialect");
        em.setJpaPropertyMap(properties);
        return em;
    }

    @Primary
    @Bean(name="transactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory){
        return new JpaTransactionManager(entityManagerFactory);
    }

}