package cyou.oxling.loanappbackend.model.spider.shixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data // 自动生成 getter、setter、toString、equals、hashCode
public class DispDataItem {

    @JsonProperty("OtherInfo")
    private List<?> OtherInfo;

    private int dispNum;

    @JsonProperty("disp_data")
    private List<DispDataDetail> disp_data;

    private String hilight;
    private int listNum;
    private int resNum;
    private int status;
}
