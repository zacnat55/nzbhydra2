package org.nzbhydra;

import org.nzbhydra.api.CategoryConverter;
import org.nzbhydra.database.IndexerRepository;
import org.nzbhydra.database.SearchResultEntity;
import org.nzbhydra.database.SearchResultRepository;
import org.nzbhydra.mapping.RssRoot;
import org.nzbhydra.searching.Category;
import org.nzbhydra.searching.CategoryProvider;
import org.nzbhydra.searching.SearchModuleConfigProvider;
import org.nzbhydra.searching.infos.InfoProvider;
import org.nzbhydra.searching.infos.InfoProviderException;
import org.nzbhydra.searching.infos.TmdbHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.WebSocketAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//@SpringBootApplication
@Configuration
@EnableAutoConfiguration(exclude = {WebSocketAutoConfiguration.class, AopAutoConfiguration.class, org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration.class})
@ComponentScan
@RestController
@EnableCaching
public class NzbHydra {

    private static final Logger log = LoggerFactory.getLogger(NzbHydra.class);

    List<SearchResultEntity> results = new ArrayList<>();


    @Autowired
    private SearchResultRepository searchResultRepository;

    @Autowired
    private IndexerRepository indexerRepository;

    @Autowired
    private SearchModuleConfigProvider searchModuleConfigProvider;

    @Autowired
    CategoryProvider categoryProvider;

    @Autowired
    InfoProvider infoProvider;

    @Autowired
    private TmdbHandler tmdbHandler;

    @Autowired
    CategoryConverter categoryConverter; //Needed to be autowired so the provider in it is initialized


    public static void main(String[] args) {

        SpringApplication.run(NzbHydra.class, args);

    }

    @Bean
    public CacheManager getCacheManager() {
        GuavaCacheManager guavaCacheManager = new GuavaCacheManager("infos", "titles");
        return guavaCacheManager;
    }

    @RequestMapping(value = "/rss")
    public RssRoot get() {
        RestTemplate restTemplate = new RestTemplate();
        RssRoot rssRoot = restTemplate.getForObject("http://127.0.0.1:5000/api?apikey=a", RssRoot.class);

        return rssRoot;

    }

    @RequestMapping(value = "/delete")
    public String delete() {
        searchResultRepository.deleteAll();
        indexerRepository.deleteAll();

        return "Ok";
    }

    @RequestMapping(value = "/categories")
    public String getCats() {
        return categoryProvider.getCategories().stream().map(Category::getName).collect(Collectors.joining(","));

    }


    @RequestMapping("/test")
    public String test() throws InfoProviderException {
        return "";
    }


    @RequestMapping("/testconfig")
    public String testconfig() {
        return searchModuleConfigProvider.getIndexers().get(0).getName();
    }



}