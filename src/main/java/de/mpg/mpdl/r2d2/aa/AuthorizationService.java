package de.mpg.mpdl.r2d2.aa;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.joda.time.chrono.IslamicChronology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.mpg.mpdl.r2d2.db.ReviewTokenRepository;
import de.mpg.mpdl.r2d2.db.UserAccountRepository;
import de.mpg.mpdl.r2d2.exceptions.AuthorizationException;
import de.mpg.mpdl.r2d2.exceptions.R2d2ApplicationException;
import de.mpg.mpdl.r2d2.exceptions.R2d2TechnicalException;
import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.DatasetVersion;
import de.mpg.mpdl.r2d2.model.ReviewToken;
import de.mpg.mpdl.r2d2.model.aa.Grant;
import de.mpg.mpdl.r2d2.model.aa.R2D2Principal;
import de.mpg.mpdl.r2d2.model.aa.UserAccount;
import de.mpg.mpdl.r2d2.model.aa.UserAccount.Role;
import de.mpg.mpdl.r2d2.search.es.daoimpl.DatasetVersionDaoImpl;


@Service
public class AuthorizationService {

  private final static Logger LOGGER = LoggerFactory.getLogger(AuthorizationService.class);

  private Map<String, Object> aaMap;

  @Autowired
  private UserAccountRepository userAccountRepository;

  @Autowired
  private ReviewTokenRepository reviewTokenRepository;


  private ObjectMapper modelMapper;


  public enum AccessType {
    GET("get"),
    READ_FILE("readFile"),
    SUBMIT("submit"),
    RELEASE("release"),
    DELETE("delete"),
    WITHDRAW("withdraw"),
    EDIT("update"),
    REVISE("revise");


    private String methodName;

    private AccessType(String methodName) {
      this.setMethodName(methodName);
    }

    public String getMethodName() {
      return methodName;
    }

    public void setMethodName(String methodName) {
      this.methodName = methodName;
    }

  }

  public AuthorizationService(ObjectMapper objectMapper) {

    try {

      this.modelMapper = objectMapper;
      aaMap = modelMapper.readValue(AuthorizationService.class.getClassLoader().getResourceAsStream("aa.json"), Map.class);

    } catch (Exception e) {
      throw new RuntimeException("Problem with parsing aa.json file.", e);
    }

  }

  public QueryBuilder modifyQueryForAa(String serviceName, String serviceMethod, QueryBuilder query, R2D2Principal principal,
      Object... objects) throws AuthorizationException, R2d2TechnicalException {

    QueryBuilder filterQuery = getAaFilterQuery(serviceName, serviceMethod, principal, objects);

    if (filterQuery != null) {
      BoolQueryBuilder completeQuery = QueryBuilders.boolQuery();
      if (query != null) {
        completeQuery.must(query);
      }
      completeQuery.filter(filterQuery);
      return completeQuery;
    }

    return query;

  }


