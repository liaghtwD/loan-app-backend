package cyou.oxling.loanappbackend.model.spider.shixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data // 自动生成 getter、setter、toString、equals、hashCode 等方法
public class ResponseData {
    private List<DispDataItem> data;
}
