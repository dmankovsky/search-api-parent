package com.mysmartservices.api.search.relation;

import com.mysmartservices.api.search.parser.RelationParseException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class AmpersandSymboleRelationTest {

    private Relation relation = new AmpersandSymboleRelation();

    //****************************************************************************************************************//
    // parse tests
    //****************************************************************************************************************//

    @Test
    void getRegex_Parser_Test() {
        // Arrange
        String expectedRegex = "(.+?)\\&";

        // Act
        String resultString = relation.getRegex();

        // Assert
        assertThat(resultString).isEqualTo(expectedRegex);

    }

    //****************************************************************************************************************//
    // parseRelation tests
    //****************************************************************************************************************//

    @Test
    void parseRelation_Success_Test() throws RelationParseException {
        // Arrange
        String testString = "expressionOne&expressionTwo";

        // Act
        Optional<String> resultString = relation.parseRelation(testString);

        // Assert
        assertThat(resultString).isPresent();
        assertThat(resultString.get()).isEqualTo("expressionOne");
    }

    @Test
    void parseRelation_Failed_Test() throws RelationParseException {
        // Arrange
        String testString = "expressionOne|expressionTwo";

        // Act
        Optional<String> resultString = relation.parseRelation(testString);

        // Assert
        assertThat(resultString).isNotPresent();
    }

    //****************************************************************************************************************//
    // concat tests
    //****************************************************************************************************************//

    @Test
    void concat_Success_Test() throws RelationParseException {
        // Arrange
        Specification<Object> rootSpec = Mockito.mock(Specification.class);
        Specification<Object> childSpec = Mockito.mock(Specification.class);
        Specification<Object> finalSpec = Mockito.mock(Specification.class);

        when(rootSpec.and(eq(childSpec))).thenReturn(finalSpec);

        // Act
        Specification<Object> resultSpec = relation.concat(rootSpec, childSpec);

        // Assert
        assertThat(resultSpec).isEqualTo(finalSpec);
    }
}
