/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * RelToken.java
 */

package com.sun.xml.ws.security.impl.policy;

import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.NestedPolicy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.security.policy.SecurityAssertionValidator;
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import static com.sun.xml.ws.security.impl.policy.Constants.*;

/**
 *
 * @author Mayank.Mishra@Sun.com
 */
public class RelToken extends PolicyAssertion implements com.sun.xml.ws.security.policy.RelToken, SecurityAssertionValidator {
    private String id;
    private List<String> tokenRefType;
    private boolean populated = false;
    private String tokenType;
    private PolicyAssertion rdKey = null;
    private AssertionFitness fitness = AssertionFitness.IS_VALID;

    //private boolean isServer = false;
    private SecurityPolicyVersion spVersion = SecurityPolicyVersion.SECURITYPOLICY200507;
    private final QName itQname;
    private String includeToken;
    
    /** Creates a new instance of RelToken */
    public RelToken(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {
        super(name,nestedAssertions,nestedAlternative);
        id= PolicyUtil.randomUUID();
        String nsUri = getName().getNamespaceURI();
        spVersion = PolicyUtil.getSecurityPolicyVersion(nsUri);
        itQname = new QName(spVersion.namespaceUri, Constants.IncludeToken);
        includeToken = spVersion.includeTokenAlways;
    }
    
    
    public String getTokenType() {
        populate();
        return tokenType;
    }
    
    public Iterator getTokenRefernceType() {
        if ( tokenRefType != null ) {
            return tokenRefType.iterator();
        } else {
            return Collections.emptyList().iterator();
        }
    }
    
    public boolean isRequireDerivedKeys() {
        populate();
        if (rdKey != null ) {
            return true;
        }
        return false;
    }
    
    public String getIncludeToken() {
        populate();
        return includeToken;
    }
    
    
    public String getTokenId() {
        return id;
    }
    
    
    public AssertionFitness validate(boolean isServer) {
        return populate(isServer);
    }
    private void populate(){
        populate(false);
    }
    
    private synchronized AssertionFitness populate(boolean isServer) {
        if(!populated){
            NestedPolicy policy = this.getNestedPolicy();
            includeToken = this.getAttributeValue(itQname);
            if(policy == null){
                if(logger.getLevel() == Level.FINE){
                    logger.log(Level.FINE,"NestedPolicy is null");
                }
                populated = true;
                return fitness;
            }
            AssertionSet as = policy.getAssertionSet();
            Iterator<PolicyAssertion> paItr = as.iterator();
            
            while(paItr.hasNext()){
                PolicyAssertion assertion  = paItr.next();
                if(PolicyUtil.isRelTokenType(assertion, spVersion)){
                    tokenType = assertion.getName().getLocalPart().intern();
                }else if(PolicyUtil.isRequireDerivedKeys(assertion, spVersion)){
                    rdKey = assertion;
                }else if(PolicyUtil.isRequireKeyIR(assertion, spVersion)){
                    if(tokenRefType == null){
                        tokenRefType = new ArrayList<String>();
                    }
                    tokenRefType.add(assertion.getName().getLocalPart().intern());
                } else{
                    if(!assertion.isOptional()){
                        
                        log_invalid_assertion(assertion, isServer,RelToken);
                        fitness = AssertionFitness.HAS_UNKNOWN_ASSERTION;
                    }
                }
            }
            populated = true;
        }
        return fitness;
    }

    public SecurityPolicyVersion getSecurityPolicyVersion() {
        return spVersion;
    }
    
}
