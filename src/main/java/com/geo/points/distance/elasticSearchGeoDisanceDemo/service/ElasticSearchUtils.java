package com.geo.points.distance.elasticSearchGeoDisanceDemo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geo.points.distance.elasticSearchGeoDisanceDemo.model.Location;
import com.geo.points.distance.elasticSearchGeoDisanceDemo.model.Page;
import com.geo.points.distance.elasticSearchGeoDisanceDemo.model.UserDocument;
import com.geo.points.distance.elasticSearchGeoDisanceDemo.model.UserServiceabilityRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.geoDistanceQuery;

@Component
@Slf4j
public class ElasticSearchUtils {

    @Value("${elastic.users.index:users}")
    private String usersIndex;

    @Value("${elastic.index.env}")
    private String indexEnv;

    @Value("${es.bulk.batch.size:500}")
    private Integer batchSize;

    @Autowired
    private TransportClient esClient;

    public final String MAPPING_FILE_SUFFIX = ".mapping.json";
    public final String UTF_8 = "utf-8";
    public String LOCATION = "location";

    public String USER_DOC_TYPE = "user";

    String DEFAULT_PAGE_NUMBER = "1";
    String DEFAULT_PRODUCT_PAGE_SIZE = "20";

    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();

    BulkProcessor bulkProcessor;


