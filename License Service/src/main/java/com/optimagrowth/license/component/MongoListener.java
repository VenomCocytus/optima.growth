package com.optimagrowth.license.component;

import com.optimagrowth.license.model.BaseModel;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MongoListener extends AbstractMongoEventListener<BaseModel> {

    @Override
    public void onBeforeConvert(BeforeConvertEvent<BaseModel> beforeConvertEvent) {
        super.onBeforeConvert(beforeConvertEvent);

        BaseModel baseModel = beforeConvertEvent.getSource();

        if(StringUtils.isBlank(baseModel.getId())) {
            baseModel.setCreateAt(LocalDateTime.now());
            baseModel.setUpdateAt(LocalDateTime.now());
        } else baseModel.setUpdateAt(LocalDateTime.now());
    }
}
