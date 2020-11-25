package com.mysmartservices.api.search.expression;


import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;
import java.util.regex.Pattern;

public interface SearchExpression {

    String getRepresentation();
    Pattern getSearchPattern();
    <T> Optional<Specification<T>> getSpecification(final String expressionString);

}
