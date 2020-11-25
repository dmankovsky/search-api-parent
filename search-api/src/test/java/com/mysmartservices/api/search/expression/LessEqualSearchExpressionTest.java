package com.mysmartservices.api.search.expression;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class LessEqualSearchExpressionTest {

    private SearchExpression searchExpression = new LessEqualSearchExpression();

    @Test
    void getRepresentation_Test() {

        // Arrange
        String expected = "<=";

        // Act
        String result = searchExpression.getRepresentation();

        // Assert
        assertThat(result).isEqualTo(expected);

    }

    @Test
    void getSearchPattern_Test() {

        // Arrange
        String textualExpression = "myField<=1";
        Pattern pattern = Pattern.compile("(\\w+?)<=(\\w+?)", Pattern.UNICODE_CHARACTER_CLASS);

        // Act
        Pattern searchPattern = searchExpression.getSearchPattern();
        Matcher matcher = searchPattern.matcher(textualExpression);
        boolean matching = matcher.find();

        // Assert

        assertThat(matching).isTrue();
        assertThat(matcher.group(1)).isEqualTo("myField");
        assertThat(matcher.group(2)).isEqualTo("1");

    }

    @Test
    void getSearchPattern_Border_Test() {

        // Arrange
        String textualExpression = "myField<1";

        // Act
        Pattern searchPattern = searchExpression.getSearchPattern();
        Matcher matcher = searchPattern.matcher(textualExpression);
        boolean matching = matcher.find();

        // Assert
        assertThat(matching).isFalse();

    }

    @Test
    void getSpecification_Success_Test() {

        // Arrange
        String expressionString = "myField<=1";

        Root<Object> root = Mockito.mock(Root.class);
        Path<String> myFieldPath = Mockito.mock(Path.class);
        when(root.<String>get(eq("myField"))).thenReturn(myFieldPath);

        CriteriaBuilder builder = Mockito.mock(CriteriaBuilder.class);
        Predicate predicate = Mockito.mock(Predicate.class);
        when(builder.lessThanOrEqualTo(eq(myFieldPath), eq("1"))).thenReturn(predicate);

        // Act
        Optional<Specification<Object>> specification = searchExpression.getSpecification(expressionString);
        assertThat(specification).isPresent();

        Specification<Object> objectSpecification = specification.get();
        Predicate resultPredicate = objectSpecification.toPredicate(root, null, builder);

        // Assert
        assertThat(resultPredicate).isEqualTo(predicate);

    }

    @Test
    void getSpecification_NoMatch_Test() {

        // Arrange
        String expressionString = "notTheExpression";

        // Act
        Optional<Specification<Object>> specification = searchExpression.getSpecification(expressionString);

        // Assert
        assertThat(specification).isEmpty();

    }

}
