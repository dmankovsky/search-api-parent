package com.mysmartservices.api.search.relation;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class RelationContextTest {

    //****************************************************************************************************************//
    // add tests
    //****************************************************************************************************************//

    @Test
    void add_Test() {
        // Arrange
        RelationContext relationContext = new RelationContext();
        Relation relation = new AmpersandSymboleRelation();

        // Act
        String uuid = relationContext.add(relation);

        // Assert
        assertThat(uuid).isNotNull().isNotEmpty();
        assertThat(relationContext.getRelation(uuid)).isEqualTo(relation);
    }

    //****************************************************************************************************************//
    // remove tests
    //****************************************************************************************************************//

    @Test
    void remove_ExistingRelation_Test() {
        // Arrange
        RelationContext relationContext = new RelationContext();
        Relation relation = new AmpersandSymboleRelation();

        // Act
        String uuid = relationContext.add(relation);
        Relation resultRelation = relationContext.remove(uuid);

        // Assert
        assertThat(resultRelation).isNotNull().isEqualTo(relation);
        assertThat(relationContext.getRelation(uuid)).isNull();
    }

    @Test
    void remove_NoExistingRelation_Test() {
        // Arrange
        RelationContext relationContext = new RelationContext();

        // Act
        Relation resultRelation = relationContext.remove("notExisting");

        // Assert
        assertThat(resultRelation).isNull();
    }

    //****************************************************************************************************************//
    // isEmpty tests
    //****************************************************************************************************************//

    @Test
    void isEmpty_False_Test() {
        // Arrange
        RelationContext relationContext = new RelationContext();
        Relation relation = new AmpersandSymboleRelation();

        // Act
        relationContext.add(relation);
        boolean result = relationContext.isEmpty();

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void isEmpty_True_Test() {
        // Arrange
        RelationContext relationContext = new RelationContext();

        // Act
        boolean result = relationContext.isEmpty();

        // Assert
        assertThat(result).isTrue();
    }

    //****************************************************************************************************************//
    // getRelationPattern tests
    //****************************************************************************************************************//

    /* TODO: Not testable, implementations need to ensure they handle the possible NullPointer exception.
    @Test
    void getRelationPattern_NoContext_Test() {
        // Arrange
        RelationContext relationContext = new RelationContext();

        // Act
        Pattern pattern = relationContext.getRelationPattern();

        // Assert
        assertThat(pattern).isNull();
    }*/

    /*@Test
    void getRelationPattern_SingleContext_Test() {
        // Arrange
        RelationContext relationContext = new RelationContext();
        Relation andRelation = Mockito.spy(new AmpersandSymboleRelation());

        // Act
        String uuid = relationContext.add(andRelation);
        Pattern pattern = relationContext.getRelationPattern();
        String regex = pattern.pattern();

        // Assert
        assertThat(regex).isEqualTo("(?<" + uuid + ">(.+?)\\&)");
    }*/

    /*@Test
    void getRelationPattern_MultipleContext_Test() {
        // Arrange
        RelationContext relationContext = new RelationContext();
        Relation andRelation = Mockito.spy(new AmpersandSymboleRelation());
        Relation orRelation = Mockito.spy(new PipeSymboleRelation());

        // Act
        String andUuid = relationContext.add(andRelation);
        String orUuid = relationContext.add(orRelation);
        Pattern pattern = relationContext.getRelationPattern();
        String regex = pattern.pattern();

        // Assert
        assertThat(regex).contains("(?<" + orUuid + ">(.+?)\\|)", "(?<" + andUuid + ">(.+?)\\&)");
    }*/

    //****************************************************************************************************************//
    // getRelation tests
    //****************************************************************************************************************//

    @Test
    void getRelation_IsEmpty_Test() {
        // Arrange
        RelationContext relationContext = new RelationContext();

        // Act
        Relation relation = relationContext.getRelation("notExisting");

        // Assert
        assertThat(relation).isNull();
    }

    @Test
    void getRelation_Present_Test() {
        // Arrange
        RelationContext relationContext = new RelationContext();
        Relation andRelation = Mockito.spy(new AmpersandSymboleRelation());

        // Act
        String andUuid = relationContext.add(andRelation);
        Relation relation = relationContext.getRelation(andUuid);

        // Assert
        assertThat(relation).isEqualTo(andRelation);
    }

    //****************************************************************************************************************//
    // resolveRelations tests
    //****************************************************************************************************************//

    @Test
    void resolveRelations_CommonRelation_Test() {
        // Arrange
        Relation andRelation = new AmpersandSymboleRelation();
        Relation orRelation = new PipeSymboleRelation();
        RelationContext relationContext = new RelationContext(Arrays.asList(andRelation, orRelation));

        String testString = "fieldOne=test&fieldTwo>2|fieldThree:Like";

        // Act
        Pattern relationPattern = relationContext.getRelationPattern();
        Matcher matcher = relationPattern.matcher(testString);

        matcher.find();
        List<Relation> relationsAnd = relationContext.resolveRelations(matcher);
        matcher.find();
        List<Relation> relationsOr = relationContext.resolveRelations(matcher);

        // Assert
        Assertions.assertThat(relationsAnd).containsExactly(andRelation);
        Assertions.assertThat(relationsOr).containsExactly(orRelation);

    }

}
