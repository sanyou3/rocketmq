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
package org.apache.rocketmq.broker.transaction;

import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.header.EndTransactionRequestHeader;
import org.apache.rocketmq.store.MessageExtBrokerInner;
import org.apache.rocketmq.store.PutMessageResult;
import java.util.concurrent.CompletableFuture;

public interface TransactionalMessageService {

    /**
     * Process prepare message, in common, we should put this message to storage service.
     * 投递事务消息，其实就是将事务消息存在对应的事务消息的topic中
     *
     * @param messageInner Prepare(Half) message.
     * @return Prepare message storage result.
     */
    PutMessageResult prepareMessage(MessageExtBrokerInner messageInner);

    /**
     * Process prepare message in async manner, we should put this message to storage service
     * 异步投递
     * @param messageInner Prepare(Half) message.
     * @return CompletableFuture of put result, will be completed at put success(flush and replica done)
     */
    CompletableFuture<PutMessageResult> asyncPrepareMessage(MessageExtBrokerInner messageInner);

    /**
     * Delete prepare message when this message has been committed or rolled back.
     * 删除已经提交或者回滚的消息，但是其实并没有真正的删除，只是将事务消息重新写到 RMQ_SYS_TRANS_OP_HALF_TOPIC 中..
     * @param messageExt
     */
    boolean deletePrepareMessage(MessageExt messageExt);

    /**
     * Invoked to process commit prepare message.
     * 提交事务消息，其实本质就是查询一个消息，内部并没有什么复杂的逻辑操作，仅仅查询
     *
     * @param requestHeader Commit message request header.
     * @return Operate result contains prepare message and relative error code.
     */
    OperationResult commitMessage(EndTransactionRequestHeader requestHeader);

    /**
     * Invoked to roll back prepare message.
     * 提交事务消息，其实本质就是查询一个消息，内部并没有什么复杂的逻辑操作，仅仅查询，跟 commitMessage 的实现逻辑一模一样
     *
     * @param requestHeader Prepare message request header.
     * @return Operate result contains prepare message and relative error code.
     */
    OperationResult rollbackMessage(EndTransactionRequestHeader requestHeader);

    /**
     * Traverse uncommitted/unroll back half message and send check back request to producer to obtain transaction
     * status.
     *
     * @param transactionTimeout The minimum time of the transactional message to be checked firstly, one message only
     * exceed this time interval that can be checked.
     * @param transactionCheckMax The maximum number of times the message was checked, if exceed this value, this
     * message will be discarded.
     * @param listener When the message is considered to be checked or discarded, the relative method of this class will
     * be invoked.
     */
    void check(long transactionTimeout, int transactionCheckMax, AbstractTransactionalMessageCheckListener listener);

    /**
     * Open transaction service.
     *
     * @return If open success, return true.
     */
    boolean open();

    /**
     * Close transaction service.
     */
    void close();
}