  public QueryBuilder getAaFilterQuery(String serviceName, String serviceMethod, Object... objects)
      throws AuthorizationException, R2d2TechnicalException {
    Map<String, Map<String, Object>> serviceMap = (Map<String, Map<String, Object>>) aaMap.get(serviceName);

    List<String> order = (List<String>) serviceMap.get("technical").get("order");
    Map<String, String> indices = (Map<String, String>) serviceMap.get("technical").get("indices");
    List<Map<String, Object>> allowedMap = (List<Map<String, Object>>) serviceMap.get(serviceMethod);

    R2D2Principal principal = null;
    UserAccount userAccount = null;
    try {
      principal = ((R2D2Principal) objects[order.indexOf("user")]);
      userAccount = principal.getUserAccount();
    } catch (NullPointerException e) {
      userAccount = null;

    }

    BoolQueryBuilder bqb = QueryBuilders.boolQuery();

    /*
    as we have one index with all public items and latest versions, we have to filter out those public items, which 
    are not the latest release and of which the authenticated user is allowed to see the latest version
        AA Filter Query
        AND NOT
        (
          dataset is NOT the lastest version
          AND
          user is allowed to see the latest private version (=owner or data manager or admin)
        )
    */

    /*
    boolean filterDuplicates = (Boolean) serviceMap.get("technical").getOrDefault("filterIndexDatasetDuplicates", Boolean.FALSE);
    BoolQueryBuilder filterDuplicatesQueryBuilder = QueryBuilders.boolQuery();
    QueryBuilder isLatestVersionQuery = QueryBuilders.scriptQuery(new Script(
        "doc['" + DatasetVersionDaoImpl.INDEX_DATASET_LATEST_VERSION + "']==doc['" + DatasetVersionDaoImpl.INDEX_VERSION_NUMBER + "']"));
    filterDuplicatesQueryBuilder.mustNot(isLatestVersionQuery);
    BoolQueryBuilder userQueryBuilder = new BoolQueryBuilder();
    filterDuplicatesQueryBuilder.must(userQueryBuilder);
    */

    if (allowedMap == null) {
      throw new AuthorizationException("No rules for service " + serviceName + ", method " + "get");
    }

    // everybody can see anything
    if (allowedMap.isEmpty()) {
      return null;
    }

    for (Map<String, Object> rules : allowedMap) {

      BoolQueryBuilder subQb = QueryBuilders.boolQuery();
      boolean userMatch = false;

      //BoolQueryBuilder subUserQueryBuilder = QueryBuilders.boolQuery();

      // Everybody is allowed to see everything
      rulesLoop: for (Entry<String, Object> rule : rules.entrySet()) {
        switch (rule.getKey()) {
          case "user": {

            Map<String, String> userMap = (Map<String, String>) rule.getValue();
            if (userAccount != null) {


              if (userMap.containsKey("field_user_id_match")) {
                String value = (String) userMap.get("field_user_id_match");

                QueryBuilder userQuery = QueryBuilders.termQuery(indices.get(value), userAccount.getId().toString());
                subQb.must(userQuery);
                userMatch = true;
                //subUserQueryBuilder.must(userQuery);


              }

              if (userMap.containsKey("role") || userMap.containsKey("field_grant_id_match")) {


                BoolQueryBuilder grantQueryBuilder = QueryBuilders.boolQuery();
                for (Grant grant : userAccount.getGrants()) {
                  if (grant.getRole().name().equalsIgnoreCase((String) userMap.get("role"))) {
                    userMatch = true;

                    if (userMap.get("field_grant_id_match") != null) {
                      /*
                      if (grant.getDataset() != null && grant.getObjectRef().startsWith("ou_")) {
                        List<String> ouIds = new ArrayList<>();
                        ouIds.add(grant.getObjectRef());
                        List<AffiliationDbVO> childList = new ArrayList<>();
                        searchAllChildOrganizations(ouIds.get(0), childList);
                        grantQueryBuilder.should(QueryBuilders.termsQuery(indices.get(userMap.get("field_grant_id_match")), ouIds));
                      } else {*/
                      grantQueryBuilder.should(
                          QueryBuilders.termsQuery(indices.get(userMap.get("field_grant_id_match")), grant.getDataset().toString()));
                      //}

                    }


                  }
                }

                if (grantQueryBuilder.hasClauses()) {
                  subQb.must(grantQueryBuilder);
                  //subUserQueryBuilder.must(grantQueryBuilder);

                }



              }


              /*
              if (userMap.containsKey("field_ou_id_match")) {
                String userOuId = userAccount.getAffiliation().getObjectId();
                List<String> ouIds = new ArrayList<>();
                ouIds.add(userOuId);
                List<AffiliationDbVO> childList = new ArrayList<>();
                searchAllChildOrganizations(ouIds.get(0), childList);
                subQb.must(QueryBuilders.termsQuery(UserAccountServiceImpl.INDEX_AFFIlIATION_OBJECTID, ouIds));
              }
              */

            }
            if (userMap.containsKey("token_match") && principal.getReviewToken() != null) {
              ReviewToken reviewToken = reviewTokenRepository.findByToken(principal.getReviewToken()).orElse(null);

              if (reviewToken != null) {
                subQb.must(QueryBuilders.termsQuery(indices.get(userMap.get("token_match")), reviewToken.getDataset().toString()));
                userMatch = true;
              }



            }

            if (!userMatch) {
              //reset queryBuilder
              subQb = QueryBuilders.boolQuery();
              //subUserQueryBuilder = QueryBuilders.boolQuery();
              break rulesLoop;
            }



            break;
          }
          default: {
            String key = rule.getKey();
            String index = indices.get(key);

            if (index == null) {
              throw new AuthorizationException("No index in aa.json defined for: " + key);
            }

            if (rule.getValue() instanceof Collection<?>) {
              List<String> valuesToCompare = (List<String>) rule.getValue();
              if (valuesToCompare.size() > 1) {
                BoolQueryBuilder valueQueryBuilder = QueryBuilders.boolQuery();
                for (String val : valuesToCompare) {
                  valueQueryBuilder.should(QueryBuilders.termQuery(index, val));
                }
                subQb.must(valueQueryBuilder);
              } else {
                subQb.must(QueryBuilders.termQuery(index, valuesToCompare.get(0)));
              }



            } else {
              Object value = getFieldValueOrString(order, objects, (String) rule.getValue());
              if (value != null) {
                subQb.must(QueryBuilders.termQuery(index, value.toString()));
              }
            }

            break;
          }
        }
      }



      /*
      if (subUserQueryBuilder.hasClauses()) {
        userQueryBuilder.should(subUserQueryBuilder);
      }
      */
      if (subQb.hasClauses()) {
        bqb.should(subQb);
      } else if (userMatch) {
        // User matches and no more rules -> User can see everything (=ADMIN!)
        // If we have to filter duplicates, only return latest releases in this case
        //return filterDuplicates ? isLatestVersionQuery : null;
        return null;
      }

    }



    if (bqb.hasClauses()) {
      /*
      if (filterDuplicates && userQueryBuilder.hasClauses()) {
        bqb.mustNot(filterDuplicatesQueryBuilder);
      }
      */
      return bqb;
    }
    throw new AuthorizationException("This search requires a login");
  }



