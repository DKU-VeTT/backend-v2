package kr.ac.dankook.VettObservabilityService.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

@Document(indexName = "vett-logs-*")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogDocument {

    @Id
    private String id;

    @Field(name = "@timestamp", type = FieldType.Date)
    private String timestamp;

    @Field(type = FieldType.Keyword)
    private String level;

    @Field(name = "level_value", type = FieldType.Integer)
    private Integer levelValue;

    @Field(type = FieldType.Keyword)
    private String service;

    @Field(type = FieldType.Keyword)
    private String app;

    @Field(name = "logger_name", type = FieldType.Keyword)
    private String loggerName;

    @Field(name = "thread_name", type = FieldType.Keyword)
    private String threadName;

    @Field(type = FieldType.Text)
    private String message;

    @Field(type = FieldType.Keyword)
    private String traceId;

    @Field(type = FieldType.Keyword)
    private String spanId;

}
