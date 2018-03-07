/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sakuli.services.forwarder;

import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import org.jtwig.environment.EnvironmentConfiguration;
import org.jtwig.environment.EnvironmentConfigurationBuilder;
import org.jtwig.spaceless.SpacelessExtension;
import org.jtwig.spaceless.configuration.SpacelessConfiguration;
import org.sakuli.datamodel.AbstractTestDataEntity;
import org.sakuli.datamodel.properties.SakuliProperties;
import org.sakuli.exceptions.SakuliExceptionHandler;
import org.sakuli.exceptions.SakuliForwarderException;
import org.sakuli.services.forwarder.configuration.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.sakuli.services.forwarder.configuration.TemplateModelEntityName.SAKULI_PROPERTIES;
import static org.sakuli.services.forwarder.configuration.TemplateModelEntityName.TEST_DATA_ENTITY;

/**
 * @author Georgi Todorov
 */
public abstract class AbstractTemplateOutputBuilder extends AbstractOutputBuilder {

    public static final String MAIN_TEMPLATE_NAME = "main.twig";
    private static final Logger logger = LoggerFactory.getLogger(AbstractTemplateOutputBuilder.class);

    @Autowired
    protected SakuliExceptionHandler exceptionHandler;
    @Autowired
    private SakuliProperties sakuliProperties;

    /**
     * Returns the name of the converter. The name is used to dynamically retrieve the template for the converter.
     *
     * @return the name of the converter
     */
    public abstract String getConverterName();

    /**
     * Returns a map of specific model objects based on the concrete template output builder.
     *
     * @return map with additional model objects
     */
    public abstract Map<TemplateModelEntityName, Object> getSpecificModelEntities();

    /**
     * Converts the current test data entity to a string based on the template for the concrete converter.
     *
     * @param abstractTestDataEntity Test data entity, which has to be converted
     * @return A string representation of the provided test data entity
     */
    public String createOutput(AbstractTestDataEntity abstractTestDataEntity) throws SakuliForwarderException {
        try {
            JtwigModel model = createModel(abstractTestDataEntity);
            EnvironmentConfiguration configuration = EnvironmentConfigurationBuilder.configuration()
                    .extensions()
                    .add(new SpacelessExtension(new SpacelessConfiguration(new LeadingWhitespaceRemover())))
                    .and()
                    .functions()
                    .add(new IsBlankFunction())
                    .add(new GetOutputStateFunction())
                    .add(new GetOutputDurationFunction())
                    .add(new ExtractScreenshotFunction(screenshotDivConverter))
                    .add(new AbbreviateFunction())
                    .add(new UnixTimestampConverterFunction())
                    .add(new GetTestDataEntityTypeFunction())
                    .and()
                    .build();
            JtwigTemplate template = JtwigTemplate.fileTemplate(getTemplatePath().toFile(), configuration);
            logger.debug(String.format("Render model into JTwig template. Model: '%s'", model));
            return template.render(model);
        } catch (Throwable thr) {
            throw new SakuliForwarderException(thr, "Exception during rendering of Twig template occurred!");
        }
    }

    /**
     * Creates the model used as input for the template engine. This can be overwritten by the concrete
     * OutputBuilder to add some specific objects to the model (e.g. specific properties)
     *
     * @return
     */
    protected JtwigModel createModel(AbstractTestDataEntity abstractTestDataEntity) {
        JtwigModel model = JtwigModel.newModel()
                .with(TEST_DATA_ENTITY.getName(), abstractTestDataEntity)
                .with(SAKULI_PROPERTIES.getName(), sakuliProperties);
        if (getSpecificModelEntities() != null) {
            getSpecificModelEntities().forEach((templateModelEntityName, object)->{
                model.with(templateModelEntityName.getName(), object);
            });
        }
        return model;
    }

    protected Path getTemplatePath() throws FileNotFoundException {
        String templateFolder = sakuliProperties.getForwarderTemplateFolder();
        Path templatePath = Paths.get(
                templateFolder + File.separator +
                        getConverterName().toLowerCase() + File.separator +
                        MAIN_TEMPLATE_NAME)
                .normalize().toAbsolutePath();
        if (Files.notExists(templatePath)) {
            throw new FileNotFoundException(String.format("JTwig template folder for %s could not be found under '%s'",
                    getConverterName(), templatePath.toString()));

        }
        logger.debug(String.format("Load JTwig template from following location: '%s'", templatePath));
        return templatePath;
    }

    @Override
    protected int getSummaryMaxLength() {
        // operation is not used for the template output builder
        return 0;
    }

    @Override
    protected String getOutputScreenshotDivWidth() {
        // operation is not used for the template output builder
        return null;
    }

}
