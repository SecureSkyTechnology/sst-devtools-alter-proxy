package com.sst.devtools.alterproxy;

import java.net.URI;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

public class LFSMappableHttpFilters extends HttpFiltersAdapter {
    private static Logger LOG = LoggerFactory.getLogger(LFSMappableHttpFilters.class);
    private final LFSMapper lfsm;

    public LFSMappableHttpFilters(HttpRequest originalRequest, ChannelHandlerContext ctx, LFSMapper lfsm) {
        super(originalRequest, ctx);
        this.lfsm = lfsm;
    }

    @Override
    public HttpResponse proxyToServerRequest(HttpObject httpObject) {
        if (httpObject instanceof FullHttpRequest) {
            FullHttpRequest fhr = (FullHttpRequest) httpObject;
            final HttpMethod method = fhr.getMethod();
            final String hostHeader = HttpHeaders.getHost(fhr, "");
            LOG.info("proxy -> server, Host={}, {}, {}", hostHeader, method, fhr.getUri());
            boolean isLFSMappableMethod =
                HttpMethod.GET.equals(method)
                    || HttpMethod.POST.equals(method)
                    || HttpMethod.PUT.equals(method)
                    || HttpMethod.DELETE.equals(method)
                    || HttpMethod.PATCH.equals(method);
            URI uri = null;
            try {
                // schema + host is dummy.
                uri = new URI(fhr.getUri());
            } catch (Exception e) {
                LOG.trace("getUri() error", e);
                isLFSMappableMethod = false;
            }
            if (!StringUtils.isEmpty(hostHeader) && isLFSMappableMethod && Objects.nonNull(uri)) {
                final String path = uri.getPath();
                LOG.trace("matcher path = {}", path);
                LFSMapEntry map = lfsm.getMap(hostHeader, path);
                if (Objects.nonNull(map)) {
                    LOG.info("proxy -> server, map path={} to {}", path, map.getLocalDir().getAbsolutePath());
                    return LFSMapper.createMappedResponse(map, path);
                }
            }
        }
        return null;
    }
}
