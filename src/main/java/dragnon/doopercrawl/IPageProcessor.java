package dragnon.doopercrawl;

import java.util.function.BiFunction;
import java.util.stream.Stream;

interface IPageProcessor extends BiFunction<String, String, Stream<String>> {}
