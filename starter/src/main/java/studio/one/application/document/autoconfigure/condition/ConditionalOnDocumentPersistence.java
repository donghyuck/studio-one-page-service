package studio.one.application.document.autoconfigure.condition;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

import studio.one.platform.autoconfigure.PersistenceProperties;
import studio.one.platform.autoconfigure.features.condition.ConditionalOnFeaturePersistence;

/**
 *
 * @author  donghyuck, son
 * @since 2026-01-06
 * @version 1.0
 *
 * <pre> 
 * 개정이력(Modification Information)
 *   수정일        수정자           수정내용
 *  ---------    --------    ---------------------------
 * 2026-01-06  donghyuck, son: 최초 생성.
 * </pre>
 */

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented 
@ConditionalOnFeaturePersistence(feature = "document")
public @interface ConditionalOnDocumentPersistence {
    @AliasFor(annotation = ConditionalOnFeaturePersistence.class, attribute = "value")
    PersistenceProperties.Type value(); 
}
