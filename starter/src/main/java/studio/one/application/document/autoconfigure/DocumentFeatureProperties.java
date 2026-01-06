package studio.one.application.document.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;
import studio.one.platform.autoconfigure.FeaturesProperties.FeatureToggle;
import studio.one.platform.autoconfigure.PersistenceProperties;
import studio.one.platform.autoconfigure.SimpleWebProperties;
import studio.one.platform.constant.PropertyKeys;

@ConfigurationProperties(prefix = PropertyKeys.Features.PREFIX + ".document")
@Getter
@Setter
public class DocumentFeatureProperties extends FeatureToggle {

    private SimpleWebProperties web = new SimpleWebProperties();

    public PersistenceProperties.Type resolvePersistence(PersistenceProperties.Type globalDefault) {
        return super.resolvePersistence(globalDefault);
    }
}
