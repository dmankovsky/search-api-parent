package com.mysmartservices.api.search.expression;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BiggerThanSearchExpression implements SearchExpression{

    @Override
    public String getRepresentation() {
        return ">";
    }

    @Override
    public Pattern getSearchPattern() {
        return Pattern.compile("(\\w+?)>(\\w+.?)", Pattern.UNICODE_CHARACTER_CLASS);
    }

    @Override
    public <T> Optional<Specification<T>> getSpecification(final String expressionString) {
        Matcher matcher = getSearchPattern().matcher(expressionString);
        if (matcher.find()) {
            String fieldName = matcher.group(1);
            String targetValue = matcher.group(2);

            return Optional.of((root, query, builder) -> {
                Path<String> fieldPath = root.<String>get(fieldName);
                return builder.greaterThan(fieldPath, targetValue);
            });
        }
        return Optional.empty();
    }

}
