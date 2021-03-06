/*
 * Copyright 2006 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.ole.coa.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.ole.coa.businessobject.ObjectConsolidation;
import org.kuali.ole.coa.service.ObjectConsService;
import org.kuali.ole.sys.OLEPropertyConstants;
import org.kuali.ole.sys.context.SpringContext;
import org.kuali.ole.sys.service.NonTransactional;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This service implementation is the default implementation of the BalanceTyp service that is delivered with Kuali.
 */

@NonTransactional
public class ObjectConsServiceImpl implements ObjectConsService {

    /**
     * @see org.kuali.ole.coa.service.ObjectConsService#getByPrimaryId(java.lang.String, java.lang.String)
     */
    public ObjectConsolidation getByPrimaryId(String chartOfAccountsCode, String objectConsCode) {
        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put(OLEPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        keys.put(OLEPropertyConstants.FIN_CONSOLIDATION_OBJECT_CODE, objectConsCode);
        return (ObjectConsolidation)SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(ObjectConsolidation.class, keys);
    }


}
