// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state.dynamodb;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import org.junit.Before;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;

import io.vlingo.actors.Definition;
import io.vlingo.actors.Protocols;
import io.vlingo.actors.World;
import io.vlingo.symbio.State.BinaryState;
import io.vlingo.symbio.store.state.Entity1;
import io.vlingo.symbio.store.state.StateStore;
import io.vlingo.symbio.store.state.Entity1.Entity1BinaryStateAdapter;
import io.vlingo.symbio.store.state.dynamodb.adapters.BinaryStateRecordAdapter;
import io.vlingo.symbio.store.state.dynamodb.adapters.RecordAdapter;
import io.vlingo.symbio.store.state.dynamodb.interests.CreateTableInterest;

public class DynamoDBBinaryStateActorTest extends DynamoDBStateActorTest<BinaryState> {
    @Before
    public void setUp() {
      super.setUp();
      final Entity1BinaryStateAdapter adapter = new Entity1BinaryStateAdapter();
      stateStore.registerAdapter(Entity1.class, adapter);
      adapterAssistant.registerAdapter(Entity1.class, adapter);
    }

    @Override
    protected Protocols stateStoreProtocols(World world, StateStore.Dispatcher dispatcher, AmazonDynamoDBAsync dynamodb, CreateTableInterest interest) {
        return world.actorFor(
                Definition.has(DynamoDBStateActor.class, Definition.parameters(dispatcher, dynamodb, interest, new BinaryStateRecordAdapter())),
                new Class[]{StateStore.class, StateStore.DispatcherControl.class}
        );
    }

    @Override
    protected void verifyDispatched(StateStore.Dispatcher dispatcher, String id, StateStore.Dispatchable<BinaryState> dispatchable) {
        verify(dispatcher).dispatch(dispatchable.id, dispatchable.state.asBinaryState());
    }

    @Override
    protected void verifyDispatched(StateStore.Dispatcher dispatcher, String id, BinaryState state) {
        verify(dispatcher, timeout(DEFAULT_TIMEOUT)).dispatch(id, state.asBinaryState());
    }

    @Override
    protected RecordAdapter<BinaryState> recordAdapter() {
        return new BinaryStateRecordAdapter();
    }
}