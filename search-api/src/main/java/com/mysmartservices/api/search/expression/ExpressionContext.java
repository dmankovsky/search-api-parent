package com.mysmartservices.api.search.expression;

import com.mysmartservices.api.search.parser.RelationParserImpl;
import com.mysmartservices.api.search.relation.Relation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.criteria.Expression;
import java.util.*;
import java.util.stream.Stream;

public class ExpressionContext {

    private final static Logger LOG = LoggerFactory.getLogger(RelationParserImpl.class);

    private final List<SearchExpression> expressions;

    public ExpressionContext() {
        expressions = new ArrayList<>();
    }

    public ExpressionContext(List<SearchExpression> expressions) {
        this.expressions = expressions;
    }

    public ExpressionContext add(SearchExpression expression) {
        expressions.add(expression);
        return this;
    }

    public ExpressionContext remove(SearchExpression expression) {
        expressions.add(expression);
        return this;
    }

    public ExpressionContext clear(){
        expressions.clear();
        return this;
    }

    public Stream<SearchExpression> stream(){
        return expressions.stream();
    }

    public boolean isEmpty(){
        return Objects.isNull(expressions) || expressions.isEmpty();
    }

}
