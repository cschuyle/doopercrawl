package dragnon.doopercrawl;

import java.util.function.BiFunction;
import java.util.stream.Stream;

interface ILinkExtractor extends BiFunction<String, String, Stream<String>> {}
