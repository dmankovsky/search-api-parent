package com.mysmartservices.api.search.relation;

import com.mysmartservices.api.search.parser.RelationParserImpl;
import com.mysmartservices.api.search.parser.RelationParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

/**
 * Context to keep Relations extends by resolving methods to flatten parsing mechanism.
 * This Class is used by the default implementation {@link RelationParserImpl} of {@link RelationParser}.
 *
 * @author Tobias Gläßer
 * @version 1.0
 */
public class RelationContext {

    private final static Logger LOG = LoggerFactory.getLogger(RelationParserImpl.class);

    private final Map<String, Relation> relations = new HashMap<>();

    /**
     * Constructor to create an empty context.
     */
    public RelationContext() {
        LOG.debug("Create empty context.");
    }

    /**
     * Constructor to create a context for a list of {@link Relation}
     *
     * @param configuredRelations relations to be configured for the ParserContext
     */
    public RelationContext(List<Relation> configuredRelations) {
        LOG.debug("Create context with default Relations.");
        configuredRelations.stream()
                .filter(Objects::nonNull)
                .forEach(this::add);
    }

    /**
     * Will serialize an {@link Relation} and add it to this context.
     * Every uuid will start with ID to ensure that it begins with a letter.
     *
     * @param relation new relation
     * @return uuid of the relation inside the relations map
     */
    public String add(Relation relation) {
        String uuid;
        do {
            uuid = "ID" + UUID.randomUUID().toString().replace("-", "");
        } while (relations.containsKey(uuid));

        relations.put(uuid, relation);

        LOG.debug("Added {} with UUID {} to context.", relation.getClass().getCanonicalName(), uuid);
        return uuid;
    }

    /**
     * Will remove a configured relation from this context.
     *
     * @param uuid of an configured {@link Relation}
     * @return the removed {@link Relation}. Returns null if uuid doesn't point to an {@link Relation} inside this context.
     */
    public Relation remove(String uuid) {
        LOG.debug("Remove Relation with UUID {} from context.", uuid);
        return relations.remove(uuid);
    }

    /**
     * Indicates if an {@link Relation} was configured inside this context.
     *
     * @return false if this context is configured. Returns true in any other case.
     */
    public boolean isEmpty() {
        return relations.isEmpty();
    }

    /**
     * Creates a pattern for all configured {@link Relation}.
     * The method will use {@link Relation#getRegex()} to receive the regex to parse a certain relation.
     * Afterwards it will group the received relation and name them with the uuid of the relation inside this context.
     * This will result in the following regex: '(?<uuid-1>regex-1)|...|(?<uuid-n>regex-n)'.
     *
     * @return {@link Pattern} of the generated regex
     */
    public Pattern getRelationPattern() {
        StringBuilder regexBuilder = new StringBuilder("(.+?)(");
        relations.entrySet().stream()
                .map(entry -> String.format("(?<%s>%s)|", entry.getKey(), entry.getValue().getRegexSymbole()))
                .forEach(regexBuilder::append);
        regexBuilder.deleteCharAt(regexBuilder.length() - 1);
        regexBuilder.append(")");

        String generalRegex = regexBuilder.toString();
        LOG.debug("Create Pattern for regex {}.", generalRegex);
        return Pattern.compile(generalRegex, Pattern.UNICODE_CHARACTER_CLASS);
    }

    /**
     * Getter for a Relation inside this context.
     *
     * @param uuid uuid of an relation inside this context
     * @return the {@link Relation} inside this context. Returns null if uuid doesn't point to an {@link Relation} inside this context.
     */
    public Relation getRelation(String uuid) {
        return relations.get(uuid);
    }

    /**
     * Recommended helper method the handle {@link Matcher} received by applying the {@link Pattern} from {@link #getRelationPattern()}
     *
     * @param matcher after {@link Matcher#find()} was applied
     * @return a {@link List} of named uuids found in this matcher. Returns an empty {@link List} if no matching uuid was found or {@link Matcher#groupCount()} is smaller than 1.
     */
    public List<Relation> resolveRelations(Matcher matcher) {
        return relations.entrySet().stream()
                .filter(entry -> nonNull(matcher.group(entry.getKey())))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

}
