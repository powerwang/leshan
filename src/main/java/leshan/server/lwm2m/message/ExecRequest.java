/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package leshan.server.lwm2m.message;

/**
 * The request to send an Execute to a client object resource instance.
 */
public class ExecRequest {

    private final int objectId;

    private final int objectInstanceId;

    private final int resourceId;

    public ExecRequest(int objectId, int objectInstanceId, int resourceId) {
        this.objectId = objectId;
        this.objectInstanceId = objectInstanceId;
        this.resourceId = resourceId;
    }

    public int getObjectId() {
        return objectId;
    }

    public int getObjectInstanceId() {
        return objectInstanceId;
    }

    public int getResourceId() {
        return resourceId;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ExecRequest [objectId=").append(objectId).append(", objectInstanceId=")
                .append(objectInstanceId).append(", resourceId=").append(resourceId).append("]");
        return builder.toString();
    }

}