package com.mysmartservices.api.search.parser;

import com.mysmartservices.api.search.relation.AmpersandSymboleRelation;
import com.mysmartservices.api.search.relation.PipeSymboleRelation;
import com.mysmartservices.api.search.relation.Relation;
import com.mysmartservices.api.search.relation.RelationContext;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RelationParserImplTest {

    //****************************************************************************************************************//
    // parse tests
    //****************************************************************************************************************//

    @Test
    void parse_Success_Test() throws RelationParseException, ExpressionParseException {
        // Arrange
        Relation andRelation = new AmpersandSymboleRelation();
        Relation orRelation = new PipeSymboleRelation();
        RelationContext context = new RelationContext(Arrays.asList(andRelation, orRelation));

        ExpressionParser expressionParser = Mockito.mock(ExpressionParserImpl.class);

        Specification<Object> spec1 = Mockito.mock(Specification.class);
        when(expressionParser.parse(eq("expressionOne"))).thenReturn(spec1);

        Specification<Object> spec2 = Mockito.mock(Specification.class);
        when(expressionParser.parse(eq("expressionTwo"))).thenReturn(spec2);

        Specification<Object> spec1and2 = Mockito.mock(Specification.class);
        when(spec1.and(eq(spec2))).thenReturn(spec1and2);

        Specification<Object> spec3 = Mockito.mock(Specification.class);
        when(expressionParser.parse(eq("expressionThree"))).thenReturn(spec3);

        Specification<Object> spec1and2or3 = Mockito.mock(Specification.class);
        when(spec1and2.or(eq(spec3))).thenReturn(spec1and2or3);

        RelationParserImpl parser = new RelationParserImpl(context, expressionParser);

        String testString = "expressionOne&expressionTwo|expressionThree";

        // Act
        Specification<Object> resultSpec = parser.parse(testString);

        // Assert
        assertThat(resultSpec).isEqualTo(spec1and2or3);

    }

    @Test
    void parse_InvertedSuccess_Test() throws RelationParseException, ExpressionParseException {
        // Arrange
        Relation andRelation = new AmpersandSymboleRelation();
        Relation orRelation = new PipeSymboleRelation();
        RelationContext context = new RelationContext(Arrays.asList(andRelation, orRelation));

        ExpressionParser expressionParser = Mockito.mock(ExpressionParserImpl.class);

        Specification<Object> spec1 = Mockito.mock(Specification.class);
        when(expressionParser.parse(eq("expressionOne"))).thenReturn(spec1);

        Specification<Object> spec2 = Mockito.mock(Specification.class);
        when(expressionParser.parse(eq("expressionTwo"))).thenReturn(spec2);

        Specification<Object> spec1and2 = Mockito.mock(Specification.class);
        when(spec1.or(eq(spec2))).thenReturn(spec1and2);

        Specification<Object> spec3 = Mockito.mock(Specification.class);
        when(expressionParser.parse(eq("expressionThree"))).thenReturn(spec3);

        Specification<Object> spec1and2or3 = Mockito.mock(Specification.class);
        when(spec1and2.and(eq(spec3))).thenReturn(spec1and2or3);

        RelationParserImpl parser = new RelationParserImpl(context, expressionParser);

        String testString = "expressionOne|expressionTwo&expressionThree";

        // Act
        Specification<Object> resultSpec = parser.parse(testString);

        // Assert
        assertThat(resultSpec).isEqualTo(spec1and2or3);

    }

    @Test
    void parse_Failed_Test() throws RelationParseException, ExpressionParseException {
        // Arrange
        Relation andRelation = new AmpersandSymboleRelation();
        Relation orRelation = new PipeSymboleRelation();
        RelationContext context = new RelationContext(Arrays.asList(andRelation, orRelation));

        ExpressionParser expressionParser = Mockito.mock(ExpressionParserImpl.class);
        when(expressionParser.parse(any())).thenThrow(new ExpressionParseException("Just a Test!"));


        RelationParserImpl parser = new RelationParserImpl(context, expressionParser);

        String testString = "expressionOne&expressionTwo|expressionThree";

        // Act
        RelationParseException exception = assertThrows(RelationParseException.class, () -> parser.parse(testString));

        // Assert
        assertThat(exception).hasMessage("Relational Parsing failed.");

    }

    //****************************************************************************************************************//
    // evaluateUniqueRelation tests
    //****************************************************************************************************************//

    @Test
    void evaluateUniqueRelation_UniqueResult_Test() throws RelationParseException {
        // Arrange
        RelationContext context = Mockito.mock(RelationContext.class);
        RelationParserImpl parser = new RelationParserImpl(context, null);

        Relation one = new AmpersandSymboleRelation();

        when(context.resolveRelations(any())).thenReturn(Arrays.asList(one));

        // Act
        Relation relation = parser.evaluateUniqueRelation(null);

        // Assert
        assertThat(relation).isEqualTo(one);

    }

    @Test
    void evaluateUniqueRelation_DoubleResult_Test() throws RelationParseException {
        // Arrange
        RelationContext context = Mockito.mock(RelationContext.class);
        RelationParserImpl parser = new RelationParserImpl(context, null);

        Relation one = new AmpersandSymboleRelation();
        Relation two = new AmpersandSymboleRelation();

        when(context.resolveRelations(any())).thenReturn(Arrays.asList(one, two));

        // Act
        RelationParseException exception = assertThrows(RelationParseException.class, () -> parser.evaluateUniqueRelation(null));

        // Assert
        assertThat(exception).hasMessage("The relation regex is not unique! The following relations apply:AmpersandSymboleRelation;AmpersandSymboleRelation;");

    }

    @Test
    void evaluateUniqueRelation_NoResult_Test() throws RelationParseException {
        // Arrange
        RelationContext context = Mockito.mock(RelationContext.class);
        RelationParserImpl parser = new RelationParserImpl(context, null);

        when(context.resolveRelations(any())).thenReturn(new ArrayList<>());

        // Act
        RelationParseException exception = assertThrows(RelationParseException.class, () -> parser.evaluateUniqueRelation(null));

        // Assert
        assertThat(exception).hasMessage("No relations could be matched!");

    }


}