  /*
  public Principal checkLoginRequired(String authenticationToken)
      throws AuthenticationException, R2d2TechnicalException, R2d2ApplicationException, AuthorizationException {
    return new Principal(userAccountService.get(authenticationToken), authenticationToken);
  }
  */



  public void checkAuthorization(String serviceName, String methodName, Object... objects)
      throws AuthorizationException, R2d2TechnicalException {

    Map<String, Map<String, Object>> serviceMap = (Map<String, Map<String, Object>>) aaMap.get(serviceName);
    if (serviceMap == null) {
      throw new AuthorizationException("Nor rules for service " + serviceName);
    }
    List<String> order = (List<String>) serviceMap.get("technical").get("order");
    List<Map<String, Object>> allowedMap = (List<Map<String, Object>>) serviceMap.get(methodName);

    if (allowedMap == null) {
      throw new AuthorizationException("No rules for service " + serviceName + ", method " + methodName);
    }

    else {
      Exception lastExceptionOfAll = null;
      for (Map<String, Object> rules : allowedMap) {
        Exception lastExceptionOfRule = null;

        for (Entry<String, Object> rule : rules.entrySet()) {

          try {
            switch (rule.getKey()) {
              case "user": {
                checkUser((Map<String, Object>) rule.getValue(), order, objects);
                break;
              }
              default: {
                String key = rule.getKey();
                Object keyValueObject = getFieldValueOrString(order, objects, key);
                String keyValue = keyValueObject != null ? keyValueObject.toString() : null;
                boolean check = false;
                if (rule.getValue() instanceof Collection<?>) {
                  List<String> valuesToCompare = (List<String>) rule.getValue();
                  check = valuesToCompare.stream().anyMatch(val -> keyValue != null && val != null && val.equalsIgnoreCase(keyValue));
                  if (!check) {
                    throw new AuthorizationException("Expected one of " + valuesToCompare + " for field " + key + " (" + keyValue + ")");
                  }
                } else {
                  Object val = getFieldValueOrString(order, objects, (String) rule.getValue().toString());
                  String value = null;
                  if (val != null) {
                    value = val.toString();
                  }
                  check = (keyValue != null && keyValue.equalsIgnoreCase(value));
                  if (!check) {
                    throw new AuthorizationException("Expected value [" + value + "] for field " + key + " (" + keyValue + ")");
                  }
                }


                break;
              }


            }
          } catch (AuthorizationException e) {
            lastExceptionOfRule = e;
            lastExceptionOfAll = e;
            break;
          }


        }

        if (lastExceptionOfRule == null) {
          return;
        }
      }
      if (lastExceptionOfAll == null) {
        return;
      } else {
        if (lastExceptionOfAll instanceof AuthorizationException)
          throw (AuthorizationException) lastExceptionOfAll;
        /*
        else if (lastExceptionOfAll instanceof AuthenticationException) {
          throw (AuthenticationException) lastExceptionOfAll;
        }
        */
      }


    }

  }

