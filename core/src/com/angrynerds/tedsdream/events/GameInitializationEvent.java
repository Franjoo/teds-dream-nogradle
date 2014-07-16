package com.angrynerds.tedsdream.events;

import java.io.Serializable;

/**
 * Author: Franz Benthin
 */
public class GameInitializationEvent implements Serializable {

    private String serialization;

    public GameInitializationEvent(String serialization) {
        this.serialization = serialization;
    }

    public String getSerialization() {
        return serialization;
    }

}
