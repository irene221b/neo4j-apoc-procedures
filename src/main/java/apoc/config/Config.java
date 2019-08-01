package apoc.config;

import apoc.ApocConfig;
import apoc.ApocConfiguration;
import apoc.Description;
import apoc.result.MapResult;
import apoc.util.Util;
import org.apache.commons.configuration2.Configuration;
import org.neo4j.common.DependencyResolver;
import org.neo4j.internal.helpers.collection.Iterators;
import org.neo4j.internal.kernel.api.security.SecurityContext;
import org.neo4j.procedure.Context;
import org.neo4j.procedure.Procedure;

import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author mh
 * @since 28.10.16
 */
public class Config {

    public static class ConfigResult {
        public final String key;
        public final Object value;

        public ConfigResult(String key, Object value) {
            this.key = key;
            this.value = value;
        }
    }

    @Context
    public SecurityContext securityContext;

    @Context
    public DependencyResolver dependencyResolver;

    @Description("apoc.config.list | Lists the Neo4j configuration as key,value table")
    @Procedure
    public Stream<ConfigResult> list() {
        Util.checkAdmin(securityContext, "apoc.config.list");
        Configuration config = dependencyResolver.resolveDependency(ApocConfig.class).getConfig();
        return Iterators.stream(config.getKeys()).map(s -> new ConfigResult(s, config.getString(s)));
    }

    @Description("apoc.config.map | Lists the Neo4j configuration as map")
    @Procedure
    public Stream<MapResult> map() {
        Util.checkAdmin(securityContext, "apoc.config.map");
        Configuration config = dependencyResolver.resolveDependency(ApocConfig.class).getConfig();
        Map<String, Object> configMap = Iterators.stream(config.getKeys())
                .collect(Collectors.toMap(s -> s, s -> config.getString(s)));
        return Stream.of(new MapResult(configMap));
    }
}
