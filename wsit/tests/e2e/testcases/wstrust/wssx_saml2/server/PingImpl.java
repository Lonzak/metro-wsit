/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package wstrust.wssx_saml2.server;


@javax.jws.WebService (endpointInterface="wstrust.wssx_saml2.server.IPingServiceContract")
//@javax.xml.ws.BindingType(javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)
public class PingImpl implements IPingServiceContract {

   public String ping(String message) {
        System.out.println("The message is here : " + message);
        return message;
    }
}

