package com.lixyz.lifekeeper.bean.recaptcha;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class RecaptchaBean {

    @JsonProperty("success")
    private Boolean success;
    @JsonProperty("error-codes")
    private List<String> errorcodes;
}
