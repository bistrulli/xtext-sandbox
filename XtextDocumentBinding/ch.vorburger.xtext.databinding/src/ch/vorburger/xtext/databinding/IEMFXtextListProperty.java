/*******************************************************************************
 * Copyright (c) 2012 Michael Vorburger (http://www.vorburger.ch).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package ch.vorburger.xtext.databinding;

import org.eclipse.emf.databinding.IEMFListProperty;

/**
 * Like IEMFEditListProperty, but using an IXtextResourceReadWriteAccess instead of an EditingDomain.
 *
 * @author Michael Vorburger
 */
public interface IEMFXtextListProperty extends IEMFXtextProperty, IEMFListProperty {

	  IEMFXtextListProperty values(IEMFXtextValueProperty property);

}
