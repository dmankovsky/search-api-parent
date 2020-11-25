package com.mysmartservices.api.search.parser;

import com.mysmartservices.api.search.expression.BiggerEqualsSearchExpression;
import com.mysmartservices.api.search.expression.BiggerThanSearchExpression;
import com.mysmartservices.api.search.expression.EqualSearchExpression;
import com.mysmartservices.api.search.expression.ExpressionContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

class ExpressionParserImplTest {

    private BiggerEqualsSearchExpression biggerEqualsSearchExpression = Mockito.spy(new BiggerEqualsSearchExpression());
    private BiggerThanSearchExpression biggerThanSearchExpression = Mockito.spy(BiggerThanSearchExpression.class);
    private EqualSearchExpression equalSearchExpression = Mockito.spy(new EqualSearchExpression());

    @Test
    public void parse_UniqueResult_Success_Test() throws ExpressionParseException {

        // Arrange
        ExpressionContext expressionContext = new ExpressionContext()
                .add(biggerEqualsSearchExpression)
                .add(biggerThanSearchExpression)
                .add(equalSearchExpression)
                .add(equalSearchExpression);
        ExpressionParser expressionParser = new ExpressionParserImpl(expressionContext);
        String expressionString = "myField>1";

        // Act
        Specification<Object> parse = expressionParser.parse(expressionString);

        // Assert
        Mockito.verify(biggerThanSearchExpression, times(1)).getSpecification(eq(expressionString));
        Assertions.assertThat(parse).isNotNull();

    }

    @Test
    public void parse_NullExpressionClassesIgnored_Success_Test() throws ExpressionParseException {

        // Arrange
        ExpressionContext expressionContext = new ExpressionContext()
                .add(biggerEqualsSearchExpression)
                .add(biggerThanSearchExpression)
                .add(null)
                .add(equalSearchExpression);
        ExpressionParser expressionParser = new ExpressionParserImpl(expressionContext);
        String expressionString = "myField>1";

        // Act
        Specification<Object> parse = expressionParser.parse(expressionString);

        // Assert
        Mockito.verify(biggerThanSearchExpression, times(1)).getSpecification(eq(expressionString));
        Assertions.assertThat(parse).isNotNull();

    }

    @Test
    public void parse_RedundantResult_Error_Test() throws ExpressionParseException {

        // Arrange
        ExpressionContext expressionContext = new ExpressionContext()
                .add(biggerEqualsSearchExpression)
                .add(biggerThanSearchExpression)
                .add(equalSearchExpression)
                .add(equalSearchExpression);
        ExpressionParser expressionParser = new ExpressionParserImpl(expressionContext);
        String expressionString = "myField=1";

        // Act
        ExpressionParseException expressionParseException = assertThrows(ExpressionParseException.class, () -> expressionParser.parse(expressionString));

        // Assert
        Assertions.assertThat(expressionParseException).hasMessage("The expression \"" + expressionString + "\" couldn't be resolved, because more than one configured SearchExpressions matched. Please ensure that SearchExpression have a unique parse phrase.");

    }

    @Test
    public void parse_NoResult_Error_Test() throws ExpressionParseException {

        // Arrange
        ExpressionContext expressionContext = new ExpressionContext()
                .add(biggerEqualsSearchExpression)
                .add(biggerThanSearchExpression)
                .add(equalSearchExpression)
                .add(equalSearchExpression);
        ExpressionParser expressionParser = new ExpressionParserImpl(expressionContext);
        String expressionString = "noExpressionFits";

        // Act
        ExpressionParseException expressionParseException = assertThrows(ExpressionParseException.class, () -> expressionParser.parse(expressionString));

        // Assert
        Assertions.assertThat(expressionParseException).hasMessage("The expression \"" + expressionString + "\" couldn't be resolved, because no SearchExpression was specified. Please ensure that expressions you want to use are registered.");

    }

    @Test
    public void parse_NoExpressionsConfigured_Error_Test() throws ExpressionParseException {

        // Arrange
        ExpressionParser expressionParser = new ExpressionParserImpl(new ExpressionContext());
        String expressionString = "noExpressionFits";

        // Act
        ExpressionParseException expressionParseException = assertThrows(ExpressionParseException.class, () -> expressionParser.parse(expressionString));

        // Assert
        Assertions.assertThat(expressionParseException).hasMessage("No expressions present. Please ensure at least one element is published in the bean List<SearchExpression> expressions.");

    }

    @Test
    public void parse_EmptyInput_Error_Test() throws ExpressionParseException {

        // Arrange
        ExpressionContext expressionContext = new ExpressionContext()
                .add(biggerEqualsSearchExpression)
                .add(biggerThanSearchExpression)
                .add(equalSearchExpression)
                .add(equalSearchExpression);
        ExpressionParser expressionParser = new ExpressionParserImpl(expressionContext);
        String expressionString = "";

        // Act
        ExpressionParseException expressionParseException = assertThrows(ExpressionParseException.class, () -> expressionParser.parse(expressionString));

        // Assert
        Assertions.assertThat(expressionParseException).hasMessage("The input \"" + expressionString + "\" is invalid.");

    }

    @Test
    public void parse_NullInput_Error_Test() throws ExpressionParseException {

        // Arrange
        ExpressionContext expressionContext = new ExpressionContext()
                .add(biggerEqualsSearchExpression)
                .add(biggerThanSearchExpression)
                .add(equalSearchExpression)
                .add(equalSearchExpression);
        ExpressionParser expressionParser = new ExpressionParserImpl(expressionContext);
        String expressionString = null;

        // Act
        ExpressionParseException expressionParseException = assertThrows(ExpressionParseException.class, () -> expressionParser.parse(expressionString));

        // Assert
        Assertions.assertThat(expressionParseException).hasMessage("The input \"null\" is invalid.");

    }

}
