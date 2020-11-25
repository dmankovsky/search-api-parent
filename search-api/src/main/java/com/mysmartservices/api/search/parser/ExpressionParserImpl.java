package com.mysmartservices.api.search.parser;

import com.mysmartservices.api.search.expression.ExpressionContext;
import com.mysmartservices.api.search.expression.SearchExpression;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public class ExpressionParserImpl implements ExpressionParser {

    private final ExpressionContext expressionContext;

    public ExpressionParserImpl(ExpressionContext expressionContext) {
        this.expressionContext = expressionContext;
    }

    @Override
    public <T> Specification<T> parse(String expressionString) throws ExpressionParseException {

        if(isNull(expressionString) || expressionString.isEmpty()){
            throw new ExpressionParseException(String.format("The input \"%s\" is invalid.", expressionString));
        }
        if(isNull(expressionContext) || expressionContext.isEmpty()){
            throw new ExpressionParseException("No expressions present. Please ensure at least one element is published in the bean List<SearchExpression> expressions.");
        }

        List<Specification<T>> matchingSpecs = expressionContext.stream()
                .filter(Objects::nonNull)
                .map(expression -> expression.<T>getSpecification(expressionString))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        if (matchingSpecs.size() == 1) {
            return matchingSpecs.get(0);
        } else if (matchingSpecs.isEmpty()) {
            throw new ExpressionParseException(String.format("The expression \"%s\" couldn't be resolved, " +
                    "because no SearchExpression was specified. Please ensure that expressions you want to use" +
                    " are registered.", expressionString));
        } else {
            throw new ExpressionParseException(String.format("The expression \"%s\" couldn't be resolved, " +
                    "because more than one configured SearchExpressions matched. Please ensure that SearchExpression " +
                    "have a unique parse phrase.", expressionString));
        }
    }


}
