package de.mpg.mpdl.r2d2.rest.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.mpg.mpdl.r2d2.exceptions.AuthorizationException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.Affiliation;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.search.service.AffiliationSearchService;
import de.mpg.mpdl.r2d2.search.service.impl.AffiliationSearchServiceImpl;
import de.mpg.mpdl.r2d2.util.Utils;

@RestController
@RequestMapping("/vocabulary")
public class VocabularyController {

  @Autowired
  RestTemplate restTemplate;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  AffiliationSearchServiceImpl affiliationSearchService;

  @GetMapping("/ous")
  public ResponseEntity<List<Affiliation>> search(@RequestParam("q") String query) throws IOException, R2d2TechnicalException {

    SearchResponse resp = affiliationSearchService.suggestOUs(query);
    SearchHit[] hits = resp.getHits().getHits();
    List<Affiliation> list = Arrays.stream(hits).map(hit -> hit.getSourceAsMap()).map(map -> {
      Affiliation ou = new Affiliation();
      ou.setOrganization((String) map.get("name"));
      ou.setId((String) map.get("id"));
      return ou;
    }).collect(Collectors.toList());

    return new ResponseEntity(list, HttpStatus.OK);
  }

  @GetMapping("/ous/{id}")
  public ResponseEntity<?> getOU(@PathVariable("id") String id) throws R2d2TechnicalException {
    SearchResponse resp = affiliationSearchService.ouDetails(id);
    SearchHit[] hits = resp.getHits().getHits();
    if (hits.length > 0) {
      return new ResponseEntity(hits[0].getSourceAsMap(), HttpStatus.OK);
    }
    return null;
  }

  @PostMapping("/ous")
  public ResponseEntity<?> ous(@RequestBody JsonNode query_params, @AuthenticationPrincipal R2D2Principal p)
      throws IOException, AuthorizationException, R2d2TechnicalException {

    String query = objectMapper.writeValueAsString(query_params);
    SearchSourceBuilder ssb = Utils.parseJsonToSearchSourceBuilder(query);
    SearchResponse resp = affiliationSearchService.searchDetailed(ssb, -1, false, p);
    SearchHit[] hits = resp.getHits().getHits();
    List<Object> list = Arrays.stream(hits).map(hit -> hit.getSourceAsMap()).map(map -> {
      return objectMapper.valueToTree(map);
    }).collect(Collectors.toList());

    return new ResponseEntity(list, HttpStatus.OK);
  }

}
