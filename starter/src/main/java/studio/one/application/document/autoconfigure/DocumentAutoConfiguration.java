package studio.one.application.document.autoconfigure;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import studio.one.application.document.autoconfigure.condition.ConditionalOnDocumentPersistence;
import studio.one.application.document.persistence.DocumentDao;
import studio.one.application.document.persistence.jdbc.DocumentDaoJdbc;
import studio.one.application.document.persistence.jpa.DocumentDaoJpa;
import studio.one.application.document.persistence.jpa.entity.DocumentEntity;
import studio.one.application.document.persistence.jpa.repo.DocumentBlockRepository;
import studio.one.application.document.persistence.jpa.repo.DocumentBlockVersionRepository;
import studio.one.application.document.persistence.jpa.repo.DocumentBodyRepository;
import studio.one.application.document.persistence.jpa.repo.DocumentBodyVersionRepository;
import studio.one.application.document.persistence.jpa.repo.DocumentPropertyRepository;
import studio.one.application.document.persistence.jpa.repo.DocumentRepository;
import studio.one.application.document.persistence.jpa.repo.DocumentVersionRepository;
import studio.one.application.document.web.DocumentExceptionHandler;
import studio.one.application.document.web.controller.DocumentController;
import studio.one.platform.autoconfigure.EntityScanRegistrarSupport;
import studio.one.platform.autoconfigure.I18nKeys;
import studio.one.platform.autoconfigure.PersistenceProperties;
import studio.one.platform.component.State;
import studio.one.platform.constant.PropertyKeys;
import studio.one.platform.constant.ServiceNames;
import studio.one.platform.service.I18n;
import studio.one.platform.util.I18nUtils;
import studio.one.platform.util.LogUtils;

@AutoConfiguration
@EnableConfigurationProperties(DocumentFeatureProperties.class)
@ComponentScan(
        basePackages = "studio.one.application.document",
        excludeFilters = {
            @Filter(type = FilterType.ANNOTATION, classes = RestController.class),
            @Filter(type = FilterType.ANNOTATION, classes = Controller.class),
            @Filter(type = FilterType.ANNOTATION, classes = RestControllerAdvice.class),
            @Filter(type = FilterType.ANNOTATION, classes = ControllerAdvice.class)
        })
@ConditionalOnProperty(prefix = PropertyKeys.Features.PREFIX + ".document", name = "enabled", havingValue = "true", matchIfMissing = false)
@Slf4j
public class DocumentAutoConfiguration {
 
    protected static final String FEATURE_NAME = "Document"; 
    
    @Configuration
    @AutoConfigureBefore(HibernateJpaAutoConfiguration.class)
    @ConditionalOnDocumentPersistence(PersistenceProperties.Type.jpa)
    static class JpaEntityScanConfig {
        @Bean
        static BeanDefinitionRegistryPostProcessor entityScanRegistrar(Environment env, ObjectProvider<I18n> i18nProvider) {
            I18n i18n = I18nUtils.resolve(i18nProvider);
            String entityKey = PropertyKeys.Features.PREFIX + ".document.entity-packages";
            String packageName = DocumentEntity.class.getPackage().getName();
            log.info(LogUtils.format(i18n, I18nKeys.AutoConfig.Feature.EntityScan.PREPARING, FEATURE_NAME, entityKey, packageName));
            return EntityScanRegistrarSupport.entityScanRegistrar( entityKey, packageName);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnBean(EntityManagerFactory.class)
    @ConditionalOnDocumentPersistence(PersistenceProperties.Type.jpa)
    @EnableJpaRepositories(basePackageClasses = { DocumentRepository.class})
    static class JpaWiring {
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnBean(EntityManagerFactory.class)
    @ConditionalOnDocumentPersistence(PersistenceProperties.Type.jpa)
    static class DocumentJpaDaoConfig {
        @Bean
        @ConditionalOnMissingBean(DocumentDao.class)
        DocumentDao documentDaoJpa(DocumentRepository docRepo,
                                   DocumentVersionRepository verRepo,
                                   DocumentBodyRepository bodyRepo,
                                   DocumentBodyVersionRepository bodyVerRepo,
                                   DocumentPropertyRepository propRepo,
                                   DocumentBlockRepository blockRepo,
                                   DocumentBlockVersionRepository blockVersionRepo,
                                   EntityManager em, 
                                   ObjectProvider<I18n> i18nProvider) {
                                    I18n i18n = I18nUtils.resolve(i18nProvider);
            log.info(LogUtils.format(i18n, I18nKeys.AutoConfig.Feature.Service.DETAILS, FEATURE_NAME,
                                LogUtils.blue(DocumentDaoJpa.class, true),
                                LogUtils.red(State.CREATED.toString())));
            return new DocumentDaoJpa(docRepo, verRepo, bodyRepo, bodyVerRepo, propRepo, blockRepo, blockVersionRepo, em);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnDocumentPersistence(PersistenceProperties.Type.jdbc)
    static class DocumentJdbcConfig {
        @Bean
        @ConditionalOnMissingBean(DocumentDaoJdbc.class)
        DocumentDao documentDaoJdbc( @Qualifier(ServiceNames.NAMED_JDBC_TEMPLATE) NamedParameterJdbcTemplate template, ObjectProvider<I18n> i18nProvider) {
            I18n i18n = I18nUtils.resolve(i18nProvider);
            log.info(LogUtils.format(i18n, I18nKeys.AutoConfig.Feature.Service.DETAILS, FEATURE_NAME,
                                LogUtils.blue(DocumentDaoJdbc.class, true),
                                LogUtils.red(State.CREATED.toString())));
            return new DocumentDaoJdbc(template);
        }
    }

    
    @Configuration
    @ConditionalOnProperty(prefix = PropertyKeys.Features.PREFIX
            + ".document.web", name = "enabled", havingValue = "true", matchIfMissing = true)
    @Import({ DocumentController.class, DocumentExceptionHandler.class })
    static class DocumentWebConfig {

    }
}
