package cos.json;

import io.vertx.core.json.JsonObject;
import kotlinx.serialization.json.JsonConfiguration;
import kotlinx.serialization.modules.EmptyModule;
import org.intellij.lang.annotations.Language;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class LibsBenchmark {

    @Language("JSON")
    private static final String                          raw      = """
            
               {
                 "created_at": "Thu Apr 06 15:24:15 +0000 2017",
                 "id_str": "850006245121695744",
                 "text": "1\\/ Today we\\u2019re sharing our vision for the future of the Twitter API platform!\\nhttps:\\/\\/t.co\\/XweGngmxlP",
                 "user": {
                   "id": 2244994945,
                   "name": "Twitter Dev",
                   "screen_name": "TwitterDev",
                   "location": "Internet",
                   "url": "https:\\/\\/dev.twitter.com\\/",
                   "description": "Your official source for Twitter Platform news, updates & events. Need technical help? Visit https:\\/\\/twittercommunity.com\\/ \\u2328\\ufe0f #TapIntoTwitter"
                 },
                 "place": {  
                 },
                 "entities": {
                   "hashtags": [     
                   ],
                   "urls": [
                     {
                       "url": "https:\\/\\/t.co\\/XweGngmxlP",
                       "unwound": {
                         "url": "https:\\/\\/cards.twitter.com\\/cards\\/18ce53wgo4h\\/3xo1c",
                         "title": "Building the Future of the Twitter API Platform"
                       }
                     }
                   ],
                   "user_mentions": [    
                   ]
                 }
               }
            """;
    private static final kotlinx.serialization.json.Json kxParser = new kotlinx.serialization.json.Json(JsonConfiguration.Companion.getDefault(), EmptyModule.INSTANCE);


    //    @Benchmark
//    public Object json1Simple() {
//        return Json.parse("{}");
//    }
//
    @Benchmark
    public Object json1() {
        return cos.oldjson.Json.parse(raw);
    }

//    @Benchmark
//    public Object json1Big() {
//        return Json.parse(rawBig);
//    }


    //    @Benchmark
//    public Object json2Simple() {
//        return Json2.parse("{}");
//    }
//
//    @Benchmark
//    public Object stringBase() {
//        int x = 0;
//        for (int i = 0; i < raw.length(); i++) {
//            x += raw.charAt(i);
//        }
//        return x;
//    }
//
    @Benchmark
    public Object json2() {
        return Json.parse(raw);
    }

//    @Benchmark
//    public Object json3() {
//        return Json3.parse(raw);
//    }

//    @Benchmark
//    public Object json2Big() {
//        return Json2.parse(rawBig);
//    }


    //    @Benchmark
//    public Object kotlinxSimple() {
//        return kxParser.parseJson("{}");
//    }
//
//    @Benchmark
//    public Object kotlinx() {
//        return kxParser.parseJson(raw);
//    }
//
//    @Benchmark
//    public Object kotlinxBig() {
//        return kxParser.parseJson(rawBig);
//    }
//
//
//
//    @Benchmark
//    public Object jacksonSimple() {
//        return new JsonObject("{}");
//    }
//
    @Benchmark
    public Object jackson() {
        return new JsonObject(raw);
    }

//    @Benchmark
//    public Object jacksonBig() {
//        return new JsonObject(rawBig);
//    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(LibsBenchmark.class.getSimpleName())
                .warmupIterations(1)
                .measurementIterations(1)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

}