  private void checkUser(Map<String, Object> ruleMap, List<String> order, Object[] objects)
      throws AuthorizationException, R2d2TechnicalException {
    R2D2Principal principal = (R2D2Principal) objects[order.indexOf("user")];


    if (principal == null) {
      throw new AuthorizationException("You have to be logged in with username/password or review token.");
    }

    UserAccount userAccount = principal.getUserAccount();

    String tokenMatch = (String) ruleMap.get("token_match");

    if (tokenMatch != null) {
      if (principal.getReviewToken() == null) {
        throw new AuthorizationException("Review token is null");
      }

      ReviewToken reviewToken = reviewTokenRepository.findByToken(principal.getReviewToken())
          .orElseThrow(() -> new AuthorizationException("Invalid review token: " + principal.getReviewToken()));


      Collection<Object> idsToBeMatched = new ArrayList<>();
      Object idToBeMatched = getFieldValueOrString(order, objects, tokenMatch);
      if (idToBeMatched instanceof Collection) {
        idsToBeMatched = (Collection<Object>) idToBeMatched;
      } else {
        idsToBeMatched.add(idToBeMatched);
      }

      boolean check = idsToBeMatched.stream().anyMatch(i -> reviewToken.getDataset().equals(i));

      if (!check) {
        throw new AuthorizationException("Review token " + principal.getReviewToken() + " does not give access to " + idsToBeMatched);
      }


    } else if (userAccount == null) {
      throw new AuthorizationException("You have to be logged in with username/password or review token.");
    }


    /*
    String ipMatch = (String) ruleMap.get("ip_match");
    
    if (ipMatch != null) {
      DecodedJWT decodedJwt = userAccountService.verifyToken(principal.getJwToken());
    
      if (decodedJwt.getHeaderClaim("ip") != null) {
        try {
          Collection<String> ouIdsToBeMatched = new ArrayList<>();
          Object ouIdToBeMatched = getFieldValueOrString(order, objects, ipMatch);
          if (ouIdToBeMatched instanceof String) {
            ouIdsToBeMatched.add(ouIdToBeMatched.toString());
          } else if (ouIdToBeMatched instanceof Collection) {
            ouIdsToBeMatched = (Collection<String>) ouIdToBeMatched;
          }
    
          String userIp = decodedJwt.getHeaderClaim("ip").asString();
          boolean check = false;
          for (String ouId : ouIdsToBeMatched) {
            IpRange ouIpRange = ipListProvider.get(ouId);
    
            if (ouIpRange.matches(userIp)) {
              check = true;
              break;
            }
          }
    
    
          if (!check) {
            throw new AuthenticationException(
                "The current user's ip adress " + userIp + " does not match required ip range  of organization with id " + ouIdToBeMatched);
          }
        } catch (Exception e) {
          throw new AuthenticationException("Error while matching IPs", e);
        }
    
      } else {
        throw new AuthenticationException("Token contains no IP, but IP match is required");
      }
    
    } else if (userAccount == null) {
      throw new AuthenticationException("You have to be logged in with username/password.");
    
    }
    
    
    */

    String userIdFieldMatch = (String) ruleMap.get("field_user_id_match");

    if (userIdFieldMatch != null) {
      Object userId = getFieldValueOrString(order, objects, userIdFieldMatch);
      String expectedUserId = (userId != null ? userId.toString() : null);

      if (expectedUserId == null || !expectedUserId.equals(userAccount.getId().toString())) {
        throw new AuthorizationException("User is not owner of object.");
      }
    }


    if (ruleMap.containsKey("role") || ruleMap.containsKey("field_grant_id_match")) {
      boolean check = false;
      String role = (String) ruleMap.get("role");

      String grantFieldMatch = (String) ruleMap.get("field_grant_id_match");

      List<String> grantFieldMatchValues = new ArrayList<>();
      if (grantFieldMatch != null) {
        Object val = getFieldValueOrString(order, objects, grantFieldMatch);
        if (val != null) {
          grantFieldMatchValues.add(val.toString());
        } else {
          LOGGER.warn("getFieldValue for " + grantFieldMatch + "returned null!");
        }



      }


      // If grant is of type "ORGANIZATION", get all children of organization as potential matches
      /*
      if (grantFieldMatch != null && (!grantFieldMatchValues.isEmpty()) && grantFieldMatchValues.get(0).startsWith("ou")) {
        List<AffiliationDbVO> childList = new ArrayList<>();
        searchAllChildOrganizations(grantFieldMatchValues.get(0), childList);
        grantFieldMatchValues.addAll(childList.stream().map(aff -> aff.getObjectId()).collect(Collectors.toList()));
      
      }
      */



      for (Grant grant : userAccount.getGrants()) {
        check = (role == null || role.equals(grant.getRole().name())) && (grantFieldMatch == null
            || (grant.getDataset() != null && grantFieldMatchValues.stream().anyMatch(id -> id.equals(grant.getDataset().toString()))));

        if (check) {
          break;
        }
      }
      /*
      for (Role grant : userAccount.getRoles()) {
        check = (role == null || role.equalsIgnoreCase(grant.name()));
      
        if (check) {
          break;
        }
      }
      */

      if (!check) {
        throw new AuthorizationException(
            "Expected user with role [" + role + "], on object [" + grantFieldMatchValues + "] (" + grantFieldMatch + ")");
      }

    }



    /*
    if (ruleMap.containsKey("field_ou_id_match")) {
      String userOuId = principal.getUserAccount().getAffiliation().getObjectId();
      String ouFieldMatch = (String) ruleMap.get("field_ou_id_match");
    
      if (ouFieldMatch != null) {
        Object val = getFieldValueOrString(order, objects, ouFieldMatch);
    
        if (val == null) {
          throw new AuthorizationException(
              "User with ou [" + userOuId + "] is not allowed to access object with field " + ouFieldMatch + "[" + val + "]");
        } else {
          List<String> ouIds = new ArrayList<>();
          ouIds.add(userOuId);
          List<AffiliationDbVO> childList = new ArrayList<>();
          searchAllChildOrganizations(ouIds.get(0), childList);
    
          if (!ouIds.contains(val.toString())) {
            throw new AuthorizationException(
                "User with ou [" + userOuId + "] is not allowed to access object with field " + ouFieldMatch + "[" + val + "]");
          }
        }
    
      }
    
    }
    */


  }



