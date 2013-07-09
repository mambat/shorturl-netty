package org.n3r.shorturl.redis;

import org.apache.commons.lang3.StringUtils;

public class HostPort {
    private String host;
    private int port;

    public HostPort(String hostPort) {
        this(hostPort, 80);
    }

    public HostPort(String hostPort, int defaultPort) {
        this(hostPort, "127.0.0.1", defaultPort);
    }

    public HostPort(String hostPort, String defaultHost, int defaultPort) {
        if (StringUtils.isEmpty(hostPort)) {
            host = defaultHost;
            port = defaultPort;
        }
        else {
            String[] redisConnArr = hostPort.split(":");
            host = ParamsUtils.getStr(redisConnArr, 0, defaultHost);
            port = ParamsUtils.getInt(redisConnArr, 1, defaultPort);
        }
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
