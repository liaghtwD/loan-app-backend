package cyou.oxling.loanappbackend.model.spider.shixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data // 自动生成 getter、setter、toString、equals、hashCode
public class DispDataDetail {

    @JsonProperty("SiteId")
    private int SiteId;

    @JsonProperty("StdStg")
    private int StdStg;

    @JsonProperty("StdStl")
    private int StdStl;

    @JsonProperty("_select_time")
    private long _select_time;

    @JsonProperty("_update_time")
    private String _update_time;

    @JsonProperty("_version")
    private int _version;

    @JsonProperty("age")
    private String age;

    @JsonProperty("areaName")
    private String areaName;

    @JsonProperty("areaNameNew")
    private String areaNameNew;

    @JsonProperty("businessEntity")
    private String businessEntity;

    @JsonProperty("cambrian_appid")
    private String cambrian_appid;

    @JsonProperty("cardNum")
    private String cardNum;

    @JsonProperty("caseCode")
    private String caseCode;

    @JsonProperty("changefreq")
    private String changefreq;

    @JsonProperty("courtName")
    private String courtName;

    @JsonProperty("disruptTypeName")
    private String disruptTypeName;

    @JsonProperty("duty")
    private String duty;

    @JsonProperty("focusNumber")
    private String focusNumber;

    @JsonProperty("gistId")
    private String gistId;

    @JsonProperty("gistUnit")
    private String gistUnit;

    @JsonProperty("iname")
    private String iname;

    @JsonProperty("lastmod")
    private String lastmod;

    @JsonProperty("loc")
    private String loc;

    @JsonProperty("partyTypeName")
    private String partyTypeName;

    @JsonProperty("performance")
    private String performance;

    @JsonProperty("performedPart")
    private String performedPart;

    @JsonProperty("priority")
    private String priority;

    @JsonProperty("publishDate")
    private String publishDate;

    @JsonProperty("publishDateStamp")
    private String publishDateStamp;

    @JsonProperty("regDate")
    private String regDate;

    @JsonProperty("sexy")
    private String sexy;

    @JsonProperty("sitelink")
    private String sitelink;

    @JsonProperty("title")
    private String title;

    @JsonProperty("type")
    private String type;

    @JsonProperty("unperformPart")
    private String unperformPart;

    @JsonProperty("xzhId")
    private String xzhId;
}