  private Object getFieldValueOrString(List<String> order, Object[] objects, String field) throws AuthorizationException {
    if (field.contains(".")) {
      String[] fieldHierarchy = field.split("\\.");
      Object object;
      try {
        object = objects[order.indexOf(fieldHierarchy[0])];
      } catch (NullPointerException e) {
        return null;
      }
      if (object == null) {
        return null;
      } else {
        List<Object> results = new ArrayList<Object>();
        getFieldValueViaGetter(object, field.substring(field.indexOf(".") + 1, field.length()), results);
        if (results.isEmpty()) {
          return null;
        } else if (results.size() == 1) {
          return results.get(0);
        } else {
          return results;
        }
      }



    } else {
      return field;
    }
  }

  private void getFieldValueViaGetter(Object object, String field, List<Object> results) throws AuthorizationException {
    try {
      String[] fieldHierarchy = field.split("\\.");

      for (PropertyDescriptor pd : Introspector.getBeanInfo(object.getClass()).getPropertyDescriptors()) {

        if (pd.getName().equals(fieldHierarchy[0])) {
          Object value = pd.getReadMethod().invoke(object);
          if (value == null) {
            return;
          }

          if (fieldHierarchy.length == 1) {
            results.add(value);
            return;

          } else {
            String nextField = field.substring(field.indexOf(".") + 1, field.length());
            if (value instanceof Collection) {

              for (Object o : (Collection<Object>) value) {
                getFieldValueViaGetter(o, nextField, results);
              }

            } else {
              getFieldValueViaGetter(value, nextField, results);
            }

          }
        }

      }


    } catch (Exception e) {
      throw new AuthorizationException("Error while calling getter in object", e);
    }
    return;

  }


}
