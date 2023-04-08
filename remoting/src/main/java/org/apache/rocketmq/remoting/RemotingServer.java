/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.rocketmq.remoting;

import io.netty.channel.Channel;
import java.util.concurrent.ExecutorService;
import org.apache.rocketmq.remoting.common.Pair;
import org.apache.rocketmq.remoting.exception.RemotingSendRequestException;
import org.apache.rocketmq.remoting.exception.RemotingTimeoutException;
import org.apache.rocketmq.remoting.exception.RemotingTooMuchRequestException;
import org.apache.rocketmq.remoting.netty.NettyRequestProcessor;
import org.apache.rocketmq.remoting.protocol.RemotingCommand;

/**
 * server 端的netty的接口
 */
public interface RemotingServer extends RemotingService {

    /**
     * 注册处理请求的处理器，策略模式，每个请求对应一个处理器
     *
     * @param requestCode 请求的code{@link org.apache.rocketmq.common.protocol.RequestCode}，代表某类请求
     * @param processor   这个请求对应的处理器，用来处理这个请求
     * @param executor
     */
    void registerProcessor(final int requestCode, final NettyRequestProcessor processor,
        final ExecutorService executor);

    /**
     * 注册默认的处理器，优先根据 requestCode 找 NettyRequestProcessor ，如果没找到，就用默认的
     *
     * 这里其实是为了方便来的，如果一个请求处理过程比较复杂，那么这个请求会对应一个NettyRequestProcessor实现，然后通过上面的registerProcessor方法注册，比如拉取消息的处理器{@link org.apache.rocketmq.broker.processor.PullMessageProcessor}
     * 但是有一些请求可能处理起来没那么麻烦，把这些请求都写到同一个NettyRequestProcessor实现中，然后通过registerDefaultProcessor来注册，就不用通过registerProcessor去一个一个注册了，比如{@link org.apache.rocketmq.broker.processor.AdminBrokerProcessor}
     *
     * @param processor
     * @param executor
     */
    void registerDefaultProcessor(final NettyRequestProcessor processor, final ExecutorService executor);

    int localListenPort();

    Pair<NettyRequestProcessor, ExecutorService> getProcessorPair(final int requestCode);

    /**
     * 同步调用，等待响应结果
     *
     * @param channel 服务端与客户端通信的channel
     * @param request
     * @param timeoutMillis
     * @return
     * @throws InterruptedException
     * @throws RemotingSendRequestException
     * @throws RemotingTimeoutException
     */
    RemotingCommand invokeSync(final Channel channel, final RemotingCommand request,
        final long timeoutMillis) throws InterruptedException, RemotingSendRequestException,
        RemotingTimeoutException;

    /**
     * 异步调用，不用同步等待结果，等有结果的话会回调InvokeCallback方法，这个方式是灰色，也就是说rocketmq没用过这个方法
     *
     * @param channel        服务端与客户端通信的channel
     * @param request
     * @param timeoutMillis
     * @param invokeCallback
     * @throws InterruptedException
     * @throws RemotingTooMuchRequestException
     * @throws RemotingTimeoutException
     * @throws RemotingSendRequestException
     */
    void invokeAsync(final Channel channel, final RemotingCommand request, final long timeoutMillis,
        final InvokeCallback invokeCallback) throws InterruptedException,
        RemotingTooMuchRequestException, RemotingTimeoutException, RemotingSendRequestException;

    /**
     * 单向调用，请求发送成功之后不关心调用的响应结果，发送过去就行
     *
     * @param channel
     * @param request
     * @param timeoutMillis
     * @throws InterruptedException
     * @throws RemotingTooMuchRequestException
     * @throws RemotingTimeoutException
     * @throws RemotingSendRequestException
     */
    void invokeOneway(final Channel channel, final RemotingCommand request, final long timeoutMillis)
        throws InterruptedException, RemotingTooMuchRequestException, RemotingTimeoutException,
        RemotingSendRequestException;

}
