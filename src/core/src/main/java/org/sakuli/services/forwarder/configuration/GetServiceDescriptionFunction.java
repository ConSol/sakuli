package org.sakuli.services.forwarder.configuration;

import org.apache.commons.lang.StringUtils;
import org.jtwig.functions.FunctionRequest;
import org.jtwig.functions.SimpleJtwigFunction;
import org.sakuli.datamodel.TestSuite;

/**
 * Custom JtwigFunction for retrieving the service description for a provided test suite.
 * The service description can be configured within the testsuite.properties. If the description
 * within the properties file is not set, then the id of the test suite is used as description.
 *
 * @author Georgi Todorov
 */
public class GetServiceDescriptionFunction extends SimpleJtwigFunction {

    private String configuredServiceDescription;

    public GetServiceDescriptionFunction(String configuredServiceDescription) {
        this.configuredServiceDescription = configuredServiceDescription;
    }

    @Override
    public String name() {
        return "getServiceDescription";
    }

    @Override
    public Object execute(FunctionRequest request) {
        assert request.getNumberOfArguments() == 1;
        TestSuite testSuite = (TestSuite) request.getArguments().get(0);
        return StringUtils.isNotEmpty(configuredServiceDescription)
                ? configuredServiceDescription
                : testSuite.getId();
    }

}
