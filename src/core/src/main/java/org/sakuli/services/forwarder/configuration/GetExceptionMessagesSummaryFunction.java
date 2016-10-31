package org.sakuli.services.forwarder.configuration;

import org.apache.commons.lang.StringUtils;
import org.jtwig.functions.FunctionRequest;
import org.jtwig.functions.SimpleJtwigFunction;
import org.sakuli.datamodel.AbstractTestDataEntity;

import java.util.Map;

/**
 * Custom JtwigFunction for retrieving the excepton text for a provided test data entity.
 * The text will be truncated to 200 characters.
 *
 * @author Georgi Todorov
 */
public class GetExceptionMessagesSummaryFunction extends SimpleJtwigFunction {

    private Map<String, String> formatExpressions;
    private int summaryMaxLength = 200;

    public GetExceptionMessagesSummaryFunction(Map<String, String> formatExpressions) {
        this.formatExpressions = formatExpressions;
    }

    @Override
    public String name() {
        return "getExceptionMessagesSummary";
    }

    @Override
    public Object execute(FunctionRequest request) {
        assert request.getNumberOfArguments() == 2;
        AbstractTestDataEntity testDataEntity = (AbstractTestDataEntity) request.getArguments().get(0);
        boolean flatFormatted = (boolean) request.getArguments().get(1);
        return cutTo(testDataEntity.getExceptionMessages(flatFormatted, formatExpressions), summaryMaxLength);
    }

    protected static String cutTo(String string, int summaryMaxLength) {
        if (string != null && string.length() > summaryMaxLength) {
            return StringUtils.substring(string, 0, summaryMaxLength) + " ...";
        }
        return string;
    }

}
