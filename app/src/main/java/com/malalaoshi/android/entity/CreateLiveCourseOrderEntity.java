package com.malalaoshi.android.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Create course order entity
 * Created by tianwei on 2/27/16.
 */
public class CreateLiveCourseOrderEntity extends JsonBodyBase implements Serializable {
    private long live_class;

    public long getLive_class() {
        return live_class;
    }

    public void setLive_class(long live_class) {
        this.live_class = live_class;
    }
}
