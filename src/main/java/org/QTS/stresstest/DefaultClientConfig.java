package org.QTS.stresstest;

import com.tvd12.ezyfoxserver.client.config.EzyClientConfig;

public class DefaultClientConfig {

    public EzyClientConfig get(int count) {
        return EzyClientConfig.builder()
            .clientName("Player#" + count)
            .zoneName("homeTown")
            .build();
    }
}
