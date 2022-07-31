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
package org.apache.rocketmq.client.impl.consumer;

import java.util.Set;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.common.protocol.body.ConsumerRunningInfo;
import org.apache.rocketmq.common.protocol.heartbeat.ConsumeType;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.common.protocol.heartbeat.SubscriptionData;

/**
 * Consumer inner interface
 * 消费者接口，一个消费者组一个对应的对象，
 */
public interface MQConsumerInner {

    /**
     * 获取消费者组的名称
     *
     * @return
     */
    String groupName();

    /**
     * 当前消费者组的消费模式 集群 or 广播
     *
     * @return
     */
    MessageModel messageModel();

    /**
     * 消息拉取模式 pull or push
     * @return
     */
    ConsumeType consumeType();

    /**
     * 这个消费者组从哪里开始消费
     *
     * @return
     */
    ConsumeFromWhere consumeFromWhere();

    /**
     * 订阅的 topic 的信息的封装，集合，因为一个消费者组会订阅很多topic
     * @return
     */
    Set<SubscriptionData> subscriptions();

    /**
     * 重平衡
     */
    void doRebalance();

    /**
     * 持久化消费者消费消息的offset
     */
    void persistConsumerOffset();

    /**
     * 更新 topic 的对应的队列信息 ，消息队列可能会随着broker的上线或者下线而变动
     *
     * @param topic
     * @param info
     */
    void updateTopicSubscribeInfo(final String topic, final Set<MessageQueue> info);

    /**
     * 判断某个订阅的 topic 的信息需不需要变动
     * @param topic
     * @return
     */
    boolean isSubscribeTopicNeedUpdate(final String topic);

    boolean isUnitMode();

    /**
     * 获取consumer的运行的信息
     *
     * @return
     */
    ConsumerRunningInfo consumerRunningInfo();
}
