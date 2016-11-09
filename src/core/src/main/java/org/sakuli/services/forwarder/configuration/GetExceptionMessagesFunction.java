package org.sakuli.services.forwarder.configuration;

import org.jtwig.functions.FunctionRequest;
import org.jtwig.functions.SimpleJtwigFunction;
import org.sakuli.datamodel.AbstractTestDataEntity;

import java.util.Map;

/**
 * Custom JtwigFunction for retrieving the excepton text for a provided test data entity.
 *
 * @author Georgi Todorov
 */
public class GetExceptionMessagesFunction extends SimpleJtwigFunction {

    private Map<String, String> formatExpressions;

    public GetExceptionMessagesFunction(Map<String, String> formatExpressions) {
        this.formatExpressions = formatExpressions;
    }

    @Override
    public String name() {
        return "getExceptionMessages";
    }

    @Override
    public Object execute(FunctionRequest request) {
        assert request.getNumberOfArguments() == 2;
        AbstractTestDataEntity testDataEntity = (AbstractTestDataEntity) request.getArguments().get(0);
        boolean flatFormatted = (boolean) request.getArguments().get(1);
        return testDataEntity.getExceptionMessages(flatFormatted, formatExpressions);
    }

}
