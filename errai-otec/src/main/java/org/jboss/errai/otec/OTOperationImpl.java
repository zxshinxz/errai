/*
 * Copyright 2013 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.otec;

import java.util.Arrays;
import java.util.List;

/**
 * @author Christian Sadilek
 * @author Mike Brock
 */
public class OTOperationImpl implements OTOperation {
  private final List<Mutation> mutations;
  private final Integer entityId;
  private final Integer revision;
  private final boolean propagate;

  private OTOperationImpl(final List<Mutation> mutationList, final Integer entityId, final Integer revision, final boolean propagate) {
    this.mutations = mutationList;
    this.entityId = entityId;
    this.revision = revision;
    this.propagate = propagate;
  }

  public static OTOperation createOperation(final List<Mutation> mutationList, final Integer entityId, final Integer revision) {
    return new OTOperationImpl(mutationList, entityId, revision, true);
  }

  public static OTOperation createLocalOnlyOperation(final List<Mutation> mutationList, final Integer entityId, final Integer revision) {
    return new OTOperationImpl(mutationList, entityId, revision, false);
  }

  public static OTOperation createLocalOnlyOperation(final OTOperation operation) {
    return new OTOperationImpl(operation.getMutations(), operation.getEntityId(), operation.getRevision(), false);
  }


  @Override
  public List<Mutation> getMutations() {
    return mutations;
  }

  @Override
  public Integer getEntityId() {
    return entityId;
  }

  @Override
  public Integer getRevision() {
    return revision;
  }

  @Override
  public boolean apply(final OTEntity entity) {
    for (final Mutation mutation : mutations) {
      mutation.apply(entity.getState());
    }
    entity.incrementRevision();
    return shouldPropagate();
  }

  @Override
  public boolean shouldPropagate() {
    return propagate;
  }

  public String toString() {
    return Arrays.toString(mutations.toArray());
  }
}
