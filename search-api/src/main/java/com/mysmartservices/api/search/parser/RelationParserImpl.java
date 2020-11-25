package com.mysmartservices.api.search.parser;

import com.mysmartservices.api.search.relation.Relation;
import com.mysmartservices.api.search.relation.RelationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;
import java.util.regex.Matcher;

import static java.util.Objects.isNull;

public class RelationParserImpl implements RelationParser {

    private final Logger LOG = LoggerFactory.getLogger(RelationParserImpl.class);

    private final RelationContext relationContext;
    private final ExpressionParser expressionParser;

    public RelationParserImpl(RelationContext relationContext, ExpressionParser expressionParser) {
        this.relationContext = relationContext;
        this.expressionParser = expressionParser;
    }


    public <T> Specification<T> parse(String searchString) throws RelationParseException {
        try {
            Optional<Specification<T>> rootSpec = Optional.empty();
            Optional<Relation> lastRelation = Optional.empty();
            int parsedPosition = 0;

            if (!relationContext.isEmpty()) {
                Matcher matcher = relationContext.getRelationPattern()
                        .matcher(searchString);

                while (matcher.find()) {
                    String relationString = matcher.group(0);
                    parsedPosition = matcher.end();

                    Relation targetRelation = evaluateUniqueRelation(matcher);

                    String expressionString = targetRelation.parseRelation(relationString)
                            .orElseThrow(() -> new RelationParseException(targetRelation.getClass().getCanonicalName() + " failed to resolve expression from " + relationString));
                    Specification<T> childSpec = expressionParser.parse(expressionString);

                    rootSpec = Optional.ofNullable(concatSpecs(rootSpec, lastRelation, childSpec));
                    lastRelation = Optional.ofNullable(targetRelation);
                }
            }

            Specification<T> childSpec = expressionParser.parse(searchString.substring(parsedPosition));
            return concatSpecs(rootSpec, lastRelation, childSpec);
        } catch (Exception e) {
            throw new RelationParseException("Relational Parsing failed.", e);
        }
    }

    private <T> Specification<T> concatSpecs(Optional<Specification<T>> rootSpec, Optional<Relation> lastRelation, Specification<T> childSpec) throws RelationParseException {
        return lastRelation.isPresent() && rootSpec.isPresent() ?
                lastRelation.get().concat(rootSpec.get(), childSpec)
                : childSpec;
    }

    public Relation evaluateUniqueRelation(Matcher matcher) throws RelationParseException {
        List<Relation> relations = relationContext.resolveRelations(matcher);
        String errorMessage;

        if (isNull(relations) || relations.isEmpty()) {
            errorMessage = "No relations could be matched!";
        } else if (relations.size() == 1) {
            return relations.get(0);
        } else {
            StringBuilder errorMessageBuilder = new StringBuilder("The relation regex is not unique! The following relations apply:");
            relations.forEach(relation -> errorMessageBuilder.append(relation.getClass().getSimpleName() + ";"));
            errorMessage = errorMessageBuilder.toString();
        }

        LOG.error(errorMessage);
        throw new RelationParseException(errorMessage);
    }

}