    @PostConstruct
    public void loadRetailerDetails() {
        bulkProcessor = BulkProcessor.builder(
                esClient,
                new BulkProcessor.Listener() {
                    @Override
                    public void beforeBulk(long executionId, BulkRequest request) {
                        log.info("beforeBulk total numer of items to be pushed {}", request.numberOfActions());
                    }

                    @Override
                    public void afterBulk(long executionId,
                                          BulkRequest request,
                                          BulkResponse response) {
                        log.info("afterBulk, Was there any failure?" + response.hasFailures());
                        if (response.hasFailures()) {
                            log.info(response.buildFailureMessage());
                        }
                    }

                    @Override
                    public void afterBulk(long executionId,
                                          BulkRequest request,
                                          Throwable failure) {
                        log.info("afterBulk");
                    }
                })
                .setBulkActions(batchSize)
                .setBulkSize(new ByteSizeValue(5, ByteSizeUnit.MB))
                .setFlushInterval(TimeValue.timeValueSeconds(3))
                .setConcurrentRequests(1)
                .setBackoffPolicy(
                        BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3))
                .build();
    }


    public String usersIndex() {
        return indexEnv.concat(usersIndex);
    }

    public String getUserIndex() {
        return usersIndex;
    }


    public boolean isAlreadyDataPublished() throws Exception {

        boolean indexExists = isIndexExists(usersIndex(), USER_DOC_TYPE, getUserIndex());

        if (indexExists) {
            SearchResponse sr = esClient.prepareSearch()
                    .setIndices(usersIndex())
                    .setTypes(USER_DOC_TYPE)
                    .setSize(1)
                    .execute().actionGet();

            if (sr != null) {
                return sr.getHits().getTotalHits() > 0;
            }
            throw new RuntimeException("Error while running the count query");
        } else {
            log.warn("Index " + getUserIndex() + " does not exists or could not be created.");
        }
        return false;
    }

    public boolean isIndexExists(String index, String documentType, String indexFileName) throws InterruptedException, ExecutionException, IOException {
        return isIndexExists(index, documentType, indexFileName, true);
    }

    public boolean isIndexExists(String index, String documentType, String indexFileName, boolean createIfNotFound) throws InterruptedException, ExecutionException, IOException {
        IndicesAdminClient indicesAdminClient = getAdminClient(esClient);
        IndicesExistsResponse response = getAdminClient(esClient).exists(new IndicesExistsRequest().indices(index)).get();
        boolean indexExists = response.isExists();

        if (!indexExists && createIfNotFound) {
            Resource resource = new ClassPathResource("es/" + indexFileName + MAPPING_FILE_SUFFIX);
            if (resource.exists()) {
                String mapping = IOUtils.toString(resource.getInputStream(), UTF_8);
                indicesAdminClient.prepareCreate(index).addMapping(documentType, mapping, XContentType.JSON).get();
                log.info("Index ::" + index + ":" + documentType + ":" + indexFileName + " with Mapping Created ");
                indexExists = true;
            }
        }
        return indexExists;
    }

    private IndicesAdminClient getAdminClient(TransportClient esClient) {
        return esClient.admin().indices();
    }


    public UserDocument populateRetailerDetailsForEs(Object[] retailerObject) {
        UserDocument UserDocument = new UserDocument();
        try {
            UserDocument.setId(Long.parseLong(retailerObject[0].toString()));
            UserDocument.setName((String) retailerObject[1]);
            UserDocument.setMobileNumber((String) retailerObject[2]);
            UserDocument.setShopName((String) retailerObject[3]);
            UserDocument.setShopType((String) retailerObject[4]);
            UserDocument.setStreetAddress((String) retailerObject[5]);
            UserDocument.setCity((String) retailerObject[6]);
            UserDocument.setState((String) retailerObject[7]);
            UserDocument.setPincode((String) retailerObject[8]);
            UserDocument.setShopSize((String) retailerObject[9]);
            UserDocument.setLocation(Location.builder().lat((Double) retailerObject[10])
                    .lon((Double) retailerObject[11]).build());
            UserDocument.setShopImageUri((String) retailerObject[12]);
            UserDocument.setConsumerProgramEnabled((boolean) retailerObject[13]);

        } catch (Exception e) {
            log.error("error while populating retailer details", e);
            return null;
        }
        return UserDocument;
    }


    public void indexDocument(UserDocument UserDocument) {
        log.info("Updating Retailer Id : {} and Document for", UserDocument.getId(), UserDocument);
        bulkProcessor.add((new IndexRequest(usersIndex()).type(USER_DOC_TYPE.trim()).id(String.valueOf(UserDocument.getId())).source(gson.toJson(UserDocument), XContentType.JSON)));
    }


    public List<UserDocument> getServicableRetailer(UserServiceabilityRequest UserServiceabilityRequest) {
        BoolQueryBuilder qb = boolQuery()
                .minimumShouldMatch(1)
                .filter(geoDistanceQuery(LOCATION)
                        .point(UserServiceabilityRequest.getLatitude(), UserServiceabilityRequest.getLongitude())
                        .distance(UserServiceabilityRequest.getRadius(), DistanceUnit.KILOMETERS));


        Page page = new Page().setNumber(UserServiceabilityRequest.getPageNumber()).setSize(UserServiceabilityRequest.getPageSize());
        SearchRequestBuilder srb = esClient.prepareSearch()
                .setIndices(usersIndex())
                .setTypes(USER_DOC_TYPE)
                .setQuery(qb)
                .addSort(SortBuilders.geoDistanceSort(LOCATION, UserServiceabilityRequest.getLatitude(), UserServiceabilityRequest.getLongitude())
                        .order(SortOrder.ASC)
                        .unit(DistanceUnit.KILOMETERS))
                .setFrom(calculateFromForProduct(page))
                .setSize(page.getSize());

        log.info(" Retailer Servicability ES Query : {}", srb.toString());
        return parseQueryresponse(srb.execute().actionGet());
    }

    private List<UserDocument> parseQueryresponse(SearchResponse sr) {

        List<UserDocument> UserDocumentList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            log.info("Found result hits : " + sr.getHits().getHits().length);
            for (SearchHit hit : sr.getHits()) {
                UserDocument ud = mapper.readValue(hit.getSourceAsString(), UserDocument.class);
                if (hit.getSortValues() != null && hit.getSortValues().length > 0) {
                    ud.setDistance((Double) hit.getSortValues()[0]);
                }
                UserDocumentList.add(ud);
            }
        } catch (Exception e) {
            log.error("Error while parsing the Retailer Document ", e);
        }
        return UserDocumentList;
    }

    private int calculateFromForProduct(Page page) {
        return (page.getNumber() - 1) * (page.getSize() == null ? Integer.valueOf(DEFAULT_PRODUCT_PAGE_SIZE) : page.getSize());
    }
}

