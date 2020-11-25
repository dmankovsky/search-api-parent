package com.mysmartservices.api.search.parser;

import org.springframework.data.jpa.domain.Specification;

public interface ExpressionParser {

    <T> Specification<T> parse(String expressionString) throws ExpressionParseException;

}
