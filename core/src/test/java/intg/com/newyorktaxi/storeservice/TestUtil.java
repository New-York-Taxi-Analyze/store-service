package com.newyorktaxi.storeservice;

import io.confluent.kafka.schemaregistry.client.rest.entities.SchemaString;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.http.entity.ContentType;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

@UtilityClass
public class TestUtil {

    @SneakyThrows
    public void registerSchema(int schemaId, String topic, String schema) {
        stubFor(post(urlPathMatching("/subjects/" + topic + "-value/versions"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", ContentType.APPLICATION_JSON.toString()).withBody("{\"id\":" + schemaId + "}")));

        final SchemaString schemaString = new SchemaString(schema);
        stubFor(get(urlPathMatching("/schemas/ids/" + schemaId))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", ContentType.APPLICATION_JSON.toString()).withBody(schemaString.toJson())));
    }
}
