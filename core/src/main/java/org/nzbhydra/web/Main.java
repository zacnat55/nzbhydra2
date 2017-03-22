package org.nzbhydra.web;

import org.nzbhydra.searching.CategoryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class Main {

    @Autowired
    private CategoryProvider categoryProvider;

    @RequestMapping(value = "/**", method = RequestMethod.GET)
    public String index(HttpSession session) {
        session.setAttribute("baseUrl", "/");
        Map<String, Object> bootstrappedData = new HashMap<>();
        Map<String, Object> safeConfig = new HashMap<>();
        safeConfig.put("key", "value");
        safeConfig.put("keyInt", 123);
        safeConfig.put("categories", categoryProvider.getCategories());
        bootstrappedData.put("safeConfig", safeConfig);
        session.setAttribute("bootstrap", bootstrappedData);
        return "index";
    }

    @RequestMapping(value = "/internalapi/getsearchrequestsforsearching") //TODO There might be a reason why this was POST originally
    @ResponseBody
    public String getSearchRequestsForSearching() {
        return "{}";
    }





}