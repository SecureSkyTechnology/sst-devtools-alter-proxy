package com.sst.devtools.alterproxy;

import java.net.URI;

import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpRequest;

public class LFSMappableHttpFiltersSource extends HttpFiltersSourceAdapter {
    private final LFSMapper lfsm;

    public LFSMappableHttpFiltersSource(LFSMapper lfsm) {
        this.lfsm = lfsm;
    }

    @Override
    public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
        try {
            final URI uri = new URI(originalRequest.getUri());
            final String ext = ApacheHttpdMimeTypes.defaultMimeTypes.getExtension(uri.getPath());
            if ("iso".equals(ext) || "dmg".equals(ext) || "exe".equals(ext)) {
                return new HttpFiltersAdapter(originalRequest, ctx) {
                    @Override
                    public void proxyToServerConnectionSucceeded(ChannelHandlerContext serverCtx) {
                        ChannelPipeline pipeline = serverCtx.pipeline();
                        if (pipeline.get("inflater") != null) {
                            pipeline.remove("inflater");
                        }
                        if (pipeline.get("aggregator") != null) {
                            pipeline.remove("aggregator");
                        }
                        super.proxyToServerConnectionSucceeded(serverCtx);
                    }
                };
            }
        } catch (Exception ignore) {
        }
        return new LFSMappableHttpFilters(originalRequest, ctx, this.lfsm);
    }

    @Override
    public int getMaximumRequestBufferSizeInBytes() {
        return 1024 * 1024; // aggregate chunks and decompress until 1MB request.
    }

    @Override
    public int getMaximumResponseBufferSizeInBytes() {
        return 1024 * 1024; // aggregate chunks and decompress until 1MB response.
    }

}
