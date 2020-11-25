package com.mysmartservices.api.search.relation;

import com.mysmartservices.api.search.parser.RelationParseException;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

public interface Relation {

    String getRegexSymbole();

    /**
     * This method will be used by the {@link RelationContext} to build the central regex.
     * @return Regex to parse the relation expression.
     */
    String getRegex();

    /**
     * This method should parse the relationString and return an expression String.
     * @param relationString a String matching the regex from {@link #getRegex()}
     * @return the root {@link com.mysmartservices.api.search.expression.SearchExpression} to pass to the {@link com.mysmartservices.api.search.parser.ExpressionParser}
     */
    Optional<String> parseRelation(String relationString) throws RelationParseException;

    /**
     * This method should be used to implement the relation into a {@link Specification}.
     *
     * @param rootSpec the root {@link Specification} of this relation, representing all previous {@link Specification}.
     * @param childSpec child {@link Specification} this relation applies to
     * @param <T> {@link javax.persistence.Entity} annotated class, used by an {@link org.springframework.data.jpa.repository.JpaRepository}
     * @return the new {@link Specification} resulting from this relation.
     * @throws RelationParseException
     */
    <T> Specification<T> concat(Specification<T> rootSpec, Specification<T> childSpec) throws RelationParseException;

}
