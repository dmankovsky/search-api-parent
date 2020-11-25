package com.mysmartservices.api.search.relation;

import com.mysmartservices.api.search.parser.RelationParseException;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PipeSymboleRelation implements Relation{

    @Override
    public String getRegexSymbole() {
        return "\\|";
    }

    @Override
    public String getRegex() {
        return "(.+?)\\|";
    }

    @Override
    public Optional<String> parseRelation(String relationString) {
        Pattern pattern = Pattern.compile(getRegex(), Pattern.UNICODE_CHARACTER_CLASS);
        Matcher matcher = pattern.matcher(relationString);
        return (matcher.find())? Optional.ofNullable(matcher.group(1)) : Optional.empty();
    }

    @Override
    public <T> Specification<T> concat(Specification<T> rootSpec, Specification<T> childSpec) {
        return rootSpec.or(childSpec);
    }


}
