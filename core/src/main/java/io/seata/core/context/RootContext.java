/*
 *  Copyright 1999-2019 Seata.io Group.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.seata.core.context;

import io.seata.common.exception.ShouldNeverHappenException;
import io.seata.common.util.StringUtils;
import io.seata.core.model.BranchType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * The type Root context.
 *
 * @author slievrly
 */
public class RootContext {

    private RootContext() {

    }

    private static final Logger LOGGER = LoggerFactory.getLogger(RootContext.class);

    /**
     * The constant KEY_XID.
     */
    public static final String KEY_XID = "TX_XID";

    public static final String KEY_BRANCH_TYPE = "BRANCH_TYPE";

    public static final String KEY_GLOBAL_LOCK_FLAG = "TX_LOCK";

    private static ContextCore CONTEXT_HOLDER = ContextCoreLoader.load();

    /**
     * Gets xid.
     *
     * @return the xid
     */
    public static String getXID() {
        String xid = CONTEXT_HOLDER.get(KEY_XID);
        if (StringUtils.isNotBlank(xid)) {
            return xid;
        }
        return null;
    }

    /**
     * Gets the current branchType
     *
     * @return the xid
     */
    public static String getBranchType() {
        return CONTEXT_HOLDER.get(KEY_BRANCH_TYPE);
    }

    /**
     * Bind.
     *
     * @param xid the xid
     */
    public static void bind(String xid) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("bind {}", xid);
        }
        CONTEXT_HOLDER.put(KEY_XID, xid);
    }

    /**
     * Bind the current branchType
     *
     * @param branchType
     */
    public static void bindBranchType(BranchType branchType) {
        CONTEXT_HOLDER.put(KEY_BRANCH_TYPE, String.valueOf(branchType.ordinal()));
    }

    /**
     * declare local transactions will use global lock check for update/delete/insert/selectForUpdate SQL
     */
    public static void bindGlobalLockFlag() {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Local Transaction Global Lock support enabled");
        }

        //just put something not null
        CONTEXT_HOLDER.put(KEY_GLOBAL_LOCK_FLAG, KEY_GLOBAL_LOCK_FLAG);
    }

    /**
     * Unbind string.
     *
     * @return the string
     */
    public static String unbind() {
        String xid = CONTEXT_HOLDER.remove(KEY_XID);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("unbind {} ", xid);
        }
        return xid;
    }

    /**
     * Unbind branchType
     *
     * @return the string
     */
    public static String unbindBranchType() {
        String branchType = CONTEXT_HOLDER.remove(KEY_BRANCH_TYPE);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("unbind {} ", branchType);
        }
        return branchType;
    }

    public static void unbindGlobalLockFlag() {
        String lockFlag = CONTEXT_HOLDER.remove(KEY_GLOBAL_LOCK_FLAG);
        if (LOGGER.isDebugEnabled() && lockFlag != null) {
            LOGGER.debug("unbind global lock flag");
        }
    }

    /**
     * In global transaction boolean.
     *
     * @return the boolean
     */
    public static boolean inGlobalTransaction() {
        return CONTEXT_HOLDER.get(KEY_XID) != null;
    }

    /**
     * requires global lock check
     *
     * @return
     */
    public static boolean requireGlobalLock() {
        return CONTEXT_HOLDER.get(KEY_GLOBAL_LOCK_FLAG) != null;
    }

    /**
     * Assert not in global transaction.
     */
    public static void assertNotInGlobalTransaction() {
        if (inGlobalTransaction()) {
            throw new ShouldNeverHappenException();
        }
    }

    /**
     * entry map
     *
     * @return
     */
    public static Map<String, String> entries() {
        return CONTEXT_HOLDER.entries();
    }
}
